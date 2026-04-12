import createMiddleware from "next-intl/middleware";
import { type NextRequest, NextResponse } from "next/server";
import { routing } from "./i18n/routing";

const intlMiddleware = createMiddleware(routing);

/**
 * When `STAFF_UI_PASSWORD` is set, the app is staff-only: any route under a locale requires
 * `ebf_staff=1`, except `/…/staff-login`. Unauthenticated users are sent to staff login; after
 * success, `next` (or default `/{locale}` home) is used — e.g. `/en` → `/en/staff-login?next=/en` → `/en`.
 */
function staffGate(request: NextRequest): NextResponse | null {
  const staffPw = process.env.STAFF_UI_PASSWORD?.trim();
  if (!staffPw) {
    return null;
  }

  const { pathname, search } = request.nextUrl;
  if (pathname.includes("/staff-login")) {
    return null;
  }

  if (request.cookies.get("ebf_staff")?.value === "1") {
    return null;
  }

  const localeMatch = /^\/(de-CH|en)(?=\/|$)/.exec(pathname);
  const locale = localeMatch ? localeMatch[1] : routing.defaultLocale;

  const intendedPath =
    pathname === "/" || pathname === ""
      ? `/${locale}`
      : `${pathname}${search}`;

  const url = request.nextUrl.clone();
  url.pathname = `/${locale}/staff-login`;
  url.search = "";
  url.searchParams.set("next", intendedPath);
  return NextResponse.redirect(url);
}

export default function middleware(request: NextRequest) {
  const gated = staffGate(request);
  if (gated) {
    return gated;
  }
  return intlMiddleware(request);
}

export const config = {
  matcher: ["/((?!api|_next|_vercel|.*\\..*).*)"],
};
