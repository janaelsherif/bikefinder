import { routing } from "@/i18n/routing";

/** Path for native forms and redirects; next-intl Link handles this automatically. */
export function localePath(locale: string, pathname: string): string {
  const p = pathname.startsWith("/") ? pathname : `/${pathname}`;
  if (locale === routing.defaultLocale) {
    return p;
  }
  return `/${locale}${p}`;
}
