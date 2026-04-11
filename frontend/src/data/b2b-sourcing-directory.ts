/**
 * B2B BIKE Sourcing Directory — procurement reference (aligned with Hamza research).
 * Rows are reference links; crawlers/import coverage varies (see docs/SCOPE_AND_PHASE2.md).
 */

export type SourcingConfidence = "HIGH" | "MEDIUM" | "LOW";

export type SourcingTableRow = {
  platform: string;
  /** Canonical shop URL */
  url: string;
  type: string;
  priceRangeEur: string;
  b2bAccess: string;
  confidence: SourcingConfidence;
};

export type SourcingRegionId =
  | "de"
  | "nl"
  | "fr"
  | "it"
  | "at"
  | "accessories";

export const SOURCING_REGION_ORDER: SourcingRegionId[] = [
  "de",
  "nl",
  "fr",
  "it",
  "at",
  "accessories",
];

export const b2bSourcingRows: Record<SourcingRegionId, SourcingTableRow[]> = {
  de: [
    {
      platform: "Rebike",
      url: "https://www.rebike.com",
      type: "Certified Refurbisher",
      priceRangeEur: "800–2,800",
      b2bAccess: "Trade enquiry via form; Decathlon-backed (Jan 2026)",
      confidence: "HIGH",
    },
    {
      platform: "Jobrad Loop",
      url: "https://www.jobrad-loop.com",
      type: "Refurbisher / Lease Returns",
      priceRangeEur: "900–2,500",
      b2bAccess: "B2B dealer portal available",
      confidence: "HIGH",
    },
    {
      platform: "eBay Kleinanzeigen",
      url: "https://www.kleinanzeigen.de",
      type: "Private Classifieds",
      priceRangeEur: "400–2,200",
      b2bAccess: 'No formal B2B; high volume — filter by "Händler"',
      confidence: "HIGH",
    },
    {
      platform: "eBay.de (E-Bikes)",
      url: "https://www.ebay.de",
      type: "Classifieds + Dealer Outlet",
      priceRangeEur: "500–3,000",
      b2bAccess: "Dealer seller accounts; PowerSeller bulk",
      confidence: "HIGH",
    },
    {
      platform: "Fahrrad.de (outlet)",
      url: "https://www.fahrrad.de",
      type: "Dealer Overstock",
      priceRangeEur: "700–2,500",
      b2bAccess: "Trade/wholesale inquiries accepted",
      confidence: "HIGH",
    },
    {
      platform: "ROSE Bikes (outlet)",
      url: "https://www.rosebikes.com",
      type: "Dealer Overstock",
      priceRangeEur: "700–2,500",
      b2bAccess: "Trade/wholesale inquiries accepted",
      confidence: "HIGH",
    },
    {
      platform: "Velokontor",
      url: "https://www.velokontor.de",
      type: "Dealer Outlet / Overstock",
      priceRangeEur: "600–2,000",
      b2bAccess: "B2B available for dealers",
      confidence: "MEDIUM",
    },
    {
      platform: "Hood.de",
      url: "https://www.hood.de",
      type: "Classifieds Marketplace",
      priceRangeEur: "400–1,800",
      b2bAccess: "Dealer accounts supported",
      confidence: "MEDIUM",
    },
    {
      platform: "LikedBikes",
      url: "https://www.likedbikes.com",
      type: "Certified Refurbisher",
      priceRangeEur: "1,000–2,800",
      b2bAccess: "Wholesale/bulk inquiry possible",
      confidence: "HIGH",
    },
  ],
  nl: [
    {
      platform: "Marktplaats",
      url: "https://www.marktplaats.nl",
      type: "Classifieds (dominant NL)",
      priceRangeEur: "400–2,500",
      b2bAccess: "Dealer accounts; high-volume seller features",
      confidence: "HIGH",
    },
    {
      platform: "BikeFair",
      url: "https://www.bikefair.org",
      type: "Classifieds Aggregator",
      priceRangeEur: "500–2,200",
      b2bAccess: "Marketplace for dealers and private",
      confidence: "HIGH",
    },
    {
      platform: "Upway NL",
      url: "https://www.upway.nl",
      type: "Certified Refurbisher",
      priceRangeEur: "900–2,600",
      b2bAccess: "B2B/bulk purchase possible via trade contact",
      confidence: "HIGH",
    },
    {
      platform: "2dehands.nl",
      url: "https://www.2dehands.nl",
      type: "Classifieds",
      priceRangeEur: "300–1,800",
      b2bAccess: "High volume; no formal B2B but dealer listing",
      confidence: "MEDIUM",
    },
    {
      platform: "Fietsenwinkel (outlet)",
      url: "https://www.fietsenwinkel.nl",
      type: "Dealer Outlet",
      priceRangeEur: "700–2,400",
      b2bAccess: "Trade/reseller inquiries accepted",
      confidence: "MEDIUM",
    },
  ],
  fr: [
    {
      platform: "Upway FR",
      url: "https://upway.co/fr",
      type: "Certified Refurbisher",
      priceRangeEur: "900–2,800",
      b2bAccess: "B2B trade contact available; operates across FR",
      confidence: "HIGH",
    },
    {
      platform: "LeBonCoin",
      url: "https://www.leboncoin.fr",
      type: "Classifieds (dominant FR)",
      priceRangeEur: "300–2,200",
      b2bAccess: 'Dealer "Pro" accounts available',
      confidence: "HIGH",
    },
    {
      platform: "Fnac (outlet)",
      url: "https://www.fnac.com",
      type: "Dealer Overstock",
      priceRangeEur: "700–2,500",
      b2bAccess: "Trade accounts for resellers",
      confidence: "MEDIUM",
    },
    {
      platform: "Darty (outlet)",
      url: "https://www.darty.com",
      type: "Dealer Overstock",
      priceRangeEur: "700–2,500",
      b2bAccess: "Trade accounts for resellers",
      confidence: "MEDIUM",
    },
    {
      platform: "Vinted / Vestiaire (bikes)",
      url: "https://www.vinted.fr",
      type: "Classifieds",
      priceRangeEur: "300–1,200",
      b2bAccess: "Consumer-oriented; low B2B value",
      confidence: "LOW",
    },
    {
      platform: "Alltricks.fr (outlet)",
      url: "https://www.alltricks.fr",
      type: "Dealer Outlet / Overstock",
      priceRangeEur: "600–2,000",
      b2bAccess: "Trade price possible at volume",
      confidence: "MEDIUM",
    },
  ],
  it: [
    {
      platform: "Subito.it",
      url: "https://www.subito.it",
      type: "Classifieds (dominant IT)",
      priceRangeEur: "300–2,000",
      b2bAccess: 'Dealer "Pro" seller accounts',
      confidence: "HIGH",
    },
    {
      platform: "eBay.it",
      url: "https://www.ebay.it",
      type: "Classifieds + Dealer",
      priceRangeEur: "400–2,200",
      b2bAccess: "Dealer account; PowerSeller",
      confidence: "HIGH",
    },
    {
      platform: "Bikestrade.it",
      url: "https://www.bikestrade.it",
      type: "B2B Wholesale/Trade Directory",
      priceRangeEur: "800–2,500",
      b2bAccess: "Explicitly B2B: trade accounts, bulk pricing",
      confidence: "HIGH",
    },
    {
      platform: "OLX Italy",
      url: "https://www.olx.it",
      type: "Classifieds",
      priceRangeEur: "300–1,500",
      b2bAccess: "Dealer listings possible",
      confidence: "MEDIUM",
    },
    {
      platform: "Bici.it",
      url: "https://www.bici.it",
      type: "Classifieds",
      priceRangeEur: "400–1,800",
      b2bAccess: "Mixed consumer/dealer",
      confidence: "MEDIUM",
    },
  ],
  at: [
    {
      platform: "willhaben.at",
      url: "https://www.willhaben.at",
      type: "Classifieds (dominant AT)",
      priceRangeEur: "400–2,200",
      b2bAccess: "Dealer Pro accounts available",
      confidence: "HIGH",
    },
    {
      platform: "eBay.at",
      url: "https://www.ebay.at",
      type: "Classifieds + Dealer",
      priceRangeEur: "500–2,500",
      b2bAccess: "Full dealer functionality",
      confidence: "HIGH",
    },
    {
      platform: "Shpock",
      url: "https://www.shpock.com",
      type: "Classifieds (AT-origin app)",
      priceRangeEur: "300–1,500",
      b2bAccess: "Consumer-dominated; lower B2B value",
      confidence: "MEDIUM",
    },
    {
      platform: "Sport-Eybl / Intersport AT (outlet)",
      url: "https://www.sport-eybl.at",
      type: "Dealer Overstock",
      priceRangeEur: "700–2,200",
      b2bAccess: "Trade inquiries for bulk",
      confidence: "MEDIUM",
    },
    {
      platform: "Fahrradmarkt.at",
      url: "https://www.fahrradmarkt.at",
      type: "Dedicated Bike Classifieds",
      priceRangeEur: "400–1,800",
      b2bAccess: "Dealer listings; niche but targeted",
      confidence: "HIGH",
    },
  ],
  accessories: [
    {
      platform: "Velomarkt.ch / EU wholesale",
      url: "https://www.velomarkt.ch",
      type: "Accessories Aggregator",
      priceRangeEur: "—",
      b2bAccess: "Racks, lights, bags",
      confidence: "HIGH",
    },
    {
      platform: "Bike-Discount.de",
      url: "https://www.bike-discount.de",
      type: "Dealer Outlet",
      priceRangeEur: "—",
      b2bAccess: "Full accessories range; volume discounts",
      confidence: "HIGH",
    },
    {
      platform: "Alltricks.fr",
      url: "https://www.alltricks.fr",
      type: "Dealer Outlet",
      priceRangeEur: "—",
      b2bAccess: "Helmets, cargo, lights; trade pricing",
      confidence: "MEDIUM",
    },
    {
      platform: "Probikeshop.fr",
      url: "https://www.probikeshop.fr",
      type: "Wholesale/Pro",
      priceRangeEur: "—",
      b2bAccess: "Pro accounts for dealers",
      confidence: "MEDIUM",
    },
    {
      platform: "Bikestrade.it",
      url: "https://www.bikestrade.it",
      type: "B2B Trade",
      priceRangeEur: "—",
      b2bAccess: "Cargo boxes, child seats",
      confidence: "HIGH",
    },
    {
      platform: "Fahrrad-XXL.de",
      url: "https://www.fahrrad-xxl.de",
      type: "Dealer Overstock",
      priceRangeEur: "—",
      b2bAccess: "Full accessories incl. cargo accessories",
      confidence: "MEDIUM",
    },
  ],
};
