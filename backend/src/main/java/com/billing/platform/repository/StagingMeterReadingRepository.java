package com.billing.platform.repository;

import com.billing.platform.entity.StagingMeterReading;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StagingMeterReadingRepository extends JpaRepository<StagingMeterReading, Long> {
    List<StagingMeterReading> findByImportBatchIdAndValid(Long importBatchId, boolean valid);
}