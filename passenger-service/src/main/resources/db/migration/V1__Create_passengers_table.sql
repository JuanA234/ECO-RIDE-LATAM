CREATE TABLE passengers (
    id BIGSERIAL PRIMARY KEY,
    keycloak_sub VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    rating_avg DECIMAL(3, 2) DEFAULT 0.0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE passengers ADD CONSTRAINT chk_passengers_rating_avg
    CHECK (rating_avg >= 0.0 AND rating_avg <= 5.0);

COMMENT ON TABLE passengers IS 'Main passenger profiles table';
COMMENT ON COLUMN passengers.keycloak_sub IS 'Keycloak subject (sub) from JWT token - unique identifier';
COMMENT ON COLUMN passengers.name IS 'Full name of the passenger';
COMMENT ON COLUMN passengers.email IS 'Email address - must be unique';
COMMENT ON COLUMN passengers.rating_avg IS 'Average rating from 0.0 to 5.0';