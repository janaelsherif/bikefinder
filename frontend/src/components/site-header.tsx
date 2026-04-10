import { getLocale, getTranslations } from "next-intl/server";
import { Link } from "@/i18n/navigation";
import { LanguageSwitcher } from "@/components/language-switcher";

export async function SiteHeader() {
  const t = await getTranslations("Nav");
  const locale = await getLocale();

  return (
    <header className="border-b border-zinc-200 bg-white">
      <div className="mx-auto flex max-w-5xl flex-wrap items-center justify-between gap-3 px-4 py-3 sm:px-6">
        <Link
          href="/"
          className="text-sm font-semibold tracking-tight text-zinc-900 hover:text-zinc-700"
        >
          {t("brand")}
        </Link>
        <div className="flex flex-wrap items-center gap-4">
          <nav className="flex flex-wrap gap-4 text-sm">
            <Link href="/" className="text-zinc-600 hover:text-zinc-900">
              {t("offers")}
            </Link>
            <Link href="/suche" className="text-zinc-600 hover:text-zinc-900">
              {t("wunschSearch")}
            </Link>
            <Link
              href="/competitive-pricing"
              className="text-zinc-600 hover:text-zinc-900"
            >
              {t("competitivePricing")}
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
