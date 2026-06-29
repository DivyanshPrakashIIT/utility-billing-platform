package com.billing.platform.controller;

import com.billing.platform.entity.AllocationRule;
import com.billing.platform.repository.AllocationRuleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/allocation-rules")
public class AllocationRuleController {

    private final AllocationRuleRepository allocationRuleRepository;

    public AllocationRuleController(AllocationRuleRepository allocationRuleRepository) {
        this.allocationRuleRepository = allocationRuleRepository;
    }

    @PostMapping
    public ResponseEntity<AllocationRule> createRule(@RequestBody AllocationRule rule) {
        return ResponseEntity.ok(allocationRuleRepository.save(rule));
    }

    @GetMapping
    public ResponseEntity<List<AllocationRule>> getAllRules() {
        return ResponseEntity.ok(allocationRuleRepository.findAll());
    }

    @GetMapping("/building/{buildingId}/utility/{utilityId}")
    public ResponseEntity<List<AllocationRule>> getRulesForBuildingUtility(
            @PathVariable Long buildingId, @PathVariable Long utilityId) {
        return ResponseEntity.ok(allocationRuleRepository.findByBuildingIdAndUtilityId(buildingId, utilityId));
    }
}