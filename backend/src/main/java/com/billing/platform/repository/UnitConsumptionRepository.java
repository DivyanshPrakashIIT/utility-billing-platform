package com.billing.platform.repository;

import com.billing.platform.entity.UnitConsumption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UnitConsumptionRepository extends JpaRepository<UnitConsumption, Long> {
    List<UnitConsumption> findByProviderBillId(Long providerBillId);
}