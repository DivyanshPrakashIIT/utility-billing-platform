package com.billing.platform.repository;

import com.billing.platform.entity.Utility;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UtilityRepository extends JpaRepository<Utility, Long> {
    List<Utility> findByBuildingId(Long buildingId);
}