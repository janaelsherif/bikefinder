-- Mark Flyway dev seed listing so it can be hidden from staff search (real procurement only).
ALTER TABLE bike_offer
    ADD COLUMN IF NOT EXISTS is_demo BOOLEAN NOT NULL DEFAULT false;

UPDATE bike_offer
SET is_demo = true
WHERE source_offer_id = 'dev-seed-001';
