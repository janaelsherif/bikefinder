-- Turn on HTML crawl for Shopify sources wired in application.yml (Rebike DE was already on in V3).
-- Roll back in DB if you need imports off: UPDATE source SET crawl_enabled = false WHERE id = ...

UPDATE source
SET crawl_enabled = true, updated_at = now()
WHERE id IN (
    'a0000004-0000-0000-0000-000000000006'::uuid,
    'a0000003-0000-0000-0000-000000000008'::uuid,
    'a0000003-0000-0000-0000-000000000011'::uuid,
    'a0000004-0000-0000-0000-000000000005'::uuid,
    'a0000003-0000-0000-0000-000000000007'::uuid,
    'a0000004-0000-0000-0000-000000000007'::uuid
);
