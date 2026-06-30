CREATE TABLE payment (
                         id BIGSERIAL PRIMARY KEY,
                         unit_bill_id BIGINT NOT NULL REFERENCES unit_bill(id),
                         amount NUMERIC(14,2) NOT NULL,
                         paid_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                         method VARCHAR(30) NOT NULL,
                         created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                         CONSTRAINT chk_payment_method CHECK (method IN ('CASH', 'BANK', 'UPI', 'CARD', 'OTHER')),
                         CONSTRAINT chk_payment_amount_positive CHECK (amount > 0)
);