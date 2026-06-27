package com.billing.platform.controller;

import com.billing.platform.entity.Unit;
import com.billing.platform.repository.UnitRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UnitController {

    private final UnitRepository unitRepository;

    public UnitController(UnitRepository unitRepository) {
        this.unitRepository = unitRepository;
    }

    @PostMapping("/buildings/{buildingId}/units")
    public ResponseEntity<Unit> createUnit(@PathVariable Long buildingId, @RequestBody Unit unit) {
        unit.setBuildingId(buildingId);
        Unit saved = unitRepository.save(unit);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/buildings/{buildingId}/units")
    public ResponseEntity<List<Unit>> getUnitsForBuilding(@PathVariable Long buildingId) {
        return ResponseEntity.ok(unitRepository.findByBuildingId(buildingId));
    }
}