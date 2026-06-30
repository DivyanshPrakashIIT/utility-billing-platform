package com.billing.platform.controller;

import com.billing.platform.entity.UnitBill;
import com.billing.platform.repository.UnitBillRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UnitBillController {

    private final UnitBillRepository unitBillRepository;

    public UnitBillController(UnitBillRepository unitBillRepository) {
        this.unitBillRepository = unitBillRepository;
    }

    @GetMapping("/units/{unitId}/bills")
    public ResponseEntity<List<UnitBill>> getBillsForUnit(@PathVariable Long unitId) {
        return ResponseEntity.ok(unitBillRepository.findByUnitId(unitId));
    }

    @GetMapping("/provider-bills/{providerBillId}/unit-bills")
    public ResponseEntity<List<UnitBill>> getUnitBillsForProviderBill(@PathVariable Long providerBillId) {
        return ResponseEntity.ok(unitBillRepository.findByProviderBillId(providerBillId));
    }
}