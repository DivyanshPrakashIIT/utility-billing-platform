package com.billing.platform.service.allocation;

import com.billing.platform.entity.AllocationRule;
import com.billing.platform.entity.Unit;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component("CONSUMPTION")
public class ConsumptionAllocationStrategy implements AllocationStrategy {

    @Override
    public Map<Long, BigDecimal> allocate(BigDecimal totalBillAmount, List<Unit> occupiedUnits,
                                          AllocationRule rule, LocalDate periodStart, LocalDate periodEnd,
                                          Map<Long, BigDecimal> consumptionByUnitId) {
        if (consumptionByUnitId == null || consumptionByUnitId.isEmpty()) {
            throw new IllegalArgumentException("Consumption data is required for CONSUMPTION allocation method");
        }

        Map<Long, BigDecimal> weights = new LinkedHashMap<>();
        for (Unit unit : occupiedUnits) {
            BigDecimal consumption = consumptionByUnitId.getOrDefault(unit.getId(), BigDecimal.ZERO);
            weights.put(unit.getId(), consumption);
        }
        return AllocationMathUtil.distributeByWeights(totalBillAmount, weights);
    }
}