package com.billing.platform.service.etl;

import com.billing.platform.entity.*;
import com.billing.platform.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvImportService {

    private final ImportBatchRepository importBatchRepository;
    private final StagingMeterReadingRepository stagingMeterReadingRepository;
    private final ErrorLogRepository errorLogRepository;
    private final MeterReadingRepository meterReadingRepository;
    private final SubMeterRepository subMeterRepository;
    private final MasterMeterRepository masterMeterRepository;

    public CsvImportService(ImportBatchRepository importBatchRepository,
                            StagingMeterReadingRepository stagingMeterReadingRepository,
                            ErrorLogRepository errorLogRepository,
                            MeterReadingRepository meterReadingRepository,
                            SubMeterRepository subMeterRepository,
                            MasterMeterRepository masterMeterRepository) {
        this.importBatchRepository = importBatchRepository;
        this.stagingMeterReadingRepository = stagingMeterReadingRepository;
        this.errorLogRepository = errorLogRepository;
        this.meterReadingRepository = meterReadingRepository;
        this.subMeterRepository = subMeterRepository;
        this.masterMeterRepository = masterMeterRepository;
    }

    @Transactional
    public ImportBatch processCsv(String fileName, InputStream fileStream) throws IOException {

        // Step 1: Create the batch record
        ImportBatch batch = new ImportBatch();
        batch.setFileName(fileName);
        batch.setStatus("RUNNING");
        batch = importBatchRepository.save(batch);

        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(fileStream, StandardCharsets.UTF_8))) {
            String line;
            boolean isHeader = true;
            while ((line = reader.readLine()) != null) {
                if (isHeader) { isHeader = false; continue; } // skip header row
                if (line.trim().isEmpty()) continue;
                lines.add(line);
            }
        }

        batch.setTotalRows(lines.size());
        int successCount = 0;
        int failCount = 0;

        // Step 2: Process each row - stage, validate, then load if valid
        for (int i = 0; i < lines.size(); i++) {
            int rowNumber = i + 2; // +2 because row 1 is the header, and humans count from 1
            String[] columns = lines.get(i).split(",");

            StagingMeterReading staging = new StagingMeterReading();
            staging.setImportBatchId(batch.getId());
            staging.setRowNumber(rowNumber);

            if (columns.length < 4) {
                logError(batch.getId(), rowNumber, null, "Row does not have 4 columns (meter_type,meter_id,reading_date,reading_value)");
                staging.setValid(false);
                stagingMeterReadingRepository.save(staging);
                failCount++;
                continue;
            }

            String meterType = columns[0].trim().toUpperCase();
            String meterIdStr = columns[1].trim();
            String dateStr = columns[2].trim();
            String valueStr = columns[3].trim();

            staging.setMeterTypeRaw(meterType);
            staging.setMeterIdRaw(meterIdStr);
            staging.setReadingDateRaw(dateStr);
            staging.setReadingValueRaw(valueStr);

            boolean rowIsValid = true;

            // Validation: meter_type
            if (!meterType.equals("SUB") && !meterType.equals("MASTER")) {
                logError(batch.getId(), rowNumber, "meter_type", "Must be SUB or MASTER, got: " + meterType);
                rowIsValid = false;
            }

            // Validation: meter_id is numeric and exists
            Long meterId = null;
            try {
                meterId = Long.parseLong(meterIdStr);
                if (meterType.equals("SUB") && subMeterRepository.findById(meterId).isEmpty()) {
                    logError(batch.getId(), rowNumber, "meter_id", "Sub-meter ID not found: " + meterId);
                    rowIsValid = false;
                } else if (meterType.equals("MASTER") && masterMeterRepository.findById(meterId).isEmpty()) {
                    logError(batch.getId(), rowNumber, "meter_id", "Master meter ID not found: " + meterId);
                    rowIsValid = false;
                }
            } catch (NumberFormatException e) {
                logError(batch.getId(), rowNumber, "meter_id", "Not a valid number: " + meterIdStr);
                rowIsValid = false;
            }

            // Validation: reading_date
            LocalDate readingDate = null;
            try {
                readingDate = LocalDate.parse(dateStr);
            } catch (Exception e) {
                logError(batch.getId(), rowNumber, "reading_date", "Invalid date format, expected YYYY-MM-DD: " + dateStr);
                rowIsValid = false;
            }

            // Validation: reading_value
            BigDecimal readingValue = null;
            try {
                readingValue = new BigDecimal(valueStr);
                if (readingValue.compareTo(BigDecimal.ZERO) < 0) {
                    logError(batch.getId(), rowNumber, "reading_value", "Reading value cannot be negative: " + valueStr);
                    rowIsValid = false;
                }
            } catch (NumberFormatException e) {
                logError(batch.getId(), rowNumber, "reading_value", "Not a valid number: " + valueStr);
                rowIsValid = false;
            }

            staging.setValid(rowIsValid);
            stagingMeterReadingRepository.save(staging);

            if (rowIsValid) {
                MeterReading reading = new MeterReading();
                reading.setReadingDate(readingDate);
                reading.setReadingValue(readingValue);
                reading.setImportBatchId(batch.getId());
                if (meterType.equals("SUB")) {
                    reading.setSubMeterId(meterId);
                } else {
                    reading.setMasterMeterId(meterId);
                }
                meterReadingRepository.save(reading);
                successCount++;
            } else {
                failCount++;
            }
        }

        // Step 3: Finalize the batch status
        batch.setSuccessRows(successCount);
        batch.setFailedRows(failCount);
        batch.setFinishedAt(OffsetDateTime.now());
        if (failCount == 0) {
            batch.setStatus("COMPLETED");
        } else if (successCount == 0) {
            batch.setStatus("FAILED");
        } else {
            batch.setStatus("PARTIALLY_FAILED");
        }

        return importBatchRepository.save(batch);
    }

    private void logError(Long batchId, int rowNumber, String fieldName, String message) {
        ErrorLog error = new ErrorLog();
        error.setImportBatchId(batchId);
        error.setRowNumber(rowNumber);
        error.setFieldName(fieldName);
        error.setErrorMessage(message);
        errorLogRepository.save(error);
    }
}