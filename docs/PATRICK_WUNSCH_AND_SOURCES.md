# PatrickBike Wunsch-Velo, sourcing URLs & intelligence docs

This file consolidates handover material from **B2B BIKE Sourcing Directory**, **5 competitors**, **PatrickBike WunschVelo HTML/spec**, **VeloIntel SaaS Design**, and **PriceSense** — and explains how they connect to **EuropeBikeFinder** (EU sourcing / CHF landed price).

---

## 1. Wunsch-Velo — same spec as **search criteria** (not a “new” form)

- **Canonical UI/spec:** The existing **PatrickBike Wunsch-Velo** definition (Sections A–I, **dropdowns** where specified, conditional E-Bike / Cargo branches, exact vs open specificity). We **reuse this field set**; we do **not** design a parallel, different form.
- **Who fills what:** Clients may still complete the form on **patrickbike.ch** (`/velo-wunsch/`). In **EuropeBikeFinder**, the primary interaction we care about is **Hamza/staff**: they **choose from dropdowns and enter data** that mirrors those same fields, in order to **search** listings (EU crawl + Swiss sources) **according to the client’s needs**. The form is the **schema for “what to look for”**, not a separate product artifact.
- **Input modes (same logical payload):** (1) **Structured entry** in-app — dropdowns + fields → query/match; (2) optional **XML import** (`POST /api/v1/bike-wishes/xml`) when the same data arrives as a file/integration.
- **Conditional logic (from HTML/spec):** Use-case drives **E-Bike** (E) and **Cargo/Family** (F); “exact vs open” toggles brand/model vs free-text; Velo-Typ can trigger E/F.
- **Privacy:** nDSG, Swiss hosting, hCaptcha on the public WordPress form; in-app staff search must still respect purpose limitation and retention for any personal data tied to a client case.

**Relation to EuropeBikeFinder:** A Wunsch payload is a **structured search profile**. It drives **filters** against `bike_offer` via **`GET /api/v1/offers`** (query parameters mirror the staff UI on **`/suche`**: Velo-Kategorie, Motor, Akku min., Landpreis max., etc.). Persisting a case for CRM/follow-up remains **`POST /api/v1/bike-wishes`**. Swiss pipelines (Ricardo/Tutti) stay a separate data path — one canonical JSON shape whether the data came from the client form, staff UI, or XML.

---

## 2. Hamza / procurement — crawl-ready URLs by country

From **B2B BIKE Sourcing Directory** (confidence HIGH unless noted). **Canonical machine-readable list:** `config/sourcing-sources.yaml` (same content seeded into DB `source` via Flyway **V7–V8**). **always verify `robots.txt` and terms** before production crawl.

**API:** `GET /api/v1/sources` lists all rows (name, `country_code`, `type`, `base_url`, `crawl_enabled`) for crawler wiring.

### Germany (primary)

| Platform            | URL (seed)        | Type                    |
|---------------------|-------------------|-------------------------|
| Rebike              | rebike.com        | Certified refurbisher   |
| Jobrad Loop         | jobrad-loop.com   | Refurbisher / lease     |
| eBay Kleinanzeigen  | kleinanzeigen.de  | Classifieds             |
| eBay.de E-Bikes     | ebay.de           | Classifieds + outlet    |
| Fahrrad.de / ROSE   | fahrrad.de, rosebikes.com | Dealer overstock |
| Velokontor          | velokontor.de     | Dealer outlet           |
| Hood.de             | hood.de           | Classifieds             |
| LikedBikes          | likedbikes.com    | Certified refurbisher   |

### Netherlands

| Platform     | URL             | Type          |
|--------------|-----------------|---------------|
| Marktplaats  | marktplaats.nl  | Classifieds   |
| BikeFair     | bikefair.org    | Aggregator    |
| Upway NL     | upway (ops NL)  | Refurbisher   |
| 2dehands     | 2dehands.nl     | Classifieds   |
| Fietsenwinkel| fietsenwinkel.nl| Dealer outlet |

### France

| Platform   | URL           | Type           |
|------------|---------------|----------------|
| Upway FR   | upway.co/fr   | Refurbisher    |
| LeBonCoin  | leboncoin.fr  | Classifieds    |
| Fnac/Darty | fnac.com, darty.com | Outlet   |
| Alltricks  | alltricks.fr  | Dealer outlet  |

### Italy

| Platform     | URL            | Type        |
|--------------|----------------|-------------|
| Subito       | subito.it      | Classifieds |
| eBay.it      | ebay.it        | Mixed       |
| Bikestrade   | bikestrade.it  | B2B trade   |
| OLX / Bici   | olx.it, bici.it| Classifieds |

### Austria

| Platform      | URL             | Type        |
|---------------|-----------------|-------------|
| willhaben     | willhaben.at    | Classifieds |
| eBay.at       | ebay.at         | Mixed       |
| Shpock        | shpock.com      | Classifieds |
| Fahrradmarkt  | fahrradmarkt.at | Classifieds |

### Accessories (wholesale / cross-border)

bike-discount.de, alltricks.fr, probikeshop.fr, bikestrade.it, fahrrad-xxl.de, velomarkt.ch (CH reference).

---

## 3. Five competitors to monitor (Hamza / Patrick context)

Aligned with **VeloIntel Module 5 — Competitor Watch** (Swiss pre-owned / certified overlap):

| Competitor           | Monitor                          | Source / scraper idea        | Example alert        |
|----------------------|----------------------------------|------------------------------|----------------------|
| Veloplus Occasions   | New listings, price, warranty    | occasionen.veloplus.ch       | 5+ new in category   |
| Upway.ch             | Inventory, promos, brands        | upway.ch                     | Price drop >10% overlap |
| Rebike.ch            | CH-deliverable, territory        | rebike.ch/ch                 | Delivery expansion   |
| BibiBike             | Volume, overlap brands           | bibibike.ch                  | Campaign / ads spike |
| Velocorner dealers   | Basel-area overlap               | velocorner.ch by PLZ         | Listing within 10 km |

**EuropeBikeFinder** focuses on **EU listings → CHF landed**; **VeloIntel** focuses on **Swiss demand, SEO, Ricardo/Tutti, competitor inventory**. Together: EU arbitrage (Hamza) + Swiss market position (Patrick).

---

## 4. VeloIntel — complementary to “Velo Finder”

- **Weekly automation:** Sunday harvest → LLM analysis → **Monday digest** (email + dashboard).
- **Modules:** Brand, **Pricing**, Geo, Accessories, **Competitor**, SEO — uses Ricardo API, Velocorner, Tutti, Velomarkt, Digitec, **DataForSEO**, **LLMLayer** (Claude + Perplexity).
- **On-demand:** e.g. `/pricing brand model` style queries (90s) — conceptually close to **PriceSense** and to our **hybrid search** on EuropeBikeFinder.

**Reuse:** ECB daily rate, PostgreSQL, median/P25/P75 language — already aligned with our **PricingService** and `swiss_price_reference`.

---

## 5. PriceSense — complementary pricing module

- **Question:** “What price should I list at?”
- **Swiss path:** Velocorner + Ricardo + Upway → median → **10% below median** with **margin floor** (buy-in × 1.30).
- **Fallback:** DE sources (Rebike, Kleinanzeigen, …) + **EUR→CHF (ECB)** + **CH factor** (default 1.20) + import allowance — same *economic* logic family as our **landed CHF** for EU buys, different formula (resale vs procurement).

**Integration idea:** Share **ECB `fx_rate`**, condition grades, and brand/category normalisation between apps via one DB or API contract.

---

## 6. XML import — contract (v1)

- Namespace (optional): `https://patrickbike.ch/ns/wunschvelo/1`
- Root: `<bikeWish version="1">`
- **Required:** `contact/email`, `contact/phone`, `contact/fullName`, `contact/preferredChannel`, `budget/chfBand`, `urgency`, `useCase`
- **Conditional blocks:** `eBike`, `cargoFamily` — same triggers as the HTML form
- **Example:** see `docs/examples/wunschvelo-example.xml`

Manual JSON and XML imports both map to the same `payload_json` shape stored in **`bike_wish_submission`**.
