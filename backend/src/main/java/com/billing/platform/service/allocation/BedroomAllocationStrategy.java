package com.billing.platform.service.allocation;

import com.billing.platform.entity.AllocationRule;
import com.billing.platform.entity.Unit;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component("BEDROOM")
public class BedroomAllocationStrategy implements AllocationStrategy {

    @Override
    public Map<Long, BigDecimal> allocate(BigDecimal totalBillAmount, List<Unit> occupiedUnits,
                                          AllocationRule rule, LocalDate periodStart, LocalDate periodEnd,
                                          Map<Long, BigDecimal> consumptionByUnitId) {
        Map<Long, BigDecimal> weights = new LinkedHashMap<>();
        for (Unit unit : occupiedUnits) {
            int bedrooms = unit.getBedroomCount() != null ? unit.getBedroomCount() : 0;
            weights.put(unit.getId(), BigDecimal.valueOf(bedrooms));
        }
        return AllocationMathUtil.distributeByWeights(totalBillAmount, weights);
    }
}