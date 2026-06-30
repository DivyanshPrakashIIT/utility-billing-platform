package com.billing.platform.repository;

import com.billing.platform.entity.AllocationHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AllocationHistoryRepository extends JpaRepository<AllocationHistory, Long> {
    List<AllocationHistory> findByProviderBillId(Long providerBillId);
}