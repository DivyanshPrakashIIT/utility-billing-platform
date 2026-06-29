package com.billing.platform.service.allocation;

import com.billing.platform.entity.AllocationRule;
import com.billing.platform.entity.Unit;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface AllocationStrategy {

    Map<Long, BigDecimal> allocate(BigDecimal totalBillAmount,
                                   List<Unit> occupiedUnits,
                                   AllocationRule rule,
                                   LocalDate periodStart,
                                   LocalDate periodEnd,
                                   Map<Long, BigDecimal> consumptionByUnitId);
}