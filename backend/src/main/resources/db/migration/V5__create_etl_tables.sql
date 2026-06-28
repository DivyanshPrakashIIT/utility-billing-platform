CREATE TABLE import_batch (
                              id BIGSERIAL PRIMARY KEY,
                              file_name VARCHAR(255) NOT NULL,
                              status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                              total_rows INT,
                              success_rows INT NOT NULL DEFAULT 0,
                              failed_rows INT NOT NULL DEFAULT 0,
                              started_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                              finished_at TIMESTAMPTZ,
                              CONSTRAINT chk_import_batch_status CHECK (status IN ('PENDING', 'RUNNING', 'COMPLETED', 'FAILED', 'PARTIALLY_FAILED'))
);

CREATE TABLE staging_meter_reading (
                                       id BIGSERIAL PRIMARY KEY,
                                       import_batch_id BIGINT NOT NULL REFERENCES import_batch(id),
                                       row_number INT NOT NULL,
                                       meter_type_raw VARCHAR(20),
                                       meter_id_raw VARCHAR(50),
                                       reading_date_raw VARCHAR(50),
                                       reading_value_raw VARCHAR(50),
                                       is_valid BOOLEAN NOT NULL DEFAULT true,
                                       created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE error_log (
                           id BIGSERIAL PRIMARY KEY,
                           import_batch_id BIGINT NOT NULL REFERENCES import_batch(id),
                           row_number INT NOT NULL,
                           field_name VARCHAR(50),
                           error_message VARCHAR(500) NOT NULL,
                           created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

ALTER TABLE meter_reading ADD COLUMN import_batch_id BIGINT REFERENCES import_batch(id);