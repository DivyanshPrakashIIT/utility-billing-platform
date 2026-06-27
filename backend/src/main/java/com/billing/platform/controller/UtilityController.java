package com.billing.platform.controller;

import com.billing.platform.entity.Utility;
import com.billing.platform.repository.UtilityRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UtilityController {

    private final UtilityRepository utilityRepository;

    public UtilityController(UtilityRepository utilityRepository) {
        this.utilityRepository = utilityRepository;
    }

    @PostMapping("/buildings/{buildingId}/utilities")
    public ResponseEntity<Utility> createUtility(@PathVariable Long buildingId, @RequestBody Utility utility) {
        utility.setBuildingId(buildingId);
        return ResponseEntity.ok(utilityRepository.save(utility));
    }

    @GetMapping("/buildings/{buildingId}/utilities")
    public ResponseEntity<List<Utility>> getUtilitiesForBuilding(@PathVariable Long buildingId) {
        return ResponseEntity.ok(utilityRepository.findByBuildingId(buildingId));
    }
}