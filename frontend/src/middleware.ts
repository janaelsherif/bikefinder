import createMiddleware from "next-intl/middleware";
import { type NextRequest, NextResponse } from "next/server";
import { routing } from "./i18n/routing";

const intlMiddleware = createMiddleware(routing);

export default function middleware(request: NextRequest) {
  const staffPw = process.env.STAFF_UI_PASSWORD;
  if (staffPw) {
    const { pathname } = request.nextUrl;
    if (pathname.includes("/staff-login")) {
      return intlMiddleware(request);
    }
    const staffOnly = /^\/([^/]+)\/(suche|competitive-pricing)\/?$/.exec(
      pathname,
    );
    if (staffOnly && request.cookies.get("ebf_staff")?.value !== "1") {
      const locale = staffOnly[1];
      const url = request.nextUrl.clone();
      url.pathname = `/${locale}/staff-login`;
      url.searchParams.set("next", pathname);
      return NextResponse.redirect(url);
    }
  }
  return intlMiddleware(request);
}

export const config = {
  matcher: ["/((?!api|_next|_vercel|.*\\..*).*)"],
};
