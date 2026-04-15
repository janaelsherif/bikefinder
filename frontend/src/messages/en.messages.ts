/** English UI strings (TS module avoids Next dev webpack JSON chunk bugs with next-intl). */
const en = {
  Home: {
    title: "EuropeBikeFinder",
    subtitle:
      "E-bike listings from Europe — price in CHF including estimated import & shipping.",
    empty:
      'Nothing to show yet. The database has no imported listings — run a Rebike crawl or import so real offers appear here (see README: "Load real listings").',
    error:
      "API unreachable. Is the backend (port 8080) and Docker (Postgres) running?",
    cta: "View listing",
    bargain: "Bargain",
    topDeal: "Top deal",
    discountVsCh: "below CH market",
    toWunschSearch: "Search by client wish (wish-bike criteria) →",
    toSourcingDirectory: "B2B sourcing directory (where to buy) →",
    listingsHeading: "Latest listings",
    heroKicker: "Procurement intelligence",
  },
  Nav: {
    brand: "EuropeBikeFinder",
    offers: "Listings",
    wunschSearch: "Wish search",
    competitivePricing: "Competitive pricing",
    competitorWatch: "Competitor watch",
    sourcingDirectory: "Sourcing directory",
    login: "Log in",
    logout: "Log out",
    language: "Language",
    locale_deCH: "German (CH)",
    locale_en: "English",
  },
  SortBar: {
    label: "Sort by",
    newest: "Newest first",
    priceAsc: "Price: low to high",
    priceDesc: "Price: high to low",
    countryAsc: "Country: A–Z",
    countryDesc: "Country: Z–A",
  },
  CountryFilter: {
    label: "Country",
    any: "All countries",
  },
  Search: {
    title: "Search by client wish",
    subtitle:
      "Dropdowns and fields follow the wish-bike logic (bike type, motor, battery, budget). Matching EU listings are filtered from the database.",
    brand: "Brand",
    model: "Model",
    bikeCategory: "Bike category",
    bikeCondition: "Condition",
    motorBrand: "Motor brand",
    motorPosition: "Motor position",
    motorMid: "Mid-drive",
    motorRear: "Rear hub",
    motorFront: "Front hub",
    minBatteryWh: "Min. battery (Wh)",
    maxLandedPriceChf: "Max. landed price (CHF)",
    maxMileageKm: "Max. mileage",
    countryCode: "Listing country",
    country_CH: "Switzerland",
    country_DE: "Germany",
    country_AT: "Austria",
    country_FR: "France",
    country_IT: "Italy",
    country_NL: "Netherlands",
    country_BE: "Belgium",
    country_ES: "Spain",
    country_PT: "Portugal",
    country_PL: "Poland",
    country_CZ: "Czechia",
    country_DK: "Denmark",
    country_SE: "Sweden",
    country_FI: "Finland",
    country_IE: "Ireland",
    country_LU: "Luxembourg",
    warrantyPresent: "With warranty only",
    bargainOnly: "Bargains only",
    any: "Any",
    budget800: "up to 800",
    budget1500: "up to 1,500",
    budget3000: "up to 3,000",
    budget5000: "up to 5,000",
    budget8000: "up to 8,000",
    submit: "Show results",
    reset: "Clear filters",
    results: "Results",
    noResults:
      "No listings match these filters. Loosen filters or try again later.",
    total: "{count} listings total",
    phBrand: "e.g. Specialized",
    phModel: "e.g. Turbo Vado",
    phMotor: "e.g. Bosch",
    phKm: "e.g. 3000",
    catCity: "City",
    catTrekking: "Trekking",
    catCargo: "Cargo",
    catMtb: "MTB",
    catRoad: "Road",
    catGravel: "Gravel",
    catKids: "Kids",
    condNew: "New",
    condLikeNew: "Like new",
    condRefurbished: "Refurbished",
    condUsed: "Used",
    exactHeading: "Exact matches",
    nearBanner:
      "No exact matches — showing the closest alternatives (relaxed category, price, mileage, etc.).",
    noneAfterNear:
      "Nothing close enough yet. Loosen filters or check back after more stock is ingested.",
  },
  StaffLogin: {
    title: "Staff access",
    hint: "Wish search is for internal procurement only.",
    password: "Password",
    submit: "Continue",
    error: "Incorrect password.",
    errorNotConfigured:
      "Sign-in is not configured (missing STAFF_UI_PASSWORD). Add it to .env.local and restart the dev server.",
  },
  Meta: {
    description:
      "Find e-bikes from Germany and the EU at attractive prices — compared to typical Swiss market levels.",
  },
  Disclaimer: {
    body: "Note: CHF landed price, import and shipping are estimates. Before buying, check current customs/VAT rules (e.g. ezv.admin.ch) and seller terms.",
  },
  Legal: {
    footerNote: "© EuropeBikeFinder — internal price overview",
    privacyTitle: "Privacy",
    privacyLead:
      "Summary (not legal advice): we process personal data only for the purpose you trigger by using the feature (e.g. storing search filters for notifications).",
    privacyBody: `Email addresses from "notify me" requests are stored until you ask for deletion, use the unsubscribe link in a notification email, or the purpose ends. When the operator configures SMTP, we may send confirmation and match-summary emails (email only — no other channels). Hosting location and subprocessors must be aligned before production (Switzerland: nDSG).

Standard server logs may apply as with your hosting provider.`,
    imprintTitle: "Legal notice",
    imprintBody: `Placeholder until final company details are set:

Responsible: [PatrickBike / company — fill in]
Address: [fill in]
Contact: [fill in]

Liability for links: no responsibility for external content.`,
  },
  CompetitivePricing: {
    title: "Competitive pricing (PriceSense)",
    subtitle:
      "Enter a bike you bought or are pricing. When live search is enabled on the API, we fetch current list prices from configured Swiss competitor shops for this request, then fall back to database comparables if needed.",
    introTitle: "Two different “%” numbers on this product",
    introBody:
      "On the home listing cards, “% below CH market” compares each offer’s estimated landed CHF price to a Swiss reference median from the swiss_price_reference table (brand/category/tier) — it is not the same as this tool.\n\nThis tool prefers on-demand live checks on competitor shop fronts (when enabled server-side), then uses bike_offer rows: median comparable landed CHF (Swiss listings if enough live/DB coverage, otherwise DE listings adjusted to CHF), condition grade A–D, configurable discount vs benchmark, and margin floor on buy-in.",
    brand: "Brand",
    model: "Model",
    modelYear: "Model year (optional)",
    modelYearHint:
      "Leave empty to ignore year filter (still matches brand/model).",
    condition: "Condition grade",
    conditionHint:
      "A = best / newest, D = heavy wear — maps to condition tiers in the database.",
    buyIn: "Total buy-in (CHF)",
    buyInHint: "Purchase + refurb + parts, in CHF (your landed cost basis).",
    submit: "Show market & recommendation",
    loading: "Calculating…",
    errorApi: "Could not reach the pricing API. Is the backend running?",
    errorHttp: "Request failed",
    validateForm: "Please fill brand, model, and buy-in.",
    benchmarkTitle: "Market benchmark (comparable listings)",
    median: "Median CHF",
    p25: "25th percentile",
    p75: "75th percentile",
    recommendTitle: "Suggested competitive list price (CHF)",
    recommendHint:
      "Based on benchmark, grade adjustment, target discount vs median, and your 30% margin floor on buy-in (configurable on server).",
    floor: "Minimum price (margin floor)",
    margin: "Gross margin vs buy-in",
    confidence: "Confidence",
    swissCount: "CH listings used",
    deCount: "DE listings in pool",
    fallback: "DE fallback used (not enough CH)",
    comparablesTitle: "Sample competitor listings (from database)",
    colSource: "Source",
    colCountry: "Country",
    colBike: "Bike",
    colYear: "Year",
    colCond: "Condition",
    colPrice: "Landed CHF",
    linkListing: "Open",
    noComparables: "No sample rows returned.",
    insufficientTitle: "Not enough comparable data",
    marginConflict: "Margin floor applied",
    gradeA: "Like new / best",
    gradeB: "Typical retail refurb",
    gradeC: "Used, more wear",
    gradeD: "High wear / budget",
    liveProbesTitle: "Live competitor checks (this request)",
    liveBenchmarkBadge: "Benchmark uses live median from these shops",
    colLiveShop: "Shop",
    colLivePrice: "List price CHF",
    colLiveLink: "Listing",
    colLiveError: "Note",
  },
  CompetitorWatch: {
    title: "Competitor Watch",
    subtitle:
      "PatrickBike / Hamza — Module 5: track what Veloplus, Upway, Rebike, BibiBike, and Velocorner do each week, and surface early signals from automated snapshots.",
    missionTitle: "Questions this module answers",
    qInventory:
      "What did Veloplus, Upway, Rebike, and BibiBike add to their inventory this week?",
    qPricing: "Did any competitor change their pricing strategy?",
    qMarket:
      "Are new players entering the Basel / Zürich certified pre-owned market?",
    briefTitle: "What to monitor (Hamza brief)",
    colCompetitor: "Competitor",
    colMonitor: "What to monitor",
    colSource: "Source / signal",
    colAlert: "Alert trigger (intent)",
    rowVeloplus: {
      monitor: "New occasion listings, price changes, warranty updates",
      source: "Veloplus shop front (HTTP snapshot; aligned with occasions feed)",
      alert: "≥5 new listings vs prior snapshot in overlapping categories (count heuristic)",
    },
    rowUpway: {
      monitor: "Swiss inventory, new brands, promotions",
      source: "upway.ch storefront snapshot",
      alert: "Price drop >10% on overlapping models (needs price series — use DB + PriceSense separately)",
    },
    rowRebike: {
      monitor: "CH-deliverable inventory, brand range",
      source: "rebike.ch snapshot",
      alert: "Material change in Swiss delivery / assortment (qualitative; review site copy)",
    },
    rowBibibike: {
      monitor: "Listing volume, pricing on shared brands",
      source: "bibibike.ch snapshot",
      alert: "Spike in third-party classifieds noise (Ricardo/Tutti) — not auto-detected here",
    },
    rowVelocorner: {
      monitor: "Dealer listings vs PatrickBike overlap (Basel area)",
      source: "velocorner.ch marketplace snapshot",
      alert: "Direct overlap within ~10 km / same PLZ band — refine with geo filters in sourcing",
    },
    nameVeloplus: "Veloplus Occasions",
    nameUpway: "Upway CH",
    nameRebike: "Rebike CH",
    nameBibibike: "BibiBike",
    nameVelocorner: "Velocorner",
    signalsTitle: "Live signals from the engine",
    signalsHint:
      "Each run fetches the public shop front (robots.txt respected), estimates visible listing count, and stores the delta vs the previous run. A highlighted badge appears when the absolute change reaches the configured threshold (default 5), matching server-side logging.",
    colLastSnapshot: "Last snapshot",
    colEstimate: "Listing count (estimate)",
    colDelta: "Δ vs previous",
    colHttp: "HTTP",
    colDuration: "Duration",
    signalAlert: "Threshold crossed — review",
    signalCalm: "Within normal band",
    openShop: "Open shop",
    noSnapshot: "No snapshots yet. Enable competitor-watch on the API and run a snapshot (scheduled or manual POST).",
    error:
      "Could not load competitor watch data. Is the backend running and is X-Staff-Token configured if required?",
    historyTitle: "Recent snapshot history",
    historyWhen: "When",
    historyEstimate: "Count",
    historyDelta: "Δ",
    technicalNote:
      "Narrow alerts from the brief (e.g. −10% price, Ricardo campaigns, 10 km overlap) require extra feeds or manual review; this page exposes the automated listing-count telemetry your backend already records.",
    aiBriefTitle: "AI competitor brief (Claude + optional Perplexity)",
    aiBriefHint:
      "Generates a short Markdown briefing from the latest snapshot data. Optional: add a focus (e.g. “Flyer in Basel”). Requires ANTHROPIC_API_KEY on the API; PERPLEXITY_API_KEY adds recent Swiss market context. No Telegram — web only.",
    aiBriefFocusPlaceholder: "Optional focus for the model (brand, city, question…)",
    aiBriefButton: "Generate brief",
    aiBriefLoading: "Generating…",
    aiBriefError: "Brief failed:",
    aiBriefPerplexityNote: "Web context included",
    aiBriefNoPerplexityNote: "Perplexity skipped or unavailable",
    crawlControlsTitle: "Crawl controls",
    crawlControlsSubtitle:
      "Run crawl jobs manually and configure the daily auto-crawl schedule for the backend.",
    crawlControlsRunMarketplaceAll: "Run marketplace crawl (all sources)",
    crawlControlsRunShopifyAll: "Run Shopify crawl batch",
    crawlControlsRunRebike: "Run Rebike crawl",
    crawlControlsRunUpwayDe: "Run Upway DE crawl",
    crawlControlsAutoEnabled: "Enable auto-crawl",
    crawlControlsAutoTime: "Auto-crawl time (Europe/Zurich)",
    crawlControlsTimezone: "Timezone",
    crawlControlsSaveSettings: "Save crawl settings",
    crawlControlsLoading: "Working…",
    crawlControlsNotConfigured: "Not configured",
    crawlControlsSettingsSaved: "Crawl settings saved.",
    crawlControlsLastRun: "Last auto-crawl run",
    crawlControlsNeverRun: "Never",
    crawlControlsRunSuccessPrefix: "Crawl started:",
    crawlControlsRunErrorPrefix: "Crawl failed:",
    crawlControlsSettingsErrorPrefix: "Could not save crawl settings:",
  },
  Sourcing: {
    heroKicker: "Procurement",
    title: "B2B BIKE sourcing directory",
    subtitle:
      "Reference list of EU platforms for procurement: classifieds, refurbishers, dealer outlets, and B2B access notes. Links open in a new tab. In-app listings come from configured crawls only — use this page to plan where to source next.",
    colPlatform: "Platform",
    colType: "Type",
    colPrice: "Price range (EUR)",
    colB2b: "B2B access",
    colConfidence: "Confidence",
    confHigh: "High",
    confMedium: "Medium",
    confLow: "Low",
    footnote:
      "Benchmarks are indicative research ranges, not live quotes. Respect each site’s terms and robots.txt. Classifieds volume ≠ automated import in this product — see scope doc.",
    regions: {
      de: {
        title: "Germany — primary sourcing market",
        intro:
          "Germany is the #1 target market — roughly 42% of Europe’s e-bike volume; significant overstock pressure in 2023–2024 makes it a rich hunting ground for procurement.",
        benchmark:
          "DE price benchmark (indicative): Bosch mid-drive commuter, ~500 Wh, refurbished with warranty → often EUR 1,400–2,200 (~CHF 1,550–2,450 landed — large saving vs typical Swiss retail).",
      },
      nl: {
        title: "Netherlands — volume & quality hub",
        intro:
          "High e-bike penetration and mature replacement cycles — useful for near-new and lease-return style inventory.",
        benchmark:
          "NL benchmark (indicative): same class often EUR 1,200–2,000 — sometimes below DE due to lease-return volume.",
      },
      fr: {
        title: "France — subsidy-driven secondary supply",
        intro:
          "Strong subsidy history created a large pool of trade-ins and outlet flow on the secondary market.",
        benchmark:
          "FR benchmark (indicative): often EUR 1,300–2,400 — still often 20–30% below typical CH retail for comparable class.",
      },
      it: {
        title: "Italy — urban mobility & classifieds depth",
        intro:
          "Large private market and dealer presence; can be cost-effective for certain categories vs DE/NL.",
        benchmark:
          "IT benchmark (indicative): often EUR 1,000–1,900 for comparable segments — varies by premium vs volume brands.",
      },
      at: {
        title: "Austria — close to Switzerland",
        intro:
          "Strong for pickup logistics toward CH; fewer refurb specialists but good classifieds and dealer outlets.",
        benchmark:
          "AT benchmark (indicative): often EUR 1,300–2,300 — smaller market than DE but useful for CH-adjacent sourcing.",
      },
      accessories: {
        title: "Accessories — wholesale & outlets (cross-border)",
        intro:
          "Parallel sources for racks, lights, cargo, and dealer accessory volume — complement bike sourcing.",
        benchmark:
          "Use dealer/pro accounts where available; verify MOQ and export terms per supplier.",
      },
    },
  },
  Alert: {
    title: "Email alerts",
    hint: "Stores your email with the current search filters. You get a confirmation and short emails when new matches appear (if the server has SMTP). No other channels.",
    email: "Email",
    emailPlaceholder: "you@example.com",
    submit: "Save",
    success: "Saved. Confirmation email if outbound mail is enabled.",
    error: "Could not save. Is the API running?",
  },
} as const;

export default en;
