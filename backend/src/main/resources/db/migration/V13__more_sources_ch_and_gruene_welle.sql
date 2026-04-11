-- Grüne Welle (DE) + Swiss marketplace catalogue rows for future PriceSense / CH pipelines.
-- crawl_enabled = false until robots.txt is verified per site.

INSERT INTO source (id, name, country_code, type, base_url, crawl_enabled, refresh_interval_min, robots_compliant, created_at, updated_at)
VALUES
    ('a0000004-0000-0000-0000-000000000001'::uuid, 'Grüne Welle DE', 'DE', 'dealer_marketplace', 'https://www.gruene-welle.de', false, 720, true, now(), now()),
    ('a0000004-0000-0000-0000-000000000002'::uuid, 'Ricardo CH', 'CH', 'classifieds', 'https://www.ricardo.ch', false, 360, true, now(), now()),
    ('a0000004-0000-0000-0000-000000000003'::uuid, 'Tutti CH', 'CH', 'classifieds', 'https://www.tutti.ch', false, 360, true, now(), now()),
    ('a0000004-0000-0000-0000-000000000004'::uuid, 'Velocorner CH', 'CH', 'dealer_marketplace', 'https://www.velocorner.ch', false, 720, true, now(), now()),
    ('a0000004-0000-0000-0000-000000000005'::uuid, 'Upway CH', 'CH', 'refurbisher', 'https://upway.ch', false, 720, true, now(), now())
ON CONFLICT (id) DO NOTHING;
