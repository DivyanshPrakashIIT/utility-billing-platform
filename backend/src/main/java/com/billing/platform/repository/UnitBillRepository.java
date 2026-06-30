package com.billing.platform.repository;

import com.billing.platform.entity.UnitBill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UnitBillRepository extends JpaRepository<UnitBill, Long> {
    List<UnitBill> findByProviderBillId(Long providerBillId);
    List<UnitBill> findByUnitId(Long unitId);
}