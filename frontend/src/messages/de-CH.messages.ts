/** German (CH) UI strings (TS module avoids Next dev webpack JSON chunk bugs with next-intl). */
const deCH = {
  Home: {
    title: "EuropeBikeFinder",
    subtitle:
      "E-Bike-Angebote aus Europa — Preis in CHF inkl. Schätzung Import & Versand.",
    empty:
      "Keine Angebote zum Anzeigen. Die Datenbank enthält noch keine importierten Listings — nach einem Rebike-Crawl oder Import erscheinen echte Inserate hier (siehe README: «Load real listings»).",
    error:
      "API nicht erreichbar. Läuft das Backend (Port 8080) und Docker (Postgres)?",
    cta: "Zum Angebot",
    bargain: "Schnäppchen",
    topDeal: "Top Deal",
    discountVsCh: "unter CH-Markt",
    toWunschSearch: "Nach Kundenwunsch suchen (Wunsch-Velo-Kriterien) →",
  },
  Nav: {
    brand: "EuropeBikeFinder",
    offers: "Angebote",
    wunschSearch: "Wunsch-Suche",
    competitivePricing: "Marktpreis",
    language: "Sprache",
    locale_deCH: "Deutsch (CH)",
    locale_en: "English",
  },
  Search: {
    title: "Nach Kundenwunsch suchen",
    subtitle:
      "Dropdowns und Angaben entsprechen der Wunsch-Velo-Logik (Velo-Typ, Motor, Akku, Budget). Es werden passende EU-Angebote aus der Datenbank gefiltert.",
    brand: "Marke",
    model: "Modell",
    bikeCategory: "Velo-Kategorie",
    bikeCondition: "Zustand",
    motorBrand: "Motor-Marke",
    motorPosition: "Motor-Lage",
    motorMid: "Mittelmotor",
    motorRear: "Nabenmotor hinten",
    motorFront: "Vorderrad",
    minBatteryWh: "Mind. Akku (Wh)",
    maxLandedPriceChf: "Max. Landpreis (CHF)",
    maxMileageKm: "Max. Kilometerstand",
    countryCode: "Herkunftsland Angebot",
    warrantyPresent: "Nur mit Garantie",
    bargainOnly: "Nur Schnäppchen",
    any: "Alle",
    budget800: "bis 800",
    budget1500: "bis 1'500",
    budget3000: "bis 3'000",
    budget5000: "bis 5'000",
    budget8000: "bis 8'000",
    submit: "Treffer anzeigen",
    reset: "Filter zurücksetzen",
    results: "Trefferliste",
    noResults:
      "Keine Angebote mit diesen Kriterien. Filter lockern oder später erneut prüfen.",
    total: "{count} Angebote gesamt",
    phBrand: "z.B. Specialized",
    phModel: "z.B. Turbo Vado",
    phMotor: "z.B. Bosch",
    phKm: "z.B. 3000",
    catCity: "City",
    catTrekking: "Trekking",
    catCargo: "Cargo",
    catMtb: "MTB",
    catRoad: "Rennvelo",
    catGravel: "Gravel",
    catKids: "Kinder",
    condNew: "Neu",
    condLikeNew: "Wie neu",
    condRefurbished: "Refurbished",
    condUsed: "Gebraucht",
    exactHeading: "Exakte Treffer",
    nearBanner:
      "Keine exakten Treffer — die nächstbesten Alternativen (gelockerte Kategorie, Preis, Kilometer, usw.).",
    noneAfterNear:
      "Noch nichts Passendes. Filter lockern oder später erneut prüfen, wenn mehr Bestand importiert ist.",
  },
  StaffLogin: {
    title: "Interner Zugang",
    hint: "Die Wunsch-Suche ist nur für interne Beschaffung.",
    password: "Passwort",
    submit: "Weiter",
    error: "Falsches Passwort.",
    home: "Zur Startseite",
  },
  Meta: {
    description:
      "Finde günstige E-Bikes aus Deutschland und der EU — verglichen mit typischen Schweizer Preisen.",
  },
  Disclaimer: {
    body: "Hinweis: Landpreis in CHF, Import und Versand sind Schätzungen. Vor einem Kauf die aktuellen Zoll- und MWSt-Regeln (z. B. ezv.admin.ch) und die Anbieterbedingungen prüfen.",
  },
  Legal: {
    footerNote: "© EuropeBikeFinder — interne Preisübersicht",
    privacyTitle: "Datenschutz",
    privacyLead:
      "Kurzfassung (keine Rechtsberatung): Personendaten werden nur für den Zweck verarbeitet, den Sie mit der Nutzung angeben (z. B. gespeicherte Suchfilter für Benachrichtigungen).",
    privacyBody: `E-Mail-Adressen aus «Benachrichtigung»-Anfragen werden in der Datenbank gespeichert, bis Sie die Löschung verlangen, den Link in einer Benachrichtigungs-E-Mail zur Abmeldung nutzen oder der Zweck entfällt. Wenn der Betreiber SMTP konfiguriert, können Bestätigungs- und Treffer-E-Mails (nur E-Mail, keine anderen Kanäle) versendet werden. Serverstandort und Auftragsverarbeiter sind vor Produktivbetrieb mit Ihrer Datenschutzstelle abzustimmen (Schweiz: nDSG).

Logfiles und technische Metadaten können wie beim Hosting-Provider üblich anfallen.`,
    imprintTitle: "Impressum",
    imprintBody: `Angaben gemäss Schweizer Recht — Platzhalter bis zur finalen Firma:

Verantwortlich: [Firma / PatrickBike — eintragen]
Adresse: [einsetzen]
Kontakt: [einsetzen]

Haftung für Links: Trotz sorgfältiger inhaltlicher Kontrolle keine Haftung für externe Links.`,
  },
  CompetitivePricing: {
    title: "Wettbewerbspreis (PriceSense)",
    subtitle:
      "Velo erfassen, das du eingekauft oder bepreisen willst. Wenn die API Live-Suche aktiviert hat, holen wir aktuelle Listenpreise von konfigurierten CH-Konkurrenz-Shops für diese Anfrage, sonst Datenbank-Vergleiche.",
    introTitle: "Zwei verschiedene «%»-Angaben",
    introBody:
      "Auf der Startseite bedeutet «% unter CH-Markt» den Vergleich des geschätzten Landpreises CHF mit einem Schweizer Referenz-Median aus swiss_price_reference (Marke/Kategorie/Stufe) — das ist nicht dasselbe wie dieses Tool.\n\nDieses Tool bevorzugt On-Demand-Live-Checks auf Konkurrenz-Shops (wenn serverseitig aktiv), danach bike_offer: Median vergleichbarer Landpreise CHF, Zustand A–D, Zielrabatt vs. Benchmark und Mindestmarge.",
    brand: "Marke",
    model: "Modell",
    modelYear: "Modelljahr (optional)",
    modelYearHint:
      "Leer lassen, um das Jahr nicht zu filtern (Marke/Modell trotzdem).",
    condition: "Zustandsstufe",
    conditionHint:
      "A = bester Zustand, D = stärkere Gebrauchsspuren — gemappt auf Konditions-Stufen in der DB.",
    buyIn: "Total Einkauf (CHF)",
    buyInHint: "Einkauf + Aufbereitung + Teile, in CHF (deine Kostenbasis).",
    submit: "Markt & Empfehlung anzeigen",
    loading: "Berechne…",
    errorApi: "Pricing-API nicht erreichbar. Läuft das Backend?",
    errorHttp: "Anfrage fehlgeschlagen",
    validateForm: "Bitte Marke, Modell und Einkauf ausfüllen.",
    benchmarkTitle: "Markt-Benchmark (vergleichbare Listings)",
    median: "Median CHF",
    p25: "25. Perzentil",
    p75: "75. Perzentil",
    recommendTitle: "Vorgeschlagener Wettbewerbs-Listenpreis (CHF)",
    recommendHint:
      "Basierend auf Benchmark, Zustandsfaktor, Zielrabatt vs. Median und Mindestmarge auf den Einkauf (auf dem Server konfigurierbar).",
    floor: "Mindestpreis (Marge)",
    margin: "Bruttomarge vs. Einkauf",
    confidence: "Konfidenz",
    swissCount: "CH-Listings genutzt",
    deCount: "DE-Listings im Pool",
    fallback: "DE-Fallback (zu wenig CH)",
    comparablesTitle: "Beispiel-Konkurrenzlistings (aus der DB)",
    colSource: "Quelle",
    colCountry: "Land",
    colBike: "Velo",
    colYear: "Jahr",
    colCond: "Zustand",
    colPrice: "Landpreis CHF",
    linkListing: "Öffnen",
    noComparables: "Keine Beispielzeilen.",
    insufficientTitle: "Zu wenig vergleichbare Daten",
    marginConflict: "Mindestmarge angewendet",
    gradeA: "Wie neu / top",
    gradeB: "Typischer Refurb",
    gradeC: "Gebraucht, mehr Abrieb",
    gradeD: "Starker Gebrauch",
    liveProbesTitle: "Live-Konkurrenz-Checks (diese Anfrage)",
    liveBenchmarkBadge: "Benchmark nutzt Live-Median dieser Shops",
    colLiveShop: "Shop",
    colLivePrice: "Listenpreis CHF",
    colLiveLink: "Inserat",
    colLiveError: "Hinweis",
  },
  Alert: {
    title: "E-Mail-Benachrichtigung",
    hint: "Speichert Ihre E-Mail mit den aktuellen Suchfiltern. Sie erhalten eine Bestätigung und bei neuen Treffern eine kurze E-Mail (wenn der Server SMTP nutzt). Keine anderen Kanäle.",
    email: "E-Mail",
    emailPlaceholder: "sie@beispiel.ch",
    submit: "Eintragen",
    success: "Gespeichert. Bestätigung per E-Mail, sofern Versand aktiv ist.",
    error: "Konnte nicht speichern. API erreichbar?",
  },
} as const;

export default deCH;
