package com.billing.platform.controller;

import com.billing.platform.entity.ErrorLog;
import com.billing.platform.entity.ImportBatch;
import com.billing.platform.repository.ErrorLogRepository;
import com.billing.platform.repository.ImportBatchRepository;
import com.billing.platform.service.etl.CsvImportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/import")
public class ImportController {

    private final CsvImportService csvImportService;
    private final ImportBatchRepository importBatchRepository;
    private final ErrorLogRepository errorLogRepository;

    public ImportController(CsvImportService csvImportService,
                            ImportBatchRepository importBatchRepository,
                            ErrorLogRepository errorLogRepository) {
        this.csvImportService = csvImportService;
        this.importBatchRepository = importBatchRepository;
        this.errorLogRepository = errorLogRepository;
    }

    @PostMapping("/batches")
    public ResponseEntity<ImportBatch> uploadCsv(@RequestParam("file") MultipartFile file) throws IOException {
        ImportBatch result = csvImportService.processCsv(file.getOriginalFilename(), file.getInputStream());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/batches/{id}")
    public ResponseEntity<ImportBatch> getBatchStatus(@PathVariable Long id) {
        return importBatchRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/batches/{id}/errors")
    public ResponseEntity<List<ErrorLog>> getBatchErrors(@PathVariable Long id) {
        return ResponseEntity.ok(errorLogRepository.findByImportBatchId(id));
    }
}