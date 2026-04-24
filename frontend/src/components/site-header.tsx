import { cookies } from "next/headers";
import { getLocale, getTranslations } from "next-intl/server";
import { Link } from "@/i18n/navigation";
import { LanguageSwitcher } from "@/components/language-switcher";
import { StaffLogoutButton } from "@/components/staff-logout-button";

const authLinkClass =
  "text-sm font-medium text-zinc-600 underline-offset-4 transition hover:text-zinc-900 hover:underline";

export async function SiteHeader() {
  const t = await getTranslations("Nav");
  const locale = await getLocale();
  const isStaffSession = cookies().get("ebf_staff")?.value === "1";

  return (
    <header className="sticky top-0 z-50 border-b border-zinc-200/80 bg-white/85 shadow-sm backdrop-blur-md supports-[backdrop-filter]:bg-white/70">
      <div className="mx-auto flex max-w-6xl flex-wrap items-center justify-between gap-4 px-4 py-3.5 sm:px-6 lg:px-8">
        <Link
          href="/"
          className="group flex items-center gap-2 text-base font-semibold tracking-tight text-zinc-900"
        >
          <span
            className="flex h-8 w-8 items-center justify-center rounded-lg bg-zinc-900 text-xs font-bold text-white shadow-sm transition group-hover:bg-zinc-800"
            aria-hidden
          >
            EB
          </span>
          <span className="hidden sm:inline">{t("brand")}</span>
        </Link>
        <div className="flex flex-wrap items-center gap-3 sm:gap-5">
          {isStaffSession ? (
            <StaffLogoutButton label={t("logout")} className={authLinkClass} />
          ) : (
            <Link href="/staff-login" className={authLinkClass}>
              {t("login")}
            </Link>
          )}
          <nav className="flex flex-wrap items-center gap-1 rounded-full border border-zinc-200/80 bg-zinc-50/90 p-1 text-sm shadow-sm">
            <Link
              href="/"
              className="rounded-full px-3 py-1.5 text-zinc-600 transition hover:bg-white hover:text-zinc-900 hover:shadow-sm"
            >
              {t("offers")}
            </Link>
            <Link
              href="/velo-news"
              className="rounded-full px-3 py-1.5 text-zinc-600 transition hover:bg-white hover:text-zinc-900 hover:shadow-sm"
            >
              {t("veloNews")}
            </Link>
            <Link
              href="/suche"
              className="rounded-full px-3 py-1.5 text-zinc-600 transition hover:bg-white hover:text-zinc-900 hover:shadow-sm"
            >
              {t("wunschSearch")}
            </Link>
            <Link
              href="/competitive-pricing"
              className="rounded-full px-3 py-1.5 text-zinc-600 transition hover:bg-white hover:text-zinc-900 hover:shadow-sm"
            >
              {t("competitivePricing")}
            </Link>
            <Link
              href="/competitor-watch"
              className="rounded-full px-3 py-1.5 text-zinc-600 transition hover:bg-white hover:text-zinc-900 hover:shadow-sm"
            >
              {t("competitorWatch")}
            </Link>
            <Link
              href="/sourcing"
              className="rounded-full px-3 py-1.5 text-zinc-600 transition hover:bg-white hover:text-zinc-900 hover:shadow-sm"
            >
              {t("sourcingDirectory")}
            </Link>
          </nav>
          <LanguageSwitcher
            locale={locale}
            labelLanguage={t("language")}
            labelDeCH={t("locale_deCH")}
            labelEn={t("locale_en")}
          />
        </div>
      </div>
    </header>
  );
}
