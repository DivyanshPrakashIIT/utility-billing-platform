CREATE TABLE utility_provider_bill (
                                       id BIGSERIAL PRIMARY KEY,
                                       master_meter_id BIGINT NOT NULL REFERENCES master_meter(id),
                                       billing_period_start DATE NOT NULL,
                                       billing_period_end DATE NOT NULL,
                                       total_amount NUMERIC(14,2) NOT NULL,
                                       total_consumption NUMERIC(14,3),
                                       status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
                                       created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                                       updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                                       CONSTRAINT chk_provider_bill_status CHECK (status IN ('DRAFT', 'FINALIZED'))
);

CREATE TABLE unit_consumption (
                                  id BIGSERIAL PRIMARY KEY,
                                  unit_id BIGINT NOT NULL REFERENCES unit(id),
                                  provider_bill_id BIGINT NOT NULL REFERENCES utility_provider_bill(id),
                                  consumption_value NUMERIC(14,3),
                                  allocation_rule_id BIGINT NOT NULL REFERENCES allocation_rule(id),
                                  allocation_weight NUMERIC(10,6) NOT NULL,
                                  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE unit_bill (
                           id BIGSERIAL PRIMARY KEY,
                           unit_id BIGINT NOT NULL REFERENCES unit(id),
                           provider_bill_id BIGINT NOT NULL REFERENCES utility_provider_bill(id),
                           amount_due NUMERIC(14,2) NOT NULL,
                           amount_paid NUMERIC(14,2) NOT NULL DEFAULT 0,
                           status VARCHAR(20) NOT NULL DEFAULT 'UNPAID',
                           generated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                           CONSTRAINT chk_unit_bill_status CHECK (status IN ('UNPAID', 'PARTIAL', 'PAID'))
);

CREATE TABLE allocation_history (
                                    id BIGSERIAL PRIMARY KEY,
                                    provider_bill_id BIGINT NOT NULL REFERENCES utility_provider_bill(id),
                                    allocation_rule_id BIGINT NOT NULL REFERENCES allocation_rule(id),
                                    snapshot_json TEXT NOT NULL,
                                    executed_at TIMESTAMPTZ NOT NULL DEFAULT now()
);