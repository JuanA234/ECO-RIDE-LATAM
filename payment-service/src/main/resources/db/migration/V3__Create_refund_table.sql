CREATE TABLE refund (
    id BIGSERIAL PRIMARY KEY,
    charge_id BIGINT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL CHECK (amount > 0),
    reason VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_refund_charge FOREIGN KEY (charge_id)
        REFERENCES charge(id) ON DELETE CASCADE
);

CREATE INDEX idx_refund_charge ON refund(charge_id);
CREATE INDEX idx_refund_created ON refund(created_at);

COMMENT ON TABLE refund IS 'Stores refund transactions for charges';