# Crawl operations (Shopify sources)

## What is on

- **Scheduled:** set `EBF_CRAWL_ENABLED=true` and deploy. Cron: `ebf.crawl.cron` (default 03:00 Europe/Zurich).
- **Manual (Shopify only):** `POST /api/v1/system/crawl/shopify-all`.
- **Manual (everything — Shopify + BibiBike / Veloplus / Velocorner + classified skips):** `POST /api/v1/system/crawl/marketplace-all` — same auth as import.
- **Manual (single):** `POST /api/v1/system/crawl/rebike`, `POST /api/v1/system/crawl/upway-de`.

## Checklist before production

1. **Robots:** Run `./scripts/verify-crawl-robots.sh` and confirm our crawler user-agent is allowed for product URLs you need. The app also enforces robots at runtime (`RobotsAllowService`).
2. **Database:** `source.crawl_enabled` must be `true` for a source to import. Flyway **V18** enables the configured Shopify rows; turn off per row if needed:  
   `UPDATE source SET crawl_enabled = false WHERE id = '…'::uuid;`

## Smoke test (local)

1. Start Postgres (`docker compose up -d`), then the API.
2. `chmod +x scripts/smoke-crawl-shopify.sh && ./scripts/smoke-crawl-shopify.sh`
3. Check JSON: each `runs[].result` should show `imported` / `skipped` / reasons.

## Non-Shopify & classifieds (status)

Product context (V1 vs Phase 2): **[SCOPE_AND_PHASE2.md](./SCOPE_AND_PHASE2.md)**.

**Implemented** (heuristic crawls — see `ebf.crawl.json-ld-link-targets` + `velocorner` in `application.yml`):

- **BibiBike CH** — regex link discovery on `/bikes` + JSON-LD `Product` import.
- **Veloplus CH** — regex on category seeds + JSON-LD on product pages.
- **Velocorner CH** — listing links on `bicycle-marketplace` + HTML price (`span.text-brand-green`) + `h1` (no JSON-LD).

**Explicit skips** (server GET blocked or unusable): **Ricardo CH**, **Tutti CH**, **Kleinanzeigen DE** — runs return `skipped` with reason until an **API** or **browser automation** path exists (Phase 2).

| Area | Examples | Notes |
|------|-----------|--------|
| Classifieds | Ricardo, Tutti, Kleinanzeigen | Cloudflare / anti-bot on search HTML — not imported in V1. |
| Grüne Welle (DE) | DB seed | Confirm live URL; then Shopify vs custom. |

**Related:** `competitor_watch` provides **snapshot / count** signals; **`POST /api/v1/system/crawl/marketplace-all`** runs **Shopify + heuristic + skips**.

## Grüne Welle URL

The seed row `Grüne Welle DE` may point at a **parked/outdated** domain. Fix `base_url` in `source` after you confirm the correct live shop URL (do not crawl until verified).
