# Product scope, limitations & Phase 2

**Maintainers:** When you change what the product imports, monitors, or promises in the UI, **update this file** so deployers and stakeholders stay aligned.

---

## V1 (shipped — complete at handoff)

| Area | What you get |
|------|----------------|
| **Sources** | Configured EU specialist shops & marketplaces (Shopify-style + selected non-Shopify adapters where feasible). Listings in Postgres with CHF-oriented pricing. |
| **App** | Home listings, wish-style **search**, **competitive pricing** tool, **competitor watch** page, **B2B sourcing directory** (`/sourcing` — EU platform reference for procurement; not live crawl data), legal pages, **de-CH** / **en**. |
| **Competitor Watch** | Scheduled **snapshots** of five competitor shop fronts — **activity estimate** + **delta vs last run** (runs in the **always-on API**, not on a static frontend host). |
| **Staff mode** | Optional browser password + API token for internal screens. |

V1 is **done** when the repo is deployed with **[DEPLOY.md](../DEPLOY.md)**; no mandatory code work remains for launch.

---

## Limitations (ongoing — not “Phase 2” bugs)

| Topic | Reality |
|--------|---------|
| **Third-party sites** | HTML and robots rules **change**; crawls may need parser fixes over time. |
| **Listing counts** | Heuristic counts and deltas are **signals**, not audited inventory audits. |
| **“New listings this week”** | Needs **stable product IDs** and diffing — not the same as snapshot deltas today. |

---

## Phase 2 (optional — update this table when priorities change)

These are **not** missing V1 deliverables; they need **new data, partnerships, or product decisions**.

| Area | Goal | Typical dependency |
|------|------|---------------------|
| **Classifieds** | Ricardo CH, Tutti CH, Kleinanzeigen DE (or subsets) | Official **API** / **feed**, or **approved** automation; sites often block bulk scraping. |
| **Competitor Watch — pricing** | e.g. **−10% vs overlap models** | **Price history** per model/SKU + overlap definition. |
| **Competitor Watch — market noise** | e.g. **Ricardo/Tutti campaign** detection | Ad/marketing **data source** or manual process. |
| **Competitor Watch — geo** | e.g. **~10 km dealer overlap** | **Geo/PLZ** on listings + PatrickBike overlap rules. |
| **Grüne Welle DE** | Live import | Confirm **live shop URL** in DB; then Shopify vs custom adapter (**[CRAWL_RUNBOOK](./CRAWL_RUNBOOK.md)**). |
| **Embeddings / similarity** | Richer “similar bikes” | Optional **OpenAI** / vector pipeline if product wants it. |

**Technical detail (non-Shopify crawls, skips, marketplace-all):** **[CRAWL_RUNBOOK.md](./CRAWL_RUNBOOK.md)** — section *Non-Shopify & classifieds*.

---

## After deploy

One-time steps and env vars: **[DEPLOY.md](../DEPLOY.md)**. Optional: enable **competitor watch** and **crawl** schedules on the API host.
