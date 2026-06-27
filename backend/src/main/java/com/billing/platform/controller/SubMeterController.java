package com.billing.platform.controller;

import com.billing.platform.entity.SubMeter;
import com.billing.platform.repository.SubMeterRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SubMeterController {

    private final SubMeterRepository subMeterRepository;

    public SubMeterController(SubMeterRepository subMeterRepository) {
        this.subMeterRepository = subMeterRepository;
    }

    @PostMapping("/units/{unitId}/sub-meters")
    public ResponseEntity<SubMeter> createSubMeter(@PathVariable Long unitId, @RequestBody SubMeter subMeter) {
        subMeter.setUnitId(unitId);
        return ResponseEntity.ok(subMeterRepository.save(subMeter));
    }

    @GetMapping("/units/{unitId}/sub-meters")
    public ResponseEntity<List<SubMeter>> getSubMetersForUnit(@PathVariable Long unitId) {
        return ResponseEntity.ok(subMeterRepository.findByUnitId(unitId));
    }
}