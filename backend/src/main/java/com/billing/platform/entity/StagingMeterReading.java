package com.billing.platform.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "staging_meter_reading")
public class StagingMeterReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "import_batch_id", nullable = false)
    private Long importBatchId;

    @Column(name = "row_number", nullable = false)
    private Integer rowNumber;

    @Column(name = "meter_type_raw", length = 20)
    private String meterTypeRaw;

    @Column(name = "meter_id_raw", length = 50)
    private String meterIdRaw;

    @Column(name = "reading_date_raw", length = 50)
    private String readingDateRaw;

    @Column(name = "reading_value_raw", length = 50)
    private String readingValueRaw;

    @Column(name = "is_valid", nullable = false)
    private boolean valid = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getImportBatchId() { return importBatchId; }
    public void setImportBatchId(Long importBatchId) { this.importBatchId = importBatchId; }

    public Integer getRowNumber() { return rowNumber; }
    public void setRowNumber(Integer rowNumber) { this.rowNumber = rowNumber; }

    public String getMeterTypeRaw() { return meterTypeRaw; }
    public void setMeterTypeRaw(String meterTypeRaw) { this.meterTypeRaw = meterTypeRaw; }

    public String getMeterIdRaw() { return meterIdRaw; }
    public void setMeterIdRaw(String meterIdRaw) { this.meterIdRaw = meterIdRaw; }

    public String getReadingDateRaw() { return readingDateRaw; }
    public void setReadingDateRaw(String readingDateRaw) { this.readingDateRaw = readingDateRaw; }

    public String getReadingValueRaw() { return readingValueRaw; }
    public void setReadingValueRaw(String readingValueRaw) { this.readingValueRaw = readingValueRaw; }

    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
}