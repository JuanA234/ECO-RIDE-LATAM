-- Passenger Repository Schema
DROP TABLE IF EXISTS passengers;

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

-- DriverProfile Repository Schema
DROP TABLE IF EXISTS driver_profiles;

CREATE TABLE driver_profiles (
                                 id BIGSERIAL PRIMARY KEY,
                                 passenger_id BIGINT NOT NULL UNIQUE,
                                 license_no VARCHAR(50) NOT NULL UNIQUE,
                                 car_plate VARCHAR(20) NOT NULL,
                                 seats_offered INTEGER NOT NULL,
                                 verification_status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
                                 CONSTRAINT fk_driver_passenger FOREIGN KEY (passenger_id)
                                     REFERENCES passengers(id) ON DELETE CASCADE
);

ALTER TABLE driver_profiles ADD CONSTRAINT chk_driver_verification_status
    CHECK (verification_status IN ('PENDING', 'VERIFIED', 'REJECTED', 'SUSPENDED'));

-- Rating Repository Schema
DROP TABLE IF EXISTS ratings;

CREATE TABLE ratings (
                         id BIGSERIAL PRIMARY KEY,
                         trip_id BIGINT NOT NULL,
                         from_id BIGINT NOT NULL,
                         to_id BIGINT NOT NULL,
                         score INTEGER NOT NULL,
                         comment VARCHAR(500),
                         CONSTRAINT fk_rating_from FOREIGN KEY (from_id)
                             REFERENCES passengers(id) ON DELETE CASCADE,
                         CONSTRAINT fk_rating_to FOREIGN KEY (to_id)
                             REFERENCES passengers(id) ON DELETE CASCADE
);

ALTER TABLE ratings ADD CONSTRAINT chk_ratings_score
    CHECK (score >= 1 AND score <= 5);

ALTER TABLE ratings ADD CONSTRAINT chk_ratings_not_self
    CHECK (from_id != to_id);

ALTER TABLE ratings ADD CONSTRAINT unique_rating_per_trip
    UNIQUE (trip_id, from_id, to_id);
