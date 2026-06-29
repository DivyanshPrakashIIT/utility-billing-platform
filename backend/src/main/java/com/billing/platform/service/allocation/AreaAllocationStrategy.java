package com.billing.platform.service.allocation;

import com.billing.platform.entity.AllocationRule;
import com.billing.platform.entity.Unit;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component("AREA")
public class AreaAllocationStrategy implements AllocationStrategy {

    @Override
    public Map<Long, BigDecimal> allocate(BigDecimal totalBillAmount, List<Unit> occupiedUnits,
                                          AllocationRule rule, LocalDate periodStart, LocalDate periodEnd,
                                          Map<Long, BigDecimal> consumptionByUnitId) {
        Map<Long, BigDecimal> weights = new LinkedHashMap<>();
        for (Unit unit : occupiedUnits) {
            BigDecimal area = unit.getFloorAreaSqft() != null ? unit.getFloorAreaSqft() : BigDecimal.ZERO;
            weights.put(unit.getId(), area);
        }
        return AllocationMathUtil.distributeByWeights(totalBillAmount, weights);
    }
}