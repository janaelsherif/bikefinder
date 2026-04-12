/** ISO-3166 alpha-2 codes shown in country dropdowns (filters + wish search). */
export const LISTING_COUNTRY_CODES = [
  "CH",
  "DE",
  "AT",
  "FR",
  "IT",
  "NL",
  "BE",
  "ES",
  "PT",
  "PL",
  "CZ",
  "DK",
  "SE",
  "FI",
  "IE",
  "LU",
] as const;

export type ListingCountryCode = (typeof LISTING_COUNTRY_CODES)[number];

export function isListingCountryCode(s: string): s is ListingCountryCode {
  return (LISTING_COUNTRY_CODES as readonly string[]).includes(s);
}

/** Uppercase ISO-2 when it is in the listing allowlist; otherwise undefined. */
export function parseListingCountryParam(
  raw: string | string[] | undefined,
): string | undefined {
  const v = Array.isArray(raw) ? raw[0] : raw;
  if (!v || typeof v !== "string") {
    return undefined;
  }
  const u = v.trim().toUpperCase();
  if (u.length !== 2) {
    return undefined;
  }
  return isListingCountryCode(u) ? u : undefined;
}
