# EuropeBikeFinder (Bike Finder Europa)

Price-intelligence platform for Swiss buyers: crawls EU e-bike listings, computes CHF landed price vs Swiss reference, search + filters.

**Source of truth:** `BikeFinder_Europa_Dev_Spec_v1.docx` + `EuropeBikeFinder_Requirements_Hamza_EN_v1.docx`.

## MVP scope (aligned with dev spec)

- **Phase 1:** Rebike.de, Grüne-Welle.de, eBay Kleinanzeigen DE (e-bikes); Spring Boot API + PostgreSQL/pgvector + Next.js UI; magic-link–ready user model; **saved searches persisted**; **e-mail alert dispatch only** (no third-party chat bots or messaging platforms in this codebase).
- **de-CH** UI copy, Swiss CHF formatting (`1'234.56`), nDSG pages to add before launch.

## Repository layout

| Path | Stack |
|------|--------|
| `backend/` | Java 21, Spring Boot 3.3, Flyway, JPA |
| `frontend/` | Next.js 14 (App Router), Tailwind, next-intl — URLs **`/de-CH/…`** (default) and **`/en/…`** (language switcher) |

**Deploy (GitHub + Vercel + API):** Vercel runs **`frontend/`** only; the API + PostgreSQL need a separate host. **[DEPLOY.md](./DEPLOY.md)** · **[docs/SCOPE_AND_PHASE2.md](./docs/SCOPE_AND_PHASE2.md)** (V1 scope, limitations, Phase 2 roadmap — **keep updated**) · **`frontend/.env.production.example`** · Docker: **`backend/Dockerfile`** · Render: **[infra/render.yaml](./infra/render.yaml)** · CI: **`./scripts/verify-build.sh`** (same as GitHub Actions).

**First push to GitHub:** use **`europe-bikefinder/`** as the repo root (not your home folder). Then `git init`, `git add -A`, verify no secrets in `git status`, commit, add `origin`, `git push -u origin main`. Run **`./scripts/verify-build.sh`** before or after push.

### Implemented in backend (beyond CRUD)

- **ECB EUR→CHF**: daily fetch from ECB XML → `fx_rate` (`EUR_CHF`), scheduled **07:00 Europe/Zurich**, plus fetch on startup if the table is empty.
- **Pricing**: landed CHF = EUR×rate + shipping (by source country) + import estimate (dev spec §6.2); **discount vs Swiss** from `swiss_price_reference` when brand/category/tier match; **bargain** flag vs configurable threshold.
- **API**: `GET /api/v1/system/fx/eur-chf` (latest rate), `POST /api/v1/system/fx/refresh` (manual ECB pull + reprice — dev helper). **`GET /api/v1/offers/{id}/similar`** — comparable listings (brand/model/category/motor, landed CHF band, year ±2) without embeddings until the vector pipeline is enabled.
- **Wunsch-Velo criteria (PatrickBike spec)**: Same logical dimensions as the Wunsch form — **staff UI** at **`/suche`** (dropdowns + fields) filters `bike_offer` via **`GET /api/v1/offers`** query params (`brand`, `model`, `bikeCategory`, `bikeCondition`, `motorBrand`, `motorPosition`, `minBatteryWh`, `maxLandedPriceChf`, `maxMileageKm`, `countryCode`, `warrantyPresent=true`, `bargainOnly=true`). Persisting a client case: `POST /api/v1/bike-wishes` (JSON), optional XML: `POST /api/v1/bike-wishes/xml`. Sample XML: `docs/examples/wunschvelo-example.xml`. Product notes: `docs/PATRICK_WUNSCH_AND_SOURCES.md`.

---

## Run locally (recommended)

**Prerequisites:** [Docker Desktop](https://www.docker.com/products/docker-desktop/) running, **JDK 21** (system install, Homebrew `temurin@21`, or a JDK unpacked under `europe-bikefinder/.jdks/`).

**Terminal 1 — API**

```bash
cd "/path/to/europe-bikefinder"
chmod +x start-backend.sh
./start-backend.sh
```

This script: starts Postgres + Redis, waits until the DB is ready, skips starting if the API is **already healthy** on port 8080, and errors clearly if **8080 is taken by something else**.

**Terminal 2 — Web UI**

```bash
cd "/path/to/europe-bikefinder"
chmod +x start-frontend.sh
./start-frontend.sh
```

Open **http://localhost:3000** (redirects to **`/de-CH`**). By default the UI **hides Flyway demo listings** (`bike_offer.is_demo`), so the home page can look **empty until you import real rows**. **Wunsch-Suche:** e.g. http://localhost:3000/de-CH/suche or http://localhost:3000/en/suche

### Load real listings (Shopify crawl)

**Operations:** see **`docs/CRAWL_RUNBOOK.md`** (includes **non-Shopify + classifieds** status). **Robots quick check:** `./scripts/verify-crawl-robots.sh`. **Smoke Shopify batch:** `./scripts/smoke-crawl-shopify.sh`. **Full marketplace (Shopify + heuristics + skips):** `POST /api/v1/system/crawl/marketplace-all`.

### Load real listings (Rebike-only — legacy shorthand)

This fills Postgres with **live** `rebike.de` product pages (JSON-LD), priced in CHF via ECB FX. Needs **internet** and **~1–2 minutes**.

1. **Infrastructure + API (two terminals)**  
   - `./scripts/dev-all.sh` — starts Postgres/Redis and prints follow-up commands.  
   - In the API shell: **`export EBF_DEV_OPEN_SYSTEM_ENDPOINTS=true`** then **`./start-backend.sh`** — enables system endpoints without `X-Import-Token` (**localhost only; never in production**).

2. **Frontend:** `./start-frontend.sh` (or `cd frontend && npm run dev`).

3. **Import offers:** **`chmod +x scripts/load-real-data.sh`** once, then **`./scripts/load-real-data.sh`** — waits for `/actuator/health`, then **`POST /api/v1/system/crawl/rebike`**. You should see JSON with **`imported` > 0**.

4. Reload **http://localhost:3000/de-CH** or **/en** — real rows appear (non-demo). To **also** show the old Flyway seed row for debugging, set **`EBF_SEARCH_INCLUDE_DEMO_LISTINGS=true`** on the **API** (see `ebf.search` in `application.yml`).

**Shorthand:** same crawl as step 3 without the health wait: **`./scripts/rebike-crawl-local.sh`**.

**Production / staging:** do **not** set `EBF_DEV_OPEN_SYSTEM_ENDPOINTS`. Use **`EBF_IMPORT_TOKEN`** and pass **`X-Import-Token`** to the crawl endpoint (or automate imports from your own pipeline). Respect **`robots.txt`** for the target shop.

**Keep data fresh:** set **`EBF_CRAWL_ENABLED=true`** so the scheduled job in `ebf.crawl.cron` runs (see `application.yml`). Manual crawl still works via `POST /api/v1/system/crawl/rebike` regardless of that flag.

**Checks**

- Health: http://localhost:8080/actuator/health  
- Wish search API: http://localhost:8080/api/v1/offers/wish (strict + near-match fallback)  
- Offers (filterable): http://localhost:8080/api/v1/offers — e.g. `?bikeCategory=city&maxLandedPriceChf=3000&brand=Specialized`  
- Similar (no embeddings): `GET /api/v1/offers/{uuid}/similar` — e.g. after you have listing UUIDs from `/offers`
- **Competitor watch (Hamza 5):** UI at **`/competitor-watch`**; API `GET /api/v1/competitor-watch/dashboard` and `GET /api/v1/competitor-watch/history/{slug}` — when **`EBF_COMPETITOR_WATCH_ENABLED=true`**, default **daily 08:00 Europe/Zurich** (see `ebf.competitor-watch` in `application.yml`). Manual: **`POST /api/v1/system/competitor-watch/run`** (same **`X-Import-Token`** / dev-open rules as import). Heuristic **listing-link count** on each shop URL + delta vs last run; **not** “new listings this week” until you add stable product-ID diffing. Scope / Phase 2: **[docs/SCOPE_AND_PHASE2.md](./docs/SCOPE_AND_PHASE2.md)**.  
- **Procurement sources** (B2B directory seeds): http://localhost:8080/api/v1/sources — also see `config/sourcing-sources.yaml`  
- Wunsch persist (optional): `POST http://localhost:8080/api/v1/bike-wishes` (JSON: `contactEmail`, optional name/phone, `payload` = Wunsch-shaped profile)  
- Alerts: `POST http://localhost:8080/api/v1/alert-subscriptions` — `{ "email", "filter": { … }, "locale" }` (optional SMTP: `ebf.mail.*`).  
- **Patrick pricing (PriceSense):** `POST /api/v1/price-sense/recommend` — JSON `{"brand","model","modelYear","conditionGrade":"B","buyInCostChf":1800}` → recommended CHF list price (median −10%, floor 30% on buy-in).  
- **Real data (CLI):** `./scripts/load-real-data.sh` — waits for health, then **Rebike crawl** (same as `POST /api/v1/system/crawl/rebike`).  
- Dev import: `POST /api/v1/system/import-offers` — with **`EBF_DEV_OPEN_SYSTEM_ENDPOINTS=true`** no header; otherwise `X-Import-Token` when `EBF_IMPORT_TOKEN` is set.  
- **Rebike crawl (raw):** `POST /api/v1/system/crawl/rebike` — same auth as import. Optional: **`EBF_CRAWL_ENABLED=true`** for the **scheduled** job (see `ebf.crawl.*` in `application.yml`).

---

## Manual commands (same result)

1. **Infrastructure**

   ```bash
   cd europe-bikefinder
   docker compose up -d
   ```

2. **Backend**

   ```bash
   cd backend
   chmod +x mvnw run-dev.sh run-tests.sh
   ./run-dev.sh spring-boot:run
   ```

   **Tests:** from `backend/`, run `./run-tests.sh` (same as `./run-dev.sh test`) — requires **JDK 21** (`brew install temurin@21` or a JDK under `europe-bikefinder/.jdks/`). With **Docker Desktop** running:  
   `docker run --rm -v "$(pwd):/work" -w /work maven:3.9.9-eclipse-temurin-21 mvn -q test`

3. **Frontend**

   ```bash
   cd frontend
   cp .env.local.example .env.local   # once
   npm install
   npm run dev
   ```

---

## When something goes wrong

| Symptom | What to do |
|--------|------------|
| `Cannot connect to the Docker daemon` | Open **Docker Desktop** and wait until it is running, then retry. |
| `JDK 21 not found` | Install Temurin 21 from [adoptium.net](https://adoptium.net/) or `brew install temurin@21`, **or** unpack a macOS JDK 21 tarball under `europe-bikefinder/.jdks/`. |
| `Port 8080 was already in use` | Another process uses 8080. Either stop it, or run `./start-backend.sh` (it exits cleanly if **this** API is already up), or use `SERVER_PORT=8081 ./start-backend.sh` and set `NEXT_PUBLIC_API_BASE_URL=http://localhost:8081` in `frontend/.env.local`. |
| Empty database / reset | `docker compose down -v` removes the Postgres volume (deletes all data), then `docker compose up -d` and start the backend again so Flyway reapplies migrations including the dev seed. |
| Next.js dev error `Cannot find module './vendor-chunks/@swc.js'`, **`Cannot find module './682.js'`** (or any missing numbered chunk under `.next/server`) | Stale dev cache. Stop `next dev`, run **`cd frontend && npm run clean`** (or `rm -rf frontend/.next`), then **`npm run dev`** again. Shortcut: **`npm run dev:clean`**. If it keeps happening, avoid spaces in the project path (e.g. move the repo out of `fl p2`) or upgrade Next.js. |

---

## Open risks (from dev spec)

- Verify each source’s `robots.txt` before crawling.
- Landed price / import lines are **Schätzungen** — label in UI; verify customs rules at ezv.admin.ch.

---

## Recently added (product backlog closure — partial)

The following is **implemented in-repo**; several items remain **out of scope** for a single codebase (see below).

| Area | What was added |
|------|----------------|
| **Legal / UX** | de-CH + en pages **`/privacy`**, **`/imprint`**, footer links, disclaimer strip (estimates / customs hint). |
| **Price alerts** | Table `price_alert_subscription`, **`POST /api/v1/alert-subscriptions`** (e-mail + `filter` JSON). Optional **SMTP**: `EBF_MAIL_ENABLED=true`, **`spring.mail.host`** (e.g. `MAIL_HOST`), **`ebf.mail.api-base-url`** for unsubscribe links; welcome mail on subscribe; **daily digest** (`ebf.mail.digest-cron`); **`GET /api/v1/alert-subscriptions/unsubscribe?token=…`**. |
| **Bulk import (dev/ops)** | **`POST /api/v1/system/import-offers`** with header **`X-Import-Token`** when **`EBF_IMPORT_TOKEN`** is set. Body: `{ "sourceId": "uuid", "offers": [ … ] }`. Sample: `backend/src/main/resources/fixtures/import-sample.json`. |
| **Sources catalogue** | Flyway **V7** seeds extra `source` rows (crawl **off** until enabled in DB / robots checked). |
| **Shopify crawls** | Same pipeline for Rebike, Upway (DE/NL/FR/CH), LikedBikes, Rebike CH — see `ebf.crawl` in `application.yml`. Manual **`POST /api/v1/system/crawl/shopify-all`** or per-endpoint; scheduled when **`EBF_CRAWL_ENABLED=true`**. |
| **Import pipeline** | `BikeOffer.createImported` + `OfferImportService` + repricing via `PricingService`. |
| **PriceSense (PatrickBike)** | **`POST /api/v1/price-sense/recommend`** — competitive **list price**: optional **on-demand live checks** on `competitor_watch_target` shops (Shopify-style `/search` → product JSON-LD) when **`EBF_PRICESENSE_LIVE=true`** (`ebf.pricesense.live-competitor-search.*`); else median from **`bike_offer`** (CH if **≥ N** rows, else DE EUR→CHF fallback). Then **10% below** (configurable) + **30% margin floor** on buy-in. UI shows per-shop live rows. Flyway **V15** adds `live_price_probe_enabled`. Same **`X-Staff-Token`** when **`EBF_STAFF_API_TOKEN`** is set. |

### Staff wish search & procurement UX (implemented)

- **`GET /api/v1/offers/wish`**: strict filters on `bike_offer`; if nothing matches and **`ebf.search.near-match-fallback`** is true, a **relaxed** query runs (wider price, mileage, no category/condition/motor-position, etc.).
- **Demo listings**: Flyway dev seed row is flagged **`bike_offer.is_demo`**; hidden from search unless **`EBF_SEARCH_INCLUDE_DEMO_LISTINGS=true`**.
- **Optional API lock**: set **`EBF_STAFF_API_TOKEN`** — then **`X-Staff-Token`** (or `Authorization: Bearer`) is required for **`GET /api/v1/offers`**, **`/offers/wish`**, **`POST /api/v1/price-sense/recommend`**, and **`/api/v1/competitor-watch/*`**. Set the same value in **`frontend/.env.local`** as **`EBF_STAFF_API_TOKEN`** for server-side fetches.
- **Optional UI gate for `/suche`**: set **`STAFF_UI_PASSWORD`** in the Next.js env; users hit **`/{locale}/staff-login`** first (httpOnly cookie).

### Still not done here (needs separate effort)

- **Full VeloIntel spec** (DataForSEO, weekly Monday e-mail digest, Ricardo API harvest, geo/brand/accessories/SEO modules, LLMLayer) — **not** this repo. **Competitor Watch** here remains **HTTP snapshots + heuristics**; optionally **`POST /api/v1/competitor-watch/brief`** ( **`ANTHROPIC_API_KEY`**, optional **`PERPLEXITY_API_KEY`**) generates an on-demand Markdown brief in the **Module 5** UI — **no Telegram**.
- **Production crawlers** beyond Rebike: **one adapter per marketplace** (Kleinanzeigen, Upway, eBay, Swiss portals, …), rate limits, deduplication at scale — Rebike remains the reference implementation. Source rows for Grüne Welle, Ricardo CH, Tutti, Velocorner, Upway CH are **seeded** (`crawl_enabled: false`) in Flyway **V13** + `config/sourcing-sources.yaml`.
- **Production SMTP** for alerts (set `EBF_MAIL_ENABLED=true` and `spring.mail.*` / `MAIL_*`); digests are scheduled daily (configurable). Unsubscribe: `GET /api/v1/alert-subscriptions/unsubscribe?token=…` (link in digest e-mails).
- **End-user auth** (magic link) and `bf_user` UI; **saved_search** tied to users (tables exist in V1; API/UI not wired).
- **Semantic / vector search** over listings: `bike_offer.embedding` + pgvector exist; populate embeddings and swap **`/offers/{id}/similar`** to vector KNN when ready.
- **Production deploy**, secrets, monitoring.

## License

Proprietary — Nexilon / Hamza Bikes. Internal use unless otherwise agreed.
