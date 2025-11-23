CREATE TABLE payment_intent (
    id BIGSERIAL PRIMARY KEY,
    reservation_id BIGINT NOT NULL UNIQUE,
    amount DECIMAL(10, 2) NOT NULL CHECK (amount > 0),
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    status VARCHAR(50) NOT NULL,
    failure_reason VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_payment_intent_reservation ON payment_intent(reservation_id);
CREATE INDEX idx_payment_intent_status ON payment_intent(status);
CREATE INDEX idx_payment_intent_created ON payment_intent(created_at);

COMMENT ON TABLE payment_intent IS 'Stores payment intent information for reservations';
COMMENT ON COLUMN payment_intent.status IS 'Valid values: REQUIRES_ACTION, AUTHORIZED, CAPTURED, FAILED, REFUNDED';
