package com.billing.platform.controller;

import com.billing.platform.entity.MeterReading;
import com.billing.platform.repository.MeterReadingRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MeterReadingController {

    private final MeterReadingRepository meterReadingRepository;

    public MeterReadingController(MeterReadingRepository meterReadingRepository) {
        this.meterReadingRepository = meterReadingRepository;
    }

    @PostMapping("/sub-meters/{subMeterId}/readings")
    public ResponseEntity<MeterReading> createSubMeterReading(@PathVariable Long subMeterId, @RequestBody MeterReading reading) {
        reading.setSubMeterId(subMeterId);
        reading.setMasterMeterId(null);
        return ResponseEntity.ok(meterReadingRepository.save(reading));
    }

    @PostMapping("/master-meters/{masterMeterId}/readings")
    public ResponseEntity<MeterReading> createMasterMeterReading(@PathVariable Long masterMeterId, @RequestBody MeterReading reading) {
        reading.setMasterMeterId(masterMeterId);
        reading.setSubMeterId(null);
        return ResponseEntity.ok(meterReadingRepository.save(reading));
    }

    @GetMapping("/sub-meters/{subMeterId}/readings")
    public ResponseEntity<List<MeterReading>> getReadingsForSubMeter(@PathVariable Long subMeterId) {
        return ResponseEntity.ok(meterReadingRepository.findBySubMeterId(subMeterId));
    }

    @GetMapping("/master-meters/{masterMeterId}/readings")
    public ResponseEntity<List<MeterReading>> getReadingsForMasterMeter(@PathVariable Long masterMeterId) {
        return ResponseEntity.ok(meterReadingRepository.findByMasterMeterId(masterMeterId));
    }
}