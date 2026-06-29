package com.billing.platform.service.allocation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.Map;

public class AllocationMathUtil {

    public static Map<Long, BigDecimal> distributeByWeights(BigDecimal totalBillAmount, Map<Long, BigDecimal> weights) {
        BigDecimal weightSum = weights.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);

        if (weightSum.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalStateException("Cannot allocate: total weight is zero (no eligible units found)");
        }

        Map<Long, BigDecimal> result = new LinkedHashMap<>();
        BigDecimal runningTotal = BigDecimal.ZERO;
        Long largestWeightUnitId = null;
        BigDecimal largestWeight = BigDecimal.valueOf(-1);

        for (Map.Entry<Long, BigDecimal> entry : weights.entrySet()) {
            BigDecimal normalizedWeight = entry.getValue().divide(weightSum, 10, RoundingMode.HALF_UP);
            BigDecimal amount = totalBillAmount.multiply(normalizedWeight).setScale(2, RoundingMode.HALF_UP);
            result.put(entry.getKey(), amount);
            runningTotal = runningTotal.add(amount);

            if (entry.getValue().compareTo(largestWeight) > 0) {
                largestWeight = entry.getValue();
                largestWeightUnitId = entry.getKey();
            }
        }

        BigDecimal difference = totalBillAmount.subtract(runningTotal);
        if (difference.compareTo(BigDecimal.ZERO) != 0 && largestWeightUnitId != null) {
            result.put(largestWeightUnitId, result.get(largestWeightUnitId).add(difference));
        }

        return result;
    }
}