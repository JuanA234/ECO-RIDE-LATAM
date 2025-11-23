CREATE TABLE templates (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(100) NOT NULL UNIQUE,
    channel VARCHAR(50) NOT NULL,
    subject VARCHAR(200),
    body TEXT NOT NULL,
);

ALTER TABLE templates ADD CONSTRAINT chk_templates_channel
    CHECK (channel IN ('EMAIL', 'SMS', 'PUSH'));

COMMENT ON TABLE templates IS 'Notification templates with placeholders';
COMMENT ON COLUMN templates.code IS 'Unique template identifier';
COMMENT ON COLUMN templates.channel IS 'Notification channel: EMAIL, SMS, or PUSH';
COMMENT ON COLUMN templates.body IS 'Template body with placeholders';