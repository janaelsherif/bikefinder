-- B2B-style source catalogue (crawl off until robots verified). IDs fixed for stable imports.
INSERT INTO source (id, name, country_code, type, base_url, crawl_enabled, refresh_interval_min, robots_compliant, created_at, updated_at)
VALUES
    ('a0000002-0000-0000-0000-000000000001'::uuid, 'eBay Kleinanzeigen DE', 'DE', 'classifieds', 'https://www.kleinanzeigen.de', false, 360, true, now(), now()),
    ('a0000002-0000-0000-0000-000000000002'::uuid, 'Marktplaats NL', 'NL', 'classifieds', 'https://www.marktplaats.nl', false, 360, true, now(), now()),
    ('a0000002-0000-0000-0000-000000000003'::uuid, 'LeBonCoin FR', 'FR', 'classifieds', 'https://www.leboncoin.fr', false, 360, true, now(), now()),
    ('a0000002-0000-0000-0000-000000000004'::uuid, 'Subito IT', 'IT', 'classifieds', 'https://www.subito.it', false, 360, true, now(), now()),
    ('a0000002-0000-0000-0000-000000000005'::uuid, 'willhaben AT', 'AT', 'classifieds', 'https://www.willhaben.at', false, 360, true, now(), now()),
    ('a0000002-0000-0000-0000-000000000006'::uuid, 'BikeFair NL', 'NL', 'dealer_marketplace', 'https://www.bikefair.org', false, 720, true, now(), now())
ON CONFLICT (id) DO NOTHING;
