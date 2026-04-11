export const dynamic = "force-dynamic";

import { getTranslations } from "next-intl/server";
import { DisclaimerStrip } from "@/components/disclaimer-strip";
import { OfferGrid } from "@/components/offer-grid";
import { PageShell } from "@/components/page-shell";
import { Link } from "@/i18n/navigation";
import { fetchOffersPage } from "@/lib/offers-api";

export default async function HomePage() {
  const t = await getTranslations("Home");
  const data = await fetchOffersPage({}, { size: 12 });

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

        {!data && (
          <p className="rounded-lg border border-amber-200 bg-amber-50 px-4 py-3 text-sm text-amber-900">
            {t("error")}
          </p>
        )}

        {data && data.content.length === 0 && (
          <p className="rounded-lg border border-zinc-200 bg-white px-4 py-6 text-sm text-zinc-600">
            {t("empty")}
          </p>
        )}

        {data && data.content.length > 0 && (
          <OfferGrid
            offers={data.content}
            labels={{
              cta: t("cta"),
              bargain: t("bargain"),
              topDeal: t("topDeal"),
              discountVsCh: t("discountVsCh"),
            }}
          />
        )}
      </div>
    </PageShell>
  );
}
