/** Mirrors backend {@code PriceSenseResponse} / {@code ComparableListingDto} JSON. */
export type PriceSenseComparable = {
  sourceName: string;
  countryCode: string;
  brand: string;
  model: string;
  modelYear: number | null;
  bikeCondition: string;
  landedPriceChf: number | null;
  sourceUrl: string;
};

export type PriceSenseLiveProbeRow = {
  slug: string;
  displayName: string;
  priceChf: number | null;
  productUrl: string | null;
  errorMessage: string | null;
};

export type PriceSenseResponseJson = {
  pMedianChf: number | null;
  pP25Chf: number | null;
  pP75Chf: number | null;
  gradeAdjustedBenchmarkChf: number | null;
  pTargetRawChf: number | null;
  pFloorChf: number | null;
  pRecommendChf: number | null;
  maxBuyInChf: number | null;
  grossMarginPct: number | null;
  marginConflict: boolean;
  marginMessage: string | null;
  nSwissListings: number;
  nGermanListings: number;
  fallbackUsed: boolean;
  confidence: string;
  eurChfRateUsed: number | null;
  swissPremiumFactorApplied: number | null;
  importAllowanceChf: number | null;
  insufficientData: boolean;
  explanation: string | null;
  sampleComparables: PriceSenseComparable[];
  liveProbes: PriceSenseLiveProbeRow[];
  liveBenchmarkUsed: boolean;
};
