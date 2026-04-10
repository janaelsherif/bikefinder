export const dynamic = "force-dynamic";

import { getTranslations } from "next-intl/server";
import { Suspense } from "react";
import {
  AlertSubscribeCard,
  type AlertSubscribeLabels,
} from "@/components/alert-subscribe-card";
import { DisclaimerStrip } from "@/components/disclaimer-strip";
import { OfferGrid } from "@/components/offer-grid";
import { PageShell } from "@/components/page-shell";
import { WunschSearchForm } from "@/components/wunsch-search-form";
import { fetchWishSearch } from "@/lib/offers-api";
import { resolveLocaleParams } from "@/lib/resolve-locale-params";

type Props = {
  params: { locale: string } | Promise<{ locale: string }>;
  searchParams: Record<string, string | string[] | undefined>;
};

function hasWishFilters(sp: Record<string, string | string[] | undefined>): boolean {
  const keys = [
    "brand",
    "model",
    "bikeCategory",
    "bikeCondition",
    "motorBrand",
    "motorPosition",
    "minBatteryWh",
    "maxLandedPriceChf",
    "maxMileageKm",
    "countryCode",
  ];
  for (const k of keys) {
    const v = sp[k];
    const s = Array.isArray(v) ? v[0] : v;
    if (s !== undefined && s !== "") {
      return true;
    }
  }
  if (sp.warrantyPresent === "true") {
    return true;
  }
  if (sp.bargainOnly === "true") {
    return true;
  }
  return false;
}

export default async function SuchePage({ params, searchParams }: Props) {
  const { locale } = await resolveLocaleParams(params);
  const t = await getTranslations("Home");
  const ts = await getTranslations("Search");
  const ta = await getTranslations("Alert");
  const data = await fetchWishSearch(searchParams, { size: 24 });

  const alertLabels: AlertSubscribeLabels = {
    title: ta("title"),
    hint: ta("hint"),
    email: ta("email"),
    emailPlaceholder: ta("emailPlaceholder"),
    submit: ta("submit"),
    success: ta("success"),
    error: ta("error"),
  };

  const exact = data?.exact?.content ?? [];
  const near = data?.near?.content ?? [];
  const tier = data?.matchTier;
  const showExact = exact.length > 0;
  const showNear = !showExact && near.length > 0;
  const showNone = data && !showExact && !showNear;

  return (
    <PageShell>
      <div className="mx-auto max-w-5xl px-4 py-8 sm:px-6">
        <DisclaimerStrip />
        <WunschSearchForm searchParams={searchParams} locale={locale} />

        <Suspense fallback={null}>
          <AlertSubscribeCard labels={alertLabels} locale={locale} />
        </Suspense>

        <section className="mt-10">
          <h2 className="mb-4 text-lg font-semibold text-zinc-900">{ts("results")}</h2>
          {!data && (
            <p className="rounded-lg border border-amber-200 bg-amber-50 px-4 py-3 text-sm text-amber-900">
              {t("error")}
            </p>
          )}
          {data && showExact && (
            <>
              <p className="mb-3 text-sm font-medium text-zinc-700">{ts("exactHeading")}</p>
              <OfferGrid
                offers={exact}
                labels={{
                  cta: t("cta"),
                  bargain: t("bargain"),
                  topDeal: t("topDeal"),
                  discountVsCh: t("discountVsCh"),
                }}
              />
              {data.exact.totalElements > 0 && (
                <p className="mt-4 text-sm text-zinc-500">
                  {ts("total", { count: data.exact.totalElements })}
                </p>
              )}
            </>
          )}
          {data && showNear && (
            <>
              <p className="mb-3 rounded-lg border border-sky-200 bg-sky-50 px-4 py-3 text-sm text-sky-950">
                {ts("nearBanner")}
              </p>
              <OfferGrid
                offers={near}
                labels={{
                  cta: t("cta"),
                  bargain: t("bargain"),
                  topDeal: t("topDeal"),
                  discountVsCh: t("discountVsCh"),
                }}
              />
              {data.near.totalElements > 0 && (
                <p className="mt-4 text-sm text-zinc-500">
                  {ts("total", { count: data.near.totalElements })}
                </p>
              )}
            </>
          )}
          {data && showNone && (
            <p className="rounded-lg border border-zinc-200 bg-white px-4 py-6 text-sm text-zinc-600">
              {tier === "NONE" && hasWishFilters(searchParams)
                ? ts("noneAfterNear")
                : ts("noResults")}
            </p>
          )}
        </section>
      </div>
    </PageShell>
  );
}
