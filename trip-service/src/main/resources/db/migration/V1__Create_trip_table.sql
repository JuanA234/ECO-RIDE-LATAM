CREATE TABLE trip (
  id BIGSERIAL PRIMARY KEY,
  driver_id BIGINT NOT NULL,
  origin VARCHAR(255) NOT NULL,
  destination VARCHAR(255) NOT NULL,
  start_time TIMESTAMP NOT NULL,
  seats_total INTEGER NOT NULL CHECK (seats_total > 0),
  seats_available INTEGER NOT NULL CHECK (seats_available >= 0),
  price DECIMAL(10, 2) NOT NULL CHECK (price > 0),
  status VARCHAR(50) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

  CONSTRAINT chk_seats_capacity CHECK (seats_available <= seats_total)
);

CREATE INDEX idx_trip_status ON trip(status);
CREATE INDEX idx_trip_driver ON trip(driver_id);
CREATE INDEX idx_trip_route ON trip(origin, destination, start_time);
CREATE INDEX idx_trip_start_time ON trip(start_time) WHERE status = 'SCHEDULED';

COMMENT ON TABLE trip IS 'Stores trip information for rideshare service';
COMMENT ON COLUMN trip.status IS 'Valid values: SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED';