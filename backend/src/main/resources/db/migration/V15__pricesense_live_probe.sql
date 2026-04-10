-- On-demand PriceSense: probe these shop fronts for live list prices (in addition to bike_offer fallback).
ALTER TABLE competitor_watch_target
    ADD COLUMN live_price_probe_enabled BOOLEAN NOT NULL DEFAULT TRUE;

COMMENT ON COLUMN competitor_watch_target.live_price_probe_enabled IS
    'When true, PriceSense live search attempts this target (Shopify-style /search + product JSON-LD).';
