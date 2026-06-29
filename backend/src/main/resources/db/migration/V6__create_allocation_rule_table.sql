CREATE TABLE allocation_rule (
                                 id BIGSERIAL PRIMARY KEY,
                                 building_id BIGINT NOT NULL REFERENCES building(id),
                                 utility_id BIGINT NOT NULL REFERENCES utility(id),
                                 method VARCHAR(30) NOT NULL,
                                 parameters_json TEXT,
                                 effective_from DATE NOT NULL,
                                 effective_to DATE,
                                 created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                                 updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                                 CONSTRAINT chk_allocation_method CHECK (
                                     method IN ('CONSUMPTION','OCCUPANCY','HEADCOUNT','AREA','BEDROOM','FIXED_PERCENTAGE')
                                     )
);