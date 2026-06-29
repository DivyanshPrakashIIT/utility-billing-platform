package com.billing.platform.repository;

import com.billing.platform.entity.AllocationRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AllocationRuleRepository extends JpaRepository<AllocationRule, Long> {
    List<AllocationRule> findByBuildingIdAndUtilityId(Long buildingId, Long utilityId);
}