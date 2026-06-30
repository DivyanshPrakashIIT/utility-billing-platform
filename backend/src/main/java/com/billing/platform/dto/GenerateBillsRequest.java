package com.billing.platform.dto;

public class GenerateBillsRequest {
    private Long buildingId;
    private Long allocationRuleId;

    public Long getBuildingId() { return buildingId; }
    public void setBuildingId(Long buildingId) { this.buildingId = buildingId; }

    public Long getAllocationRuleId() { return allocationRuleId; }
    public void setAllocationRuleId(Long allocationRuleId) { this.allocationRuleId = allocationRuleId; }
}