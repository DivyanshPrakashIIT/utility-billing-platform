package com.billing.platform.repository;

import com.billing.platform.entity.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UnitRepository extends JpaRepository<Unit, Long> {
    List<Unit> findByBuildingId(Long buildingId);
}