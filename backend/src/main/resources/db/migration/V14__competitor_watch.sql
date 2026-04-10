-- Automated watch for PatrickBike / Hamza “5 main competitors” (snapshot + delta; no Telegram).
CREATE TABLE competitor_watch_target (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    slug VARCHAR(64) NOT NULL UNIQUE,
    display_name VARCHAR(200) NOT NULL,
    watch_url TEXT NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE competitor_watch_snapshot (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    target_id UUID NOT NULL REFERENCES competitor_watch_target(id) ON DELETE CASCADE,
    captured_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    http_status INT,
    listing_count_estimate INT,
    delta_vs_previous INT,
    summary_json JSONB,
    error_message TEXT,
    duration_ms INT
);

CREATE INDEX idx_cws_target_time ON competitor_watch_snapshot(target_id, captured_at DESC);

-- Hamza competitor set (VeloIntel Module 5). URLs are public shop fronts; tune per robots.txt review.
INSERT INTO competitor_watch_target (id, slug, display_name, watch_url, active, created_at, updated_at)
VALUES
    ('f0000001-0000-0000-0000-000000000001'::uuid, 'veloplus', 'Veloplus Occasions', 'https://www.veloplus.ch', true, now(), now()),
    ('f0000001-0000-0000-0000-000000000002'::uuid, 'upway_ch', 'Upway CH', 'https://upway.ch', true, now(), now()),
    ('f0000001-0000-0000-0000-000000000003'::uuid, 'rebike_ch', 'Rebike CH', 'https://rebike.ch', true, now(), now()),
    ('f0000001-0000-0000-0000-000000000004'::uuid, 'bibibike', 'BibiBike', 'https://www.bibibike.ch', true, now(), now()),
    ('f0000001-0000-0000-0000-000000000005'::uuid, 'velocorner', 'Velocorner', 'https://www.velocorner.ch', true, now(), now())
ON CONFLICT (slug) DO NOTHING;
