package com.billing.platform.controller;

import com.billing.platform.entity.UtilityProviderBill;
import com.billing.platform.repository.UtilityProviderBillRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/provider-bills")
public class UtilityProviderBillController {

    private final UtilityProviderBillRepository providerBillRepository;

    public UtilityProviderBillController(UtilityProviderBillRepository providerBillRepository) {
        this.providerBillRepository = providerBillRepository;
    }

    @PostMapping
    public ResponseEntity<UtilityProviderBill> createProviderBill(@RequestBody UtilityProviderBill bill) {
        return ResponseEntity.ok(providerBillRepository.save(bill));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UtilityProviderBill> getProviderBill(@PathVariable Long id) {
        return providerBillRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}