-- Persisted crawl scheduler settings (runtime-editable via system API).

CREATE TABLE IF NOT EXISTS crawl_settings (
    id SMALLINT PRIMARY KEY,
    auto_crawl_enabled BOOLEAN NOT NULL DEFAULT false,
    auto_crawl_time TIME NOT NULL DEFAULT '03:00:00',
    timezone VARCHAR(64) NOT NULL DEFAULT 'Europe/Zurich',
    last_auto_run_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO crawl_settings (id, auto_crawl_enabled, auto_crawl_time, timezone)
VALUES (1, false, '03:00:00', 'Europe/Zurich')
ON CONFLICT (id) DO NOTHING;
