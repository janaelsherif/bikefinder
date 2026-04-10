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
  },
  Nav: {
    brand: "EuropeBikeFinder",
    offers: "Listings",
    wunschSearch: "Wish search",
    competitivePricing: "Competitive pricing",
    language: "Language",
    locale_deCH: "German (CH)",
    locale_en: "English",
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
    home: "Back to home",
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
