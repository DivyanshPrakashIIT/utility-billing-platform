package com.billing.platform.repository;

import com.billing.platform.entity.SubMeter;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SubMeterRepository extends JpaRepository<SubMeter, Long> {
    List<SubMeter> findByUnitId(Long unitId);
    List<SubMeter> findByUtilityId(Long utilityId);
}