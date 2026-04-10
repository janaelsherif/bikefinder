-- Reference CH price for discount % on the dev-seed bike (Specialized city mid).
INSERT INTO swiss_price_reference (id, brand, bike_category, spec_tier, median_chf, p25_chf, p75_chf, updated_at)
VALUES (
    'c0000001-0000-0000-0000-000000000001'::uuid,
    'Specialized',
    'city',
    'mid',
    3400.00,
    2900.00,
    4200.00,
    now()
)
ON CONFLICT (brand, bike_category, spec_tier) DO NOTHING;
