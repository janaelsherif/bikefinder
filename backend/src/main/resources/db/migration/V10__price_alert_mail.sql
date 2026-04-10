-- Email alert dispatch: watermark for "new since" queries; per-row unsubscribe token.
ALTER TABLE price_alert_subscription
    ADD COLUMN last_offer_watermark TIMESTAMPTZ,
    ADD COLUMN unsubscribe_token UUID NOT NULL DEFAULT gen_random_uuid();

CREATE UNIQUE INDEX idx_price_alert_sub_unsubscribe_token ON price_alert_subscription(unsubscribe_token);
