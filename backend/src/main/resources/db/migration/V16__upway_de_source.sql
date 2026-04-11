-- Upway DE (Shopify) for shared HTML + JSON-LD crawl. crawl_enabled = false until ops enable after robots review.

INSERT INTO source (id, name, country_code, type, base_url, crawl_enabled, refresh_interval_min, robots_compliant, created_at, updated_at)
VALUES
    ('a0000004-0000-0000-0000-000000000006'::uuid, 'Upway DE', 'DE', 'refurbisher', 'https://www.upway.de', false, 720, true, now(), now())
ON CONFLICT (id) DO NOTHING;
