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
      <div className="mx-auto max-w-5xl px-4 py-10 sm:px-6">
        <DisclaimerStrip />
        <header className="mb-10">
          <h1 className="text-3xl font-semibold tracking-tight text-zinc-900">
            {t("title")}
          </h1>
          <p className="mt-2 max-w-2xl text-sm leading-relaxed text-zinc-600">
            {t("subtitle")}
          </p>
          <p className="mt-4 text-sm text-zinc-600">
            <Link
              href="/suche"
              className="font-medium text-zinc-900 underline underline-offset-2"
            >
              {t("toWunschSearch")}
            </Link>
          </p>
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
