-- Eliminar tablas si existen (en orden inverso por las FK)
DROP TABLE IF EXISTS charges CASCADE;
DROP TABLE IF EXISTS payment_intents CASCADE;
DROP TABLE IF EXISTS reservations CASCADE;
DROP TABLE IF EXISTS trips CASCADE;

-- Crear tabla trips CON updated_at
CREATE TABLE trips (
   id BIGSERIAL PRIMARY KEY,
   driver_id BIGINT NOT NULL,
   origin VARCHAR(255) NOT NULL,
   destination VARCHAR(255) NOT NULL,
   start_time TIMESTAMP NOT NULL,
   seats_total INTEGER NOT NULL,
   seats_available INTEGER NOT NULL,
   price DECIMAL(10, 2) NOT NULL,
   status VARCHAR(50) NOT NULL,
   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Resto de las tablas...
CREATE TABLE reservations (
  id BIGSERIAL PRIMARY KEY,
  trip_id BIGINT NOT NULL,
  passenger_id BIGINT NOT NULL,
  status VARCHAR(50) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (trip_id) REFERENCES trips(id) ON DELETE CASCADE
);

-- Índices
CREATE INDEX idx_trips_driver_id ON trips(driver_id);
CREATE INDEX idx_trips_status ON trips(status);
CREATE INDEX idx_reservations_trip_id ON reservations(trip_id);
CREATE INDEX idx_reservations_passenger_id ON reservations(passenger_id);