CREATE TABLE charge (
    id BIGSERIAL PRIMARY KEY,
    payment_intent_id BIGINT NOT NULL,
    provider VARCHAR(50) NOT NULL,
    provider_ref VARCHAR(255) NOT NULL UNIQUE,
    amount DECIMAL(10, 2) NOT NULL CHECK (amount > 0),
    captured_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_charge_payment_intent FOREIGN KEY (payment_intent_id)
        REFERENCES payment_intent(id) ON DELETE CASCADE
);

CREATE INDEX idx_charge_payment_intent ON charge(payment_intent_id);
CREATE INDEX idx_charge_provider_ref ON charge(provider_ref);
CREATE INDEX idx_charge_captured ON charge(captured_at);

COMMENT ON TABLE charge IS 'Stores successful payment charges';
COMMENT ON COLUMN charge.provider IS 'Payment provider: STRIPE, PAYPAL, etc.';