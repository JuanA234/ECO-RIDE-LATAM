-- Template Repository Schema
DROP TABLE IF EXISTS templates

CREATE TABLE templates (
                           id BIGSERIAL PRIMARY KEY,
                           code VARCHAR(100) NOT NULL UNIQUE,
                           channel VARCHAR(50) NOT NULL,
                           subject VARCHAR(200),
                           body TEXT NOT NULL
);

ALTER TABLE templates ADD CONSTRAINT chk_templates_channel
    CHECK (channel IN ('EMAIL', 'SMS', 'PUSH'));

-- Outbox Repository Schema
DROP TABLE IF EXISTS outbox;

CREATE TABLE outbox (
                        id BIGSERIAL PRIMARY KEY,
                        event_type VARCHAR(100) NOT NULL,
                        payload TEXT NOT NULL,
                        status VARCHAR(50) NOT NULL,
                        retries INTEGER NOT NULL DEFAULT 0
);

ALTER TABLE outbox ADD CONSTRAINT chk_outbox_status
    CHECK (status IN ('PENDING', 'SENT', 'FAILED'));
