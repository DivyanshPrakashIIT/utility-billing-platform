CREATE TABLE building (
                          id BIGSERIAL PRIMARY KEY,
                          name VARCHAR(150) NOT NULL,
                          address VARCHAR(300),
                          currency_code CHAR(3) NOT NULL DEFAULT 'USD',
                          created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                          updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE unit (
                      id BIGSERIAL PRIMARY KEY,
                      building_id BIGINT NOT NULL REFERENCES building(id),
                      unit_code VARCHAR(20) NOT NULL,
                      floor_area_sqft NUMERIC(10,2),
                      bedroom_count INT,
                      is_active BOOLEAN NOT NULL DEFAULT true,
                      created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                      updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                      CONSTRAINT uq_unit_code_per_building UNIQUE (building_id, unit_code)
);

CREATE TABLE tenant (
                        id BIGSERIAL PRIMARY KEY,
                        unit_id BIGINT NOT NULL REFERENCES unit(id),
                        full_name VARCHAR(150) NOT NULL,
                        resident_count INT NOT NULL DEFAULT 1,
                        move_in_date DATE NOT NULL,
                        move_out_date DATE,
                        created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                        updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);