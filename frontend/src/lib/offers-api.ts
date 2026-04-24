import { staffFetchHeaders } from "./staff-headers";

export type OfferSummary = {
  id: string;
  sourceName: string;
  countryCode: string;
  sourceType: string;
  brand: string | null;
  model: string | null;
  modelYear: number | null;
  bikeCategory: string | null;
  bikeCondition: string | null;
  motorBrand: string | null;
  batteryWh: number | null;
  mileageKm: number | null;
  warrantyType: string | null;
  warrantyMonths: number | null;
  landedPriceChf: number | null;
  discountVsSwissPct: number | null;
  bargain: boolean;
  topDeal: boolean;
  qualityScore: number | null;
  imageUrl: string | null;
  sourceUrl: string;
  matchTier?: string | null;
};

export type SpringPage = {
  content: OfferSummary[];
  totalElements: number;
};

export type WishSearchResponse = {
  matchTier: "EXACT" | "NEAR" | "NONE";
  exact: SpringPage;
  near: SpringPage;
};

const ALLOWED_KEYS = new Set([
  "brand",
  "model",
  "bikeCategory",
  "bikeCondition",
  "motorBrand",
  "motorPosition",
  "minBatteryWh",
  "maxLandedPriceChf",
  "minDiscountVsSwissPct",
  "maxMileageKm",
  "countryCode",
  /** Discovery feed: CH, DE, AT, FR, IT, NL (server expands). */
  "nearbyMarkets",
  "warrantyPresent",
  "bargainOnly",
  "size",
  "page",
  /** Server: newest | price_asc | price_desc | country_asc | country_desc */
  "offerSort",
]);

/** Builds query string for GET /api/v1/offers from plain search params (form or URL). */
export function toOffersQueryString(
  params: Record<string, string | string[] | undefined>,
  options?: { size?: number },
): string {
  const p = new URLSearchParams();
  const size = options?.size ?? 24;
  p.set("size", String(size));

  for (const [key, raw] of Object.entries(params)) {
    if (!ALLOWED_KEYS.has(key)) {
      continue;
    }
    const val = Array.isArray(raw) ? raw[0] : raw;
    if (val === undefined || val === null || val === "") {
      continue;
    }
    p.set(key, val);
  }
  return p.toString();
}

export async function fetchOffersPage(
  params: Record<string, string | string[] | undefined>,
  options?: { size?: number },
): Promise<SpringPage | null> {
  const base =
    process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080";
  const qs = toOffersQueryString(params, { size: options?.size });
  const url = `${base}/api/v1/offers?${qs}`;
  try {
    const res = await fetch(url, {
      cache: "no-store",
      headers: staffFetchHeaders(),
    });
    if (!res.ok) {
      return null;
    }
    return res.json();
  } catch {
    return null;
  }
}

/** Browser poll via Next `/api/offers` proxy (staff token stays server-side). */
export async function fetchOffersPageViaProxy(
  params: Record<string, string | string[] | undefined>,
  options?: { size?: number },
): Promise<SpringPage | null> {
  const qs = toOffersQueryString(params, { size: options?.size });
  const url = `/api/offers?${qs}`;
  try {
    const res = await fetch(url, { cache: "no-store" });
    if (!res.ok) {
      return null;
    }
    return res.json();
  } catch {
    return null;
  }
}

/** Client refresh interval for Velo news (ms). */
export const VELO_NEWS_POLL_MS = 30_000;

/** Staff wish search: strict + optional near-match fallback (Spring {@code /api/v1/offers/wish}). */
export async function fetchWishSearch(
  params: Record<string, string | string[] | undefined>,
  options?: { size?: number },
): Promise<WishSearchResponse | null> {
  const base =
    process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080";
  const qs = toOffersQueryString(params, { size: options?.size });
  const url = `${base}/api/v1/offers/wish?${qs}`;
  try {
    const res = await fetch(url, {
      cache: "no-store",
      headers: staffFetchHeaders(),
    });
    if (!res.ok) {
      return null;
    }
    return res.json();
  } catch {
    return null;
  }
}
