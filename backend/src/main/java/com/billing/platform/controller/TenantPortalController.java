package com.billing.platform.controller;

import com.billing.platform.entity.Payment;
import com.billing.platform.entity.UnitBill;
import com.billing.platform.repository.PaymentRepository;
import com.billing.platform.repository.UnitBillRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tenant-portal")
public class TenantPortalController {

    private final UnitBillRepository unitBillRepository;
    private final PaymentRepository paymentRepository;

    public TenantPortalController(UnitBillRepository unitBillRepository, PaymentRepository paymentRepository) {
        this.unitBillRepository = unitBillRepository;
        this.paymentRepository = paymentRepository;
    }

    @GetMapping("/units/{unitId}/bill-history")
    public ResponseEntity<List<Map<String, Object>>> getBillHistory(@PathVariable Long unitId) {
        List<UnitBill> bills = unitBillRepository.findByUnitId(unitId);

        List<Map<String, Object>> result = new ArrayList<>();
        for (UnitBill bill : bills) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("bill", bill);
            entry.put("payments", paymentRepository.findByUnitBillId(bill.getId()));
            result.add(entry);
        }

        return ResponseEntity.ok(result);
    }
}