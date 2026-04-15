export const dynamic = "force-dynamic";

import { getTranslations } from "next-intl/server";
import { DisclaimerStrip } from "@/components/disclaimer-strip";
import { HomeOffersSection } from "@/components/home-offers-section";
import { PageShell } from "@/components/page-shell";
import { Link } from "@/i18n/navigation";
import { LISTING_COUNTRY_CODES } from "@/lib/country-options";

export default async function HomePage() {
  const t = await getTranslations("Home");
  const ts = await getTranslations("SortBar");
  const tSearch = await getTranslations("Search");
  const tCf = await getTranslations("CountryFilter");

  const byCode: Record<string, string> = {};
  for (const code of LISTING_COUNTRY_CODES) {
    byCode[code] = tSearch(`country_${code}` as never);
  }

  return (
    <PageShell>
      <div className="mx-auto max-w-6xl px-4 py-10 sm:px-6 lg:px-8 lg:py-14">
        <DisclaimerStrip />
        <header className="mb-12 rounded-2xl border border-zinc-200/80 bg-white/90 p-8 shadow-card backdrop-blur-sm sm:p-10">
          <p className="text-xs font-semibold uppercase tracking-widest text-zinc-500">
            {t("heroKicker")}
          </p>
          <h1 className="mt-3 text-3xl font-semibold tracking-tight text-zinc-900 sm:text-4xl lg:text-[2.5rem] lg:leading-tight">
            {t("title")}
          </h1>
          <p className="mt-4 max-w-2xl text-base leading-relaxed text-zinc-600 sm:text-lg">
            {t("subtitle")}
          </p>
          <div className="mt-8 flex flex-wrap items-center gap-3">
            <Link
              href="/suche"
              className="inline-flex items-center justify-center rounded-full bg-zinc-900 px-6 py-3 text-sm font-semibold text-white shadow-md transition hover:bg-zinc-800 hover:shadow-lg focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-zinc-900"
            >
              {t("toWunschSearch")}
            </Link>
            <Link
              href="/sourcing"
              className="inline-flex items-center justify-center rounded-full border border-zinc-300 bg-white px-6 py-3 text-sm font-semibold text-zinc-900 shadow-sm transition hover:border-zinc-400 hover:bg-zinc-50"
            >
              {t("toSourcingDirectory")}
            </Link>
          </div>
        </header>

        <HomeOffersSection
          labels={{
            sort: {
              label: ts("label"),
              newest: ts("newest"),
              priceAsc: ts("priceAsc"),
              priceDesc: ts("priceDesc"),
              countryAsc: ts("countryAsc"),
              countryDesc: ts("countryDesc"),
            },
            country: {
              label: tCf("label"),
              any: tCf("any"),
              byCode,
            },
            grid: {
              cta: t("cta"),
              bargain: t("bargain"),
              topDeal: t("topDeal"),
              discountVsCh: t("discountVsCh"),
            },
            empty: t("empty"),
            error: t("error"),
            loading: t("loading"),
          }}
        />
      </div>
    </PageShell>
  );
}
