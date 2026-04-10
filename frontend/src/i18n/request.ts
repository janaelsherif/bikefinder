import { getRequestConfig } from "next-intl/server";
import deCH from "../messages/de-CH.messages";
import en from "../messages/en.messages";
import { routing } from "./routing";

function isSupportedLocale(
  x: string | undefined,
): x is (typeof routing.locales)[number] {
  return (
    x !== undefined && (routing.locales as readonly string[]).includes(x)
  );
}

/** Message modules are plain TS (not JSON) to avoid webpack JSON chunk bugs in dev/SSR with next-intl. */
function messagesForLocale(locale: (typeof routing.locales)[number]) {
  switch (locale) {
    case "en":
      return en;
    case "de-CH":
    default:
      return deCH;
  }
}

export default getRequestConfig(async ({ requestLocale }) => {
  const raw = await requestLocale;
  const locale: (typeof routing.locales)[number] = isSupportedLocale(raw)
    ? raw
    : routing.defaultLocale;
  return {
    locale,
    messages: messagesForLocale(locale),
  };
});
