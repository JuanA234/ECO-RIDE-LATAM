CREATE TABLE reservations (
     id BIGSERIAL PRIMARY KEY,
     trip_id BIGINT NOT NULL,
     passenger_id BIGINT NOT NULL,
     status VARCHAR(50) NOT NULL,
     created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
     updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

     CONSTRAINT fk_reservation_trip FOREIGN KEY (trip_id)
         REFERENCES trips(id) ON DELETE RESTRICT,
     CONSTRAINT uq_trip_passenger UNIQUE (trip_id, passenger_id)
);

CREATE INDEX idx_reservation_trip ON reservations(trip_id);
CREATE INDEX idx_reservation_passenger ON reservations(passenger_id);
CREATE INDEX idx_reservation_status ON reservations(status);
CREATE INDEX idx_reservation_passenger_status ON reservations(passenger_id, status);

COMMENT ON TABLE reservations IS 'Stores passenger reservations for trips';
COMMENT ON COLUMN reservations.status IS 'Valid values: PENDING, CONFIRMED, CANCELLED, PAYMENT_FAILED';