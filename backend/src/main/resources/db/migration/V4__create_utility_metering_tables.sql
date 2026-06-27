CREATE TABLE utility (
                         id BIGSERIAL PRIMARY KEY,
                         building_id BIGINT NOT NULL REFERENCES building(id),
                         utility_type VARCHAR(30) NOT NULL,
                         unit_of_measure VARCHAR(10) NOT NULL,
                         created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                         updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                         CONSTRAINT chk_utility_type CHECK (utility_type IN ('ELECTRICITY', 'WATER', 'GAS'))
);

CREATE TABLE master_meter (
                              id BIGSERIAL PRIMARY KEY,
                              utility_id BIGINT NOT NULL REFERENCES utility(id),
                              meter_number VARCHAR(50) NOT NULL UNIQUE,
                              install_date DATE NOT NULL,
                              created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                              updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE sub_meter (
                           id BIGSERIAL PRIMARY KEY,
                           unit_id BIGINT NOT NULL REFERENCES unit(id),
                           utility_id BIGINT NOT NULL REFERENCES utility(id),
                           meter_serial VARCHAR(50) NOT NULL UNIQUE,
                           install_date DATE NOT NULL,
                           created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                           updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE meter_reading (
                               id BIGSERIAL PRIMARY KEY,
                               sub_meter_id BIGINT REFERENCES sub_meter(id),
                               master_meter_id BIGINT REFERENCES master_meter(id),
                               reading_date DATE NOT NULL,
                               reading_value NUMERIC(14,3) NOT NULL,
                               created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                               CONSTRAINT chk_one_meter_type CHECK (
                                   (sub_meter_id IS NOT NULL AND master_meter_id IS NULL)
                                       OR
                                   (sub_meter_id IS NULL AND master_meter_id IS NOT NULL)
                                   )
);