package com.billing.platform.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "allocation_history")
public class AllocationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "provider_bill_id", nullable = false)
    private Long providerBillId;

    @Column(name = "allocation_rule_id", nullable = false)
    private Long allocationRuleId;

    @Column(name = "snapshot_json", nullable = false, columnDefinition = "TEXT")
    private String snapshotJson;

    @Column(name = "executed_at", nullable = false, updatable = false)
    private OffsetDateTime executedAt;

    @PrePersist
    protected void onCreate() {
        executedAt = OffsetDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProviderBillId() { return providerBillId; }
    public void setProviderBillId(Long providerBillId) { this.providerBillId = providerBillId; }

    public Long getAllocationRuleId() { return allocationRuleId; }
    public void setAllocationRuleId(Long allocationRuleId) { this.allocationRuleId = allocationRuleId; }

    public String getSnapshotJson() { return snapshotJson; }
    public void setSnapshotJson(String snapshotJson) { this.snapshotJson = snapshotJson; }

    public OffsetDateTime getExecutedAt() { return executedAt; }
}