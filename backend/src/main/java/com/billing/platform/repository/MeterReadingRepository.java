package com.billing.platform.repository;

import com.billing.platform.entity.MeterReading;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MeterReadingRepository extends JpaRepository<MeterReading, Long> {
    List<MeterReading> findBySubMeterId(Long subMeterId);
    List<MeterReading> findByMasterMeterId(Long masterMeterId);
}
