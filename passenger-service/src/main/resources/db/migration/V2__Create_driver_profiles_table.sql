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

COMMENT ON TABLE driver_profiles IS 'Driver profiles for passengers who also drive';
COMMENT ON COLUMN driver_profiles.passenger_id IS 'Foreign key to passengers table - one driver profile per passenger';
COMMENT ON COLUMN driver_profiles.license_no IS 'Driver license number - must be unique';
COMMENT ON COLUMN driver_profiles.car_plate IS 'Vehicle plate number';
COMMENT ON COLUMN driver_profiles.seats_offered IS 'Number of available seats in vehicle';
COMMENT ON COLUMN driver_profiles.verification_status IS 'Verification status: PENDING, VERIFIED, REJECTED, SUSPENDED';