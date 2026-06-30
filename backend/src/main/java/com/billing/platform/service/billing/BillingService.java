package com.billing.platform.service.billing;

import com.billing.platform.entity.AllocationHistory;
import com.billing.platform.entity.AllocationRule;
import com.billing.platform.entity.MeterReading;
import com.billing.platform.entity.SubMeter;
import com.billing.platform.entity.Unit;
import com.billing.platform.entity.UnitBill;
import com.billing.platform.entity.UnitConsumption;
import com.billing.platform.entity.UtilityProviderBill;
import com.billing.platform.repository.AllocationHistoryRepository;
import com.billing.platform.repository.AllocationRuleRepository;
import com.billing.platform.repository.MeterReadingRepository;
import com.billing.platform.repository.SubMeterRepository;
import com.billing.platform.repository.UnitBillRepository;
import com.billing.platform.repository.UnitConsumptionRepository;
import com.billing.platform.repository.UnitRepository;
import com.billing.platform.repository.UtilityProviderBillRepository;
import com.billing.platform.service.allocation.AllocationStrategy;
import com.billing.platform.service.allocation.AllocationStrategyFactory;
import tools.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BillingService {

    private final UtilityProviderBillRepository providerBillRepository;
    private final UnitRepository unitRepository;
    private final AllocationRuleRepository allocationRuleRepository;
    private final AllocationStrategyFactory strategyFactory;
    private final SubMeterRepository subMeterRepository;
    private final MeterReadingRepository meterReadingRepository;
    private final UnitConsumptionRepository unitConsumptionRepository;
    private final UnitBillRepository unitBillRepository;
    private final AllocationHistoryRepository allocationHistoryRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public BillingService(UtilityProviderBillRepository providerBillRepository,
                          UnitRepository unitRepository,
                          AllocationRuleRepository allocationRuleRepository,
                          AllocationStrategyFactory strategyFactory,
                          SubMeterRepository subMeterRepository,
                          MeterReadingRepository meterReadingRepository,
                          UnitConsumptionRepository unitConsumptionRepository,
                          UnitBillRepository unitBillRepository,
                          AllocationHistoryRepository allocationHistoryRepository) {
        this.providerBillRepository = providerBillRepository;
        this.unitRepository = unitRepository;
        this.allocationRuleRepository = allocationRuleRepository;
        this.strategyFactory = strategyFactory;
        this.subMeterRepository = subMeterRepository;
        this.meterReadingRepository = meterReadingRepository;
        this.unitConsumptionRepository = unitConsumptionRepository;
        this.unitBillRepository = unitBillRepository;
        this.allocationHistoryRepository = allocationHistoryRepository;
    }

    @Transactional
    public List<UnitBill> generateBills(Long providerBillId, Long buildingId, Long allocationRuleId) {

        UtilityProviderBill providerBill = providerBillRepository.findById(providerBillId)
                .orElseThrow(() -> new IllegalArgumentException("Provider bill not found: " + providerBillId));

        if ("FINALIZED".equals(providerBill.getStatus())) {
            throw new IllegalStateException("Provider bill " + providerBillId + " is already finalized and cannot be regenerated");
        }

        AllocationRule rule = allocationRuleRepository.findById(allocationRuleId)
                .orElseThrow(() -> new IllegalArgumentException("Allocation rule not found: " + allocationRuleId));

        List<Unit> occupiedUnits = unitRepository.findByBuildingId(buildingId)
                .stream()
                .filter(Unit::isActive)
                .collect(Collectors.toList());

        if (occupiedUnits.isEmpty()) {
            throw new IllegalStateException("No active units found for building " + buildingId);
        }

        // For CONSUMPTION method, calculate real per-unit consumption from sub-meter readings
        Map<Long, BigDecimal> consumptionByUnitId = null;
        if ("CONSUMPTION".equals(rule.getMethod())) {
            consumptionByUnitId = calculateConsumptionFromMeterReadings(
                    occupiedUnits, rule.getUtilityId(), providerBill.getBillingPeriodStart(), providerBill.getBillingPeriodEnd());
        }

        AllocationStrategy strategy = strategyFactory.get(rule.getMethod());

        Map<Long, BigDecimal> allocationResult = strategy.allocate(
                providerBill.getTotalAmount(),
                occupiedUnits,
                rule,
                providerBill.getBillingPeriodStart(),
                providerBill.getBillingPeriodEnd(),
                consumptionByUnitId
        );

        // Save UNIT_CONSUMPTION rows (audit detail of weight used per unit)
        BigDecimal weightSum = BigDecimal.ZERO;
        for (Map.Entry<Long, BigDecimal> entry : allocationResult.entrySet()) {
            weightSum = weightSum.add(entry.getValue());
        }

        for (Map.Entry<Long, BigDecimal> entry : allocationResult.entrySet()) {
            UnitConsumption uc = new UnitConsumption();
            uc.setUnitId(entry.getKey());
            uc.setProviderBillId(providerBillId);
            uc.setAllocationRuleId(allocationRuleId);
            if (consumptionByUnitId != null) {
                uc.setConsumptionValue(consumptionByUnitId.getOrDefault(entry.getKey(), BigDecimal.ZERO));
            }
            BigDecimal weight = weightSum.compareTo(BigDecimal.ZERO) > 0
                    ? entry.getValue().divide(providerBill.getTotalAmount(), 6, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;
            uc.setAllocationWeight(weight);
            unitConsumptionRepository.save(uc);
        }

        // Save UNIT_BILL rows
        List<UnitBill> generatedBills = new java.util.ArrayList<>();
        for (Map.Entry<Long, BigDecimal> entry : allocationResult.entrySet()) {
            UnitBill bill = new UnitBill();
            bill.setUnitId(entry.getKey());
            bill.setProviderBillId(providerBillId);
            bill.setAmountDue(entry.getValue());
            bill.setStatus("UNPAID");
            generatedBills.add(unitBillRepository.save(bill));
        }

        // Save ALLOCATION_HISTORY snapshot
        try {
            Map<String, Object> snapshot = new LinkedHashMap<>();
            snapshot.put("providerBillId", providerBillId);
            snapshot.put("allocationRuleId", allocationRuleId);
            snapshot.put("method", rule.getMethod());
            snapshot.put("totalBillAmount", providerBill.getTotalAmount());
            snapshot.put("allocationResult", allocationResult);
            if (consumptionByUnitId != null) {
                snapshot.put("consumptionByUnitId", consumptionByUnitId);
            }

            AllocationHistory history = new AllocationHistory();
            history.setProviderBillId(providerBillId);
            history.setAllocationRuleId(allocationRuleId);
            history.setSnapshotJson(objectMapper.writeValueAsString(snapshot));
            allocationHistoryRepository.save(history);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to save allocation history snapshot: " + e.getMessage());
        }

        // Finalize the provider bill
        providerBill.setStatus("FINALIZED");
        providerBillRepository.save(providerBill);

        return generatedBills;
    }

    private Map<Long, BigDecimal> calculateConsumptionFromMeterReadings(List<Unit> units, Long utilityId,
                                                                        java.time.LocalDate periodStart,
                                                                        java.time.LocalDate periodEnd) {
        Map<Long, BigDecimal> consumptionByUnitId = new LinkedHashMap<>();

        for (Unit unit : units) {
            List<SubMeter> subMeters = subMeterRepository.findByUnitId(unit.getId())
                    .stream()
                    .filter(sm -> sm.getUtilityId().equals(utilityId))
                    .collect(Collectors.toList());

            BigDecimal totalConsumption = BigDecimal.ZERO;

            for (SubMeter subMeter : subMeters) {
                List<MeterReading> readings = meterReadingRepository.findBySubMeterId(subMeter.getId())
                        .stream()
                        .sorted((a, b) -> a.getReadingDate().compareTo(b.getReadingDate()))
                        .collect(Collectors.toList());

                MeterReading earliest = null;
                MeterReading latest = null;
                for (MeterReading reading : readings) {
                    if (!reading.getReadingDate().isBefore(periodStart) && !reading.getReadingDate().isAfter(periodEnd)) {
                        if (earliest == null) earliest = reading;
                        latest = reading;
                    }
                }

                if (earliest != null && latest != null && !earliest.getId().equals(latest.getId())) {
                    BigDecimal delta = latest.getReadingValue().subtract(earliest.getReadingValue());
                    if (delta.compareTo(BigDecimal.ZERO) > 0) {
                        totalConsumption = totalConsumption.add(delta);
                    }
                } else if (latest != null) {
                    // Only one reading in period - use the value itself as a fallback
                    totalConsumption = totalConsumption.add(latest.getReadingValue());
                }
            }

            consumptionByUnitId.put(unit.getId(), totalConsumption);
        }

        return consumptionByUnitId;
    }
}