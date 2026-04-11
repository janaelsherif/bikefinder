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
    toSourcingDirectory: "B2B-Sourcing-Verzeichnis (Einkauf) →",
    listingsHeading: "Aktuelle Angebote",
    heroKicker: "Beschaffungs-Intelligence",
  },
  Nav: {
    brand: "EuropeBikeFinder",
    offers: "Angebote",
    wunschSearch: "Wunsch-Suche",
    competitivePricing: "Marktpreis",
    competitorWatch: "Konkurrenz-Monitor",
    sourcingDirectory: "Sourcing-Verzeichnis",
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
  CompetitorWatch: {
    title: "Konkurrenz-Monitor",
    subtitle:
      "PatrickBike / Hamza — Modul 5: Veloplus, Upway, Rebike, BibiBike und Velocorner wöchentlich beobachten und frühe Signale aus automatischen Snapshots sichtbar machen.",
    missionTitle: "Fragen dieses Moduls",
    qInventory:
      "Was haben Veloplus, Upway, Rebike und BibiBike diese Woche an Lagerbestand / neuen Inseraten dazugetan?",
    qPricing: "Hat ein Wettbewerber seine Preislogik merklich geändert?",
    qMarket:
      "Kommen neue Anbieter im Markt für zertifizierte Occasion-Bikes in Basel / Zürich dazu?",
    briefTitle: "Was zu beobachten ist (Hamza-Brief)",
    colCompetitor: "Wettbewerber",
    colMonitor: "Fokus",
    colSource: "Quelle / Signal",
    colAlert: "Alert (Zielbild)",
    rowVeloplus: {
      monitor: "Neue Occasion-Inserate, Preisänderungen, Garantie-Hinweise",
      source: "Veloplus-Shopfront (HTTP-Snapshot; konsistent mit Occasions-Logik)",
      alert: "≥5 neue Listings vs. vorheriger Lauf in überlappenden Kategorien (Heuristik)",
    },
    rowUpway: {
      monitor: "CH-Sortiment, neue Marken, Aktionen",
      source: "upway.ch Storefront-Snapshot",
      alert: "Preissenkung >10 % bei gleichen Modellen (Preisreihe nötig — separat via DB / PriceSense)",
    },
    rowRebike: {
      monitor: "CH-lieferbare Modelle, Markenbreite",
      source: "rebike.ch Snapshot",
      alert: "Sachliche Änderung bei CH-Lieferung / Sortiment (qualitativ; Site prüfen)",
    },
    rowBibibike: {
      monitor: "Listings-Volumen, Preise bei gemeinsamen Marken",
      source: "bibibike.ch Snapshot",
      alert: "Klassifizierte Kampagnen (Ricardo/Tutti) — hier nicht automatisch erkannt",
    },
    rowVelocorner: {
      monitor: "Händler-Inserate vs. PatrickBike-Overlap (Raum Basel)",
      source: "velocorner.ch Marketplace-Snapshot",
      alert: "Direktes Overlap ~10 km / PLZ — mit Geo-Filtern in der Beschaffung verfeinern",
    },
    nameVeloplus: "Veloplus Occasions",
    nameUpway: "Upway CH",
    nameRebike: "Rebike CH",
    nameBibibike: "BibiBike",
    nameVelocorner: "Velocorner",
    signalsTitle: "Live-Signale aus dem System",
    signalsHint:
      "Pro Lauf wird die öffentliche Shopfront geladen (robots.txt beachtet), die sichtbare Inseratezahl geschätzt und die Differenz zum letzten Lauf gespeichert. Ein Hinweis-Badge erscheint, wenn die Änderung den konfigurierten Schwellwert erreicht (Standard 5), analog zu den Server-Logs.",
    colLastSnapshot: "Letzter Snapshot",
    colEstimate: "Inserate (Schätzung)",
    colDelta: "Δ zum Vorlauf",
    colHttp: "HTTP",
    colDuration: "Dauer",
    signalAlert: "Schwelle — prüfen",
    signalCalm: "Im erwarteten Band",
    openShop: "Shop öffnen",
    noSnapshot:
      "Noch keine Snapshots. Competitor-Watch auf der API aktivieren und einen Lauf auslösen (Cron oder manueller POST).",
    error:
      "Konkurrenz-Daten nicht ladbar. Läuft das Backend und ist ggf. X-Staff-Token gesetzt?",
    historyTitle: "Letzte Snapshots",
    historyWhen: "Zeit",
    historyEstimate: "Anzahl",
    historyDelta: "Δ",
    technicalNote:
      "Feinere Alerts aus dem Brief (z. B. −10 % Preis, Ricardo-Kampagnen, 10 km Overlap) brauchen zusätzliche Daten oder manuelle Prüfung; diese Seite zeigt die automatische Inserate-Telemetrie, die das Backend bereits speichert.",
    aiBriefTitle: "KI-Konkurrenz-Brief (Claude + optional Perplexity)",
    aiBriefHint:
      "Erzeugt ein kurzes Markdown-Briefing aus den neuesten Snapshot-Daten. Optional: Fokus ergänzen (z. B. «Flyer in Basel»). Auf der API braucht es ANTHROPIC_API_KEY; PERPLEXITY_API_KEY liefert aktuellen Schweizer Marktkontext. Kein Telegram — nur Web.",
    aiBriefFocusPlaceholder: "Optionaler Fokus für das Modell (Marke, Ort, Frage …)",
    aiBriefButton: "Brief erzeugen",
    aiBriefLoading: "Wird erzeugt…",
    aiBriefError: "Brief fehlgeschlagen:",
    aiBriefPerplexityNote: "Web-Kontext enthalten",
    aiBriefNoPerplexityNote: "Perplexity übersprungen oder nicht verfügbar",
  },
  Sourcing: {
    heroKicker: "Beschaffung",
    title: "B2B-Sourcing-Verzeichnis (E-Bikes)",
    subtitle:
      "Referenzliste EU-Plattformen für die Beschaffung: Inserate, Refurbisher, Händler-Outlets und Hinweise zum B2B-Zugang. Links öffnen in einem neuen Tab. In-app-Listings stammen nur von konfigurierten Crawls — dieses Verzeichnis hilft bei der Einkaufsplanung.",
    colPlatform: "Plattform",
    colType: "Typ",
    colPrice: "Preisspanne (EUR)",
    colB2b: "B2B-Zugang",
    colConfidence: "Vertrauen",
    confHigh: "Hoch",
    confMedium: "Mittel",
    confLow: "Tief",
    footnote:
      "Benchmarks sind indikative Research-Spannen, keine Live-Quotes. Nutzungsbedingungen und robots.txt der Ziele beachten. Inseratsvolumen ≠ automatischer Import in diesem Produkt — siehe Scope-Dokument.",
    regions: {
      de: {
        title: "Deutschland — Haupt-Beschaffungsmarkt",
        intro:
          "Markt Nr. 1 — rund 42 % des EU-E-Bike-Volumens; starke Überbestände 2023–2024 machen ihn zum zentralen Beschaffungsfeld.",
        benchmark:
          "DE-Benchmark (indikativ): Bosch Mittelmotor-Pendler, ~500 Wh, refurbished mit Garantie → oft EUR 1’400–2’200 (~CHF 1’550–2’450 landed — deutlich unter typischem CH-Retail).",
      },
      nl: {
        title: "Niederlande — Volumen & Qualität",
        intro:
          "Hohe E-Bike-Durchdringung und ausgereifte Austauschzyklen — gut für nahezu-neue und Leasing-Rückläufer.",
        benchmark:
          "NL-Benchmark (indikativ): gleiche Klasse oft EUR 1’200–2’000 — teils unter DE durch Leasing-Rückläufer.",
      },
      fr: {
        title: "Frankreich — Subventionen & Second-Hand",
        intro:
          "Subventionshistorie speist grossen Markt an Trade-ins und Outlet-Flow.",
        benchmark:
          "FR-Benchmark (indikativ): oft EUR 1’300–2’400 — häufig 20–30 % unter typischem CH-Retail vergleichbarer Klasse.",
      },
      it: {
        title: "Italien — Urban & Classifieds",
        intro:
          "Grosser Privatmarkt und Händlerpräsenz; je nach Kategorie attraktiv vs. DE/NL.",
        benchmark:
          "IT-Benchmark (indikativ): oft EUR 1’000–1’900 — stark abhängig von Premium vs. Volumenmarken.",
      },
      at: {
        title: "Österreich — nah an der CH",
        intro:
          "Gut für Logistik Richtung CH; solide Classifieds und Händler-Outlets.",
        benchmark:
          "AT-Benchmark (indikativ): oft EUR 1’300–2’300 — kleinerer Markt als DE, aber nah für CH-Sourcing.",
      },
      accessories: {
        title: "Zubehör — Grosshandel & Outlets (Cross-Border)",
        intro:
          "Parallelquellen für Gepäckträger, Licht, Cargo usw. — ergänzend zur Velo-Beschaffung.",
        benchmark:
          "Dealer-/Pro-Konten wo möglich; MOQ und Exportbedingungen pro Lieferant prüfen.",
      },
    },
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
