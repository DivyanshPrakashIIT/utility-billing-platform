package com.billing.platform.service.allocation;

import com.billing.platform.entity.AllocationRule;
import com.billing.platform.entity.Unit;
import tools.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component("FIXED_PERCENTAGE")
public class FixedPercentageAllocationStrategy implements AllocationStrategy {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @SuppressWarnings("unchecked")
    public Map<Long, BigDecimal> allocate(BigDecimal totalBillAmount, List<Unit> occupiedUnits,
                                          AllocationRule rule, LocalDate periodStart, LocalDate periodEnd,
                                          Map<Long, BigDecimal> consumptionByUnitId) {
        if (rule.getParametersJson() == null) {
            throw new IllegalArgumentException("FIXED_PERCENTAGE method requires parameters_json with unitId->percentage map");
        }

        Map<String, Object> rawMap;
        try {
            rawMap = objectMapper.readValue(rule.getParametersJson(), Map.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid parameters_json: " + e.getMessage());
        }

        Map<Long, BigDecimal> percentages = new LinkedHashMap<>();
        BigDecimal percentageSum = BigDecimal.ZERO;
        for (Map.Entry<String, Object> entry : rawMap.entrySet()) {
            Long unitId = Long.parseLong(entry.getKey());
            BigDecimal pct = new BigDecimal(entry.getValue().toString());
            percentages.put(unitId, pct);
            percentageSum = percentageSum.add(pct);
        }

        BigDecimal tolerance = new BigDecimal("0.0001");
        if (percentageSum.subtract(BigDecimal.ONE).abs().compareTo(tolerance) > 0) {
            throw new IllegalArgumentException("Fixed percentages must sum to 1.0 (got " + percentageSum + ")");
        }

        Map<Long, BigDecimal> weights = new LinkedHashMap<>();
        for (Unit unit : occupiedUnits) {
            weights.put(unit.getId(), percentages.getOrDefault(unit.getId(), BigDecimal.ZERO));
        }
        return AllocationMathUtil.distributeByWeights(totalBillAmount, weights);
    }
}