package com.billing.platform.repository;

import com.billing.platform.entity.ErrorLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ErrorLogRepository extends JpaRepository<ErrorLog, Long> {
    List<ErrorLog> findByImportBatchId(Long importBatchId);
}