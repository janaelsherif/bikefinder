-- Full B2B sourcing directory (Hamza procurement). Matches config/sourcing-sources.yaml.
-- crawl_enabled = false until robots.txt + ToS sign-off per site.

INSERT INTO source (id, name, country_code, type, base_url, crawl_enabled, refresh_interval_min, robots_compliant, created_at, updated_at)
VALUES
    ('a0000003-0000-0000-0000-000000000001'::uuid, 'Jobrad Loop (DE)', 'DE', 'refurbisher', 'https://www.jobrad-loop.com', false, 720, true, now(), now()),
    ('a0000003-0000-0000-0000-000000000002'::uuid, 'eBay DE', 'DE', 'classifieds', 'https://www.ebay.de', false, 360, true, now(), now()),
    ('a0000003-0000-0000-0000-000000000003'::uuid, 'Fahrrad.de', 'DE', 'dealer_marketplace', 'https://www.fahrrad.de', false, 720, true, now(), now()),
    ('a0000003-0000-0000-0000-000000000004'::uuid, 'ROSE Bikes', 'DE', 'dealer_marketplace', 'https://www.rosebikes.com', false, 720, true, now(), now()),
    ('a0000003-0000-0000-0000-000000000005'::uuid, 'Velokontor', 'DE', 'dealer_marketplace', 'https://www.velokontor.de', false, 720, true, now(), now()),
    ('a0000003-0000-0000-0000-000000000006'::uuid, 'Hood.de', 'DE', 'classifieds', 'https://www.hood.de', false, 360, true, now(), now()),
    ('a0000003-0000-0000-0000-000000000007'::uuid, 'LikedBikes', 'DE', 'refurbisher', 'https://www.likedbikes.com', false, 720, true, now(), now()),
    ('a0000003-0000-0000-0000-000000000008'::uuid, 'Upway NL', 'NL', 'refurbisher', 'https://www.upway.nl', false, 720, true, now(), now()),
    ('a0000003-0000-0000-0000-000000000009'::uuid, '2dehands NL', 'NL', 'classifieds', 'https://www.2dehands.nl', false, 360, true, now(), now()),
    ('a0000003-0000-0000-0000-000000000010'::uuid, 'Fietsenwinkel NL', 'NL', 'dealer_marketplace', 'https://www.fietsenwinkel.nl', false, 720, true, now(), now()),
    ('a0000003-0000-0000-0000-000000000011'::uuid, 'Upway FR', 'FR', 'refurbisher', 'https://www.upway.co/fr', false, 720, true, now(), now()),
    ('a0000003-0000-0000-0000-000000000012'::uuid, 'Fnac FR', 'FR', 'oem_outlet', 'https://www.fnac.com', false, 720, true, now(), now()),
    ('a0000003-0000-0000-0000-000000000013'::uuid, 'Darty FR', 'FR', 'oem_outlet', 'https://www.darty.com', false, 720, true, now(), now()),
    ('a0000003-0000-0000-0000-000000000014'::uuid, 'Alltricks FR', 'FR', 'dealer_marketplace', 'https://www.alltricks.fr', false, 720, true, now(), now()),
    ('a0000003-0000-0000-0000-000000000015'::uuid, 'eBay IT', 'IT', 'classifieds', 'https://www.ebay.it', false, 360, true, now(), now()),
    ('a0000003-0000-0000-0000-000000000016'::uuid, 'Bikestrade IT', 'IT', 'dealer_marketplace', 'https://www.bikestrade.it', false, 720, true, now(), now()),
    ('a0000003-0000-0000-0000-000000000017'::uuid, 'OLX IT', 'IT', 'classifieds', 'https://www.olx.it', false, 360, true, now(), now()),
    ('a0000003-0000-0000-0000-000000000018'::uuid, 'eBay AT', 'AT', 'classifieds', 'https://www.ebay.at', false, 360, true, now(), now()),
    ('a0000003-0000-0000-0000-000000000019'::uuid, 'Shpock', 'AT', 'classifieds', 'https://www.shpock.com', false, 360, true, now(), now()),
    ('a0000003-0000-0000-0000-000000000020'::uuid, 'Fahrradmarkt AT', 'AT', 'classifieds', 'https://www.fahrradmarkt.at', false, 360, true, now(), now()),
    ('a0000003-0000-0000-0000-000000000021'::uuid, 'bike-discount.de', 'DE', 'dealer_marketplace', 'https://www.bike-discount.de', false, 720, true, now(), now()),
    ('a0000003-0000-0000-0000-000000000022'::uuid, 'Probikeshop FR', 'FR', 'dealer_marketplace', 'https://www.probikeshop.fr', false, 720, true, now(), now()),
    ('a0000003-0000-0000-0000-000000000023'::uuid, 'Fahrrad XXL DE', 'DE', 'dealer_marketplace', 'https://www.fahrrad-xxl.de', false, 720, true, now(), now()),
    ('a0000003-0000-0000-0000-000000000024'::uuid, 'Velomarkt CH', 'CH', 'classifieds', 'https://www.velomarkt.ch', false, 360, true, now(), now())
ON CONFLICT (id) DO NOTHING;
