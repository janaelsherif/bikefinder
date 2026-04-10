-- Email + filter snapshot for future "notify when new matches" (no SMTP in MVP — stored + logged).
CREATE TABLE price_alert_subscription (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(320) NOT NULL,
    filter_json JSONB NOT NULL,
    locale VARCHAR(16) NOT NULL DEFAULT 'de-CH',
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_price_alert_sub_email ON price_alert_subscription(email);
CREATE INDEX idx_price_alert_sub_created ON price_alert_subscription(created_at DESC);
