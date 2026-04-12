/** Path for native forms (`action=…`). With `localePrefix: "always"`, every URL includes the locale. */
export function localePath(locale: string, pathname: string): string {
  const p = pathname.startsWith("/") ? pathname : `/${pathname}`;
  return `/${locale}${p}`;
}
