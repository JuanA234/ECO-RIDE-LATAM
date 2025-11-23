CREATE TABLE reservation (
     id BIGSERIAL PRIMARY KEY,
     trip_id BIGINT NOT NULL,
     passenger_id BIGINT NOT NULL,
     status VARCHAR(50) NOT NULL,
     created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
     updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

     CONSTRAINT fk_reservation_trip FOREIGN KEY (trip_id)
         REFERENCES trip(id) ON DELETE RESTRICT,
     CONSTRAINT uq_trip_passenger UNIQUE (trip_id, passenger_id)
);

CREATE INDEX idx_reservation_trip ON reservation(trip_id);
CREATE INDEX idx_reservation_passenger ON reservation(passenger_id);
CREATE INDEX idx_reservation_status ON reservation(status);
CREATE INDEX idx_reservation_passenger_status ON reservation(passenger_id, status);

COMMENT ON TABLE reservation IS 'Stores passenger reservations for trips';
COMMENT ON COLUMN reservation.status IS 'Valid values: PENDING, CONFIRMED, CANCELLED, PAYMENT_FAILED';