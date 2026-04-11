-- Veloplus + BibiBike CH rows for JSON-LD / HTML heuristic crawls. Enable Velocorner CH import alongside V18 Shopify batch.

INSERT INTO source (id, name, country_code, type, base_url, crawl_enabled, refresh_interval_min, robots_compliant, created_at, updated_at)
VALUES
    ('a0000005-0000-0000-0000-000000000001'::uuid, 'Veloplus CH', 'CH', 'dealer_marketplace', 'https://www.veloplus.ch', true, 720, true, now(), now()),
    ('a0000005-0000-0000-0000-000000000002'::uuid, 'BibiBike CH', 'CH', 'refurbisher', 'https://www.bibibike.ch', true, 720, true, now(), now())
ON CONFLICT (id) DO NOTHING;

UPDATE source SET crawl_enabled = true, updated_at = now()
WHERE id = 'a0000004-0000-0000-0000-000000000004'::uuid;
