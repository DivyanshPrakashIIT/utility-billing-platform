package com.billing.platform.controller;

import com.billing.platform.dto.AllocationPreviewRequest;
import com.billing.platform.entity.AllocationRule;
import com.billing.platform.entity.Unit;
import com.billing.platform.repository.AllocationRuleRepository;
import com.billing.platform.repository.UnitRepository;
import com.billing.platform.service.allocation.AllocationStrategy;
import com.billing.platform.service.allocation.AllocationStrategyFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/allocation-preview")
public class AllocationPreviewController {

    private final AllocationRuleRepository allocationRuleRepository;
    private final UnitRepository unitRepository;
    private final AllocationStrategyFactory strategyFactory;

    public AllocationPreviewController(AllocationRuleRepository allocationRuleRepository,
                                       UnitRepository unitRepository,
                                       AllocationStrategyFactory strategyFactory) {
        this.allocationRuleRepository = allocationRuleRepository;
        this.unitRepository = unitRepository;
        this.strategyFactory = strategyFactory;
    }

    @PostMapping
    public ResponseEntity<Map<Long, BigDecimal>> previewAllocation(@RequestBody AllocationPreviewRequest request) {
        AllocationRule rule = allocationRuleRepository.findById(request.getAllocationRuleId())
                .orElseThrow(() -> new IllegalArgumentException("Allocation rule not found: " + request.getAllocationRuleId()));

        List<Unit> occupiedUnits = unitRepository.findByBuildingId(request.getBuildingId())
                .stream()
                .filter(Unit::isActive)
                .collect(Collectors.toList());

        if (occupiedUnits.isEmpty()) {
            throw new IllegalStateException("No active units found for building " + request.getBuildingId());
        }

        AllocationStrategy strategy = strategyFactory.get(rule.getMethod());

        Map<Long, BigDecimal> result = strategy.allocate(
                request.getTotalBillAmount(),
                occupiedUnits,
                rule,
                request.getPeriodStart(),
                request.getPeriodEnd(),
                request.getConsumptionByUnitId()
        );

        return ResponseEntity.ok(result);
    }
}