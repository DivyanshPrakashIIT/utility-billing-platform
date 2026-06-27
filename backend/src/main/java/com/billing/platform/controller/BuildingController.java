package com.billing.platform.controller;

import com.billing.platform.entity.Building;
import com.billing.platform.repository.BuildingRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/buildings")
public class BuildingController {

    private final BuildingRepository buildingRepository;

    public BuildingController(BuildingRepository buildingRepository) {
        this.buildingRepository = buildingRepository;
    }

    @PostMapping
    public ResponseEntity<Building> createBuilding(@RequestBody Building building) {
        Building saved = buildingRepository.save(building);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<List<Building>> getAllBuildings() {
        return ResponseEntity.ok(buildingRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Building> getBuildingById(@PathVariable Long id) {
        return buildingRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}