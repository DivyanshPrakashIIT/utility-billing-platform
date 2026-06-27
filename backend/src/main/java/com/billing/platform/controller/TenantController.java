package com.billing.platform.controller;

import com.billing.platform.entity.Tenant;
import com.billing.platform.repository.TenantRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TenantController {

    private final TenantRepository tenantRepository;

    public TenantController(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    @PostMapping("/units/{unitId}/tenants")
    public ResponseEntity<Tenant> createTenant(@PathVariable Long unitId, @RequestBody Tenant tenant) {
        tenant.setUnitId(unitId);
        Tenant saved = tenantRepository.save(tenant);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/units/{unitId}/tenants")
    public ResponseEntity<List<Tenant>> getTenantsForUnit(@PathVariable Long unitId) {
        return ResponseEntity.ok(tenantRepository.findByUnitId(unitId));
    }
}