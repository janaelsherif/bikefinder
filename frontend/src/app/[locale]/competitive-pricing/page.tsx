export const dynamic = "force-dynamic";

import { getTranslations } from "next-intl/server";
import { DisclaimerStrip } from "@/components/disclaimer-strip";
import { CompetitivePricingTool } from "@/components/competitive-pricing-tool";
import { PageShell } from "@/components/page-shell";

export default async function CompetitivePricingPage() {
  const t = await getTranslations("CompetitivePricing");
  const labels = {
    introTitle: t("introTitle"),
    introBody: t("introBody"),
    brand: t("brand"),
    model: t("model"),
    modelYear: t("modelYear"),
    modelYearHint: t("modelYearHint"),
    condition: t("condition"),
    conditionHint: t("conditionHint"),
    buyIn: t("buyIn"),
    buyInHint: t("buyInHint"),
    submit: t("submit"),
    loading: t("loading"),
    errorApi: t("errorApi"),
    errorHttp: t("errorHttp"),
    validateForm: t("validateForm"),
    benchmarkTitle: t("benchmarkTitle"),
    median: t("median"),
    p25: t("p25"),
    p75: t("p75"),
    recommendTitle: t("recommendTitle"),
    recommendHint: t("recommendHint"),
    floor: t("floor"),
    margin: t("margin"),
    confidence: t("confidence"),
    swissCount: t("swissCount"),
    deCount: t("deCount"),
    fallback: t("fallback"),
    comparablesTitle: t("comparablesTitle"),
    colSource: t("colSource"),
    colCountry: t("colCountry"),
    colBike: t("colBike"),
    colYear: t("colYear"),
    colCond: t("colCond"),
    colPrice: t("colPrice"),
    linkListing: t("linkListing"),
    noComparables: t("noComparables"),
    insufficientTitle: t("insufficientTitle"),
    marginConflict: t("marginConflict"),
    gradeA: t("gradeA"),
    gradeB: t("gradeB"),
    gradeC: t("gradeC"),
    gradeD: t("gradeD"),
    liveProbesTitle: t("liveProbesTitle"),
    liveBenchmarkBadge: t("liveBenchmarkBadge"),
    colLiveShop: t("colLiveShop"),
    colLivePrice: t("colLivePrice"),
    colLiveLink: t("colLiveLink"),
    colLiveError: t("colLiveError"),
  };

  return (
    <PageShell>
      <div className="mx-auto max-w-6xl px-4 py-10 sm:px-6 lg:px-8">
        <DisclaimerStrip />
        <header className="mb-10 rounded-2xl border border-zinc-200/80 bg-white/90 p-8 shadow-card backdrop-blur-sm sm:p-10">
          <h1 className="text-3xl font-semibold tracking-tight text-zinc-900 sm:text-4xl">
            {t("title")}
          </h1>
          <p className="mt-3 max-w-3xl text-base leading-relaxed text-zinc-600">
            {t("subtitle")}
          </p>
        </header>
        <CompetitivePricingTool labels={labels} />
      </div>
    </PageShell>
  );
}
