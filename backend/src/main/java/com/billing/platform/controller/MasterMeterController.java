
package com.billing.platform.controller;

import com.billing.platform.entity.MasterMeter;
import com.billing.platform.repository.MasterMeterRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MasterMeterController {

    private final MasterMeterRepository masterMeterRepository;

    public MasterMeterController(MasterMeterRepository masterMeterRepository) {
        this.masterMeterRepository = masterMeterRepository;
    }

    @PostMapping("/utilities/{utilityId}/master-meters")
    public ResponseEntity<MasterMeter> createMasterMeter(@PathVariable Long utilityId, @RequestBody MasterMeter masterMeter) {
        masterMeter.setUtilityId(utilityId);
        return ResponseEntity.ok(masterMeterRepository.save(masterMeter));
    }

    @GetMapping("/utilities/{utilityId}/master-meters")
    public ResponseEntity<List<MasterMeter>> getMasterMetersForUtility(@PathVariable Long utilityId) {
        return ResponseEntity.ok(masterMeterRepository.findByUtilityId(utilityId));
    }
}