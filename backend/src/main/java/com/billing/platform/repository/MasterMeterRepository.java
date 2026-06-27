package com.billing.platform.repository;

import com.billing.platform.entity.MasterMeter;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MasterMeterRepository extends JpaRepository<MasterMeter, Long> {
    List<MasterMeter> findByUtilityId(Long utilityId);
}