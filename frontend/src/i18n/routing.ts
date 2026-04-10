import { defineRouting } from "next-intl/routing";

export const routing = defineRouting({
  locales: ["de-CH", "en"],
  defaultLocale: "de-CH",
  // "always" keeps URLs explicit (/de-CH, /en) and avoids /en 404s seen with "as-needed" + [locale].
  localePrefix: "always",
});
