package com.billing.platform.repository;

import com.billing.platform.entity.UtilityProviderBill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UtilityProviderBillRepository extends JpaRepository<UtilityProviderBill, Long> {
}