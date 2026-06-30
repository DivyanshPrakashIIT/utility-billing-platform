package com.billing.platform.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "unit_consumption")
public class UnitConsumption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "unit_id", nullable = false)
    private Long unitId;

    @Column(name = "provider_bill_id", nullable = false)
    private Long providerBillId;

    @Column(name = "consumption_value", precision = 14, scale = 3)
    private BigDecimal consumptionValue;

    @Column(name = "allocation_rule_id", nullable = false)
    private Long allocationRuleId;

    @Column(name = "allocation_weight", nullable = false, precision = 10, scale = 6)
    private BigDecimal allocationWeight;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUnitId() { return unitId; }
    public void setUnitId(Long unitId) { this.unitId = unitId; }

    public Long getProviderBillId() { return providerBillId; }
    public void setProviderBillId(Long providerBillId) { this.providerBillId = providerBillId; }

    public BigDecimal getConsumptionValue() { return consumptionValue; }
    public void setConsumptionValue(BigDecimal consumptionValue) { this.consumptionValue = consumptionValue; }

    public Long getAllocationRuleId() { return allocationRuleId; }
    public void setAllocationRuleId(Long allocationRuleId) { this.allocationRuleId = allocationRuleId; }

    public BigDecimal getAllocationWeight() { return allocationWeight; }
    public void setAllocationWeight(BigDecimal allocationWeight) { this.allocationWeight = allocationWeight; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
}