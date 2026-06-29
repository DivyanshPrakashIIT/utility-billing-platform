package com.billing.platform.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public class AllocationPreviewRequest {
    private Long buildingId;
    private Long allocationRuleId;
    private BigDecimal totalBillAmount;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private Map<Long, BigDecimal> consumptionByUnitId;

    public Long getBuildingId() { return buildingId; }
    public void setBuildingId(Long buildingId) { this.buildingId = buildingId; }

    public Long getAllocationRuleId() { return allocationRuleId; }
    public void setAllocationRuleId(Long allocationRuleId) { this.allocationRuleId = allocationRuleId; }

    public BigDecimal getTotalBillAmount() { return totalBillAmount; }
    public void setTotalBillAmount(BigDecimal totalBillAmount) { this.totalBillAmount = totalBillAmount; }

    public LocalDate getPeriodStart() { return periodStart; }
    public void setPeriodStart(LocalDate periodStart) { this.periodStart = periodStart; }

    public LocalDate getPeriodEnd() { return periodEnd; }
    public void setPeriodEnd(LocalDate periodEnd) { this.periodEnd = periodEnd; }

    public Map<Long, BigDecimal> getConsumptionByUnitId() { return consumptionByUnitId; }
    public void setConsumptionByUnitId(Map<Long, BigDecimal> consumptionByUnitId) { this.consumptionByUnitId = consumptionByUnitId; }
}