package com.billing.platform.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "meter_reading")
public class MeterReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sub_meter_id")
    private Long subMeterId;

    @Column(name = "master_meter_id")
    private Long masterMeterId;


    @Column(name = "import_batch_id")
    private Long importBatchId;

    @Column(name = "reading_date", nullable = false)
    private LocalDate readingDate;

    @Column(name = "reading_value", nullable = false, precision = 14, scale = 3)
    private BigDecimal readingValue;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSubMeterId() { return subMeterId; }
    public void setSubMeterId(Long subMeterId) { this.subMeterId = subMeterId; }

    public Long getMasterMeterId() { return masterMeterId; }
    public void setMasterMeterId(Long masterMeterId) { this.masterMeterId = masterMeterId; }

    public Long getImportBatchId() {
        return importBatchId;
    }

    public void setImportBatchId(Long importBatchId) {
        this.importBatchId = importBatchId;
    }
    
    public LocalDate getReadingDate() { return readingDate; }
    public void setReadingDate(LocalDate readingDate) { this.readingDate = readingDate; }

    public BigDecimal getReadingValue() { return readingValue; }
    public void setReadingValue(BigDecimal readingValue) { this.readingValue = readingValue; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
}
