package com.billing.platform.controller;

import com.billing.platform.dto.GenerateBillsRequest;
import com.billing.platform.entity.UnitBill;
import com.billing.platform.service.billing.BillingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/provider-bills")
public class BillGenerationController {

    private final BillingService billingService;

    public BillGenerationController(BillingService billingService) {
        this.billingService = billingService;
    }

    @PostMapping("/{id}/generate")
    public ResponseEntity<List<UnitBill>> generateBills(@PathVariable Long id, @RequestBody GenerateBillsRequest request) {
        List<UnitBill> bills = billingService.generateBills(id, request.getBuildingId(), request.getAllocationRuleId());
        return ResponseEntity.ok(bills);
    }
}