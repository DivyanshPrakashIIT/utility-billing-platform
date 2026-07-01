CREATE TABLE app_user (
                          id BIGSERIAL PRIMARY KEY,
                          username VARCHAR(50) NOT NULL UNIQUE,
                          email VARCHAR(150) NOT NULL UNIQUE,
                          password_hash VARCHAR(255) NOT NULL,
                          role VARCHAR(20) NOT NULL DEFAULT 'TENANT',
                          is_active BOOLEAN NOT NULL DEFAULT true,
                          created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                          updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                          CONSTRAINT chk_user_role CHECK (role IN ('ADMIN', 'OWNER', 'TENANT'))
);

-- Default admin user: username=admin, password=admin123
-- BCrypt hash of 'admin123'
INSERT INTO app_user (username, email, password_hash, role)
VALUES ('admin', 'admin@billing.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN');