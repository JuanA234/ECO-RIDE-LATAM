CREATE TABLE outbox (
    id BIGSERIAL PRIMARY KEY,
    event_type VARCHAR(100) NOT NULL,
    payload TEXT NOT NULL,
    status VARCHAR(50) NOT NULL,
    retries INTEGER NOT NULL DEFAULT 0
);

ALTER TABLE outbox ADD CONSTRAINT chk_outbox_status
    CHECK (status IN ('PENDING', 'SENT', 'FAILED'));

COMMENT ON TABLE outbox IS 'Outbox pattern for notification event tracking';
COMMENT ON COLUMN outbox.event_type IS 'Type of event';
COMMENT ON COLUMN outbox.payload IS 'JSON payload of the notification request';
COMMENT ON COLUMN outbox.status IS 'Current status: PENDING, SENT, or FAILED';
COMMENT ON COLUMN outbox.retries IS 'Number of retry attempts made';