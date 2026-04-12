import { defineRouting } from "next-intl/routing";

export const routing = defineRouting({
  locales: ["de-CH", "en"],
  /** English-first: `/` resolves to `/en`; staff gate uses this for `next` when path has no locale. */
  defaultLocale: "en",
  // "always" keeps URLs explicit (/de-CH, /en) and avoids /en 404s seen with "as-needed" + [locale].
  localePrefix: "always",
});
