/**
 * Swiss-style CHF display (de-CH). Apostrophe thousands separator where supported by Intl.
 */
export function formatChf(amount: number | null | undefined): string {
  if (amount == null || Number.isNaN(amount)) {
    return "—";
  }
  return new Intl.NumberFormat("de-CH", {
    style: "currency",
    currency: "CHF",
    currencyDisplay: "narrowSymbol",
    minimumFractionDigits: 0,
    maximumFractionDigits: 0,
  }).format(amount);
}
