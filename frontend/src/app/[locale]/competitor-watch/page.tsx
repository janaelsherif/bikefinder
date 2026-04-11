export const dynamic = "force-dynamic";

import { getTranslations } from "next-intl/server";
import { CompetitorWatchBriefPanel } from "@/components/competitor-watch-brief-panel";
import type { CompetitorWatchCopy } from "@/components/competitor-watch-module";
import { CompetitorWatchModule } from "@/components/competitor-watch-module";
import { PageShell } from "@/components/page-shell";
import { fetchCompetitorWatchPageData } from "@/lib/competitor-watch-api";
import { resolveLocaleParams } from "@/lib/resolve-locale-params";

type Props = {
  params: { locale: string } | Promise<{ locale: string }>;
};

export default async function CompetitorWatchPage({ params }: Props) {
  const { locale } = await resolveLocaleParams(params);
  const t = await getTranslations("CompetitorWatch");
  const { dashboard, histories } = await fetchCompetitorWatchPageData(12);

  const labels: CompetitorWatchCopy = {
    title: t("title"),
    subtitle: t("subtitle"),
    missionTitle: t("missionTitle"),
    qInventory: t("qInventory"),
    qPricing: t("qPricing"),
    qMarket: t("qMarket"),
    briefTitle: t("briefTitle"),
    colCompetitor: t("colCompetitor"),
    colMonitor: t("colMonitor"),
    colSource: t("colSource"),
    colAlert: t("colAlert"),
    rowVeloplus: {
      monitor: t("rowVeloplus.monitor"),
      source: t("rowVeloplus.source"),
      alert: t("rowVeloplus.alert"),
    },
    rowUpway: {
      monitor: t("rowUpway.monitor"),
      source: t("rowUpway.source"),
      alert: t("rowUpway.alert"),
    },
    rowRebike: {
      monitor: t("rowRebike.monitor"),
      source: t("rowRebike.source"),
      alert: t("rowRebike.alert"),
    },
    rowBibibike: {
      monitor: t("rowBibibike.monitor"),
      source: t("rowBibibike.source"),
      alert: t("rowBibibike.alert"),
    },
    rowVelocorner: {
      monitor: t("rowVelocorner.monitor"),
      source: t("rowVelocorner.source"),
      alert: t("rowVelocorner.alert"),
    },
    nameVeloplus: t("nameVeloplus"),
    nameUpway: t("nameUpway"),
    nameRebike: t("nameRebike"),
    nameBibibike: t("nameBibibike"),
    nameVelocorner: t("nameVelocorner"),
    signalsTitle: t("signalsTitle"),
    signalsHint: t("signalsHint"),
    colLastSnapshot: t("colLastSnapshot"),
    colEstimate: t("colEstimate"),
    colDelta: t("colDelta"),
    colHttp: t("colHttp"),
    colDuration: t("colDuration"),
    signalAlert: t("signalAlert"),
    signalCalm: t("signalCalm"),
    openShop: t("openShop"),
    noSnapshot: t("noSnapshot"),
    error: t("error"),
    historyTitle: t("historyTitle"),
    historyWhen: t("historyWhen"),
    historyEstimate: t("historyEstimate"),
    historyDelta: t("historyDelta"),
    technicalNote: t("technicalNote"),
  };

  const aiBriefLabels = {
    title: t("aiBriefTitle"),
    hint: t("aiBriefHint"),
    focusPlaceholder: t("aiBriefFocusPlaceholder"),
    button: t("aiBriefButton"),
    loading: t("aiBriefLoading"),
    errorPrefix: t("aiBriefError"),
    perplexityNote: t("aiBriefPerplexityNote"),
    noPerplexityNote: t("aiBriefNoPerplexityNote"),
  };

  return (
    <PageShell>
      <div className="mx-auto max-w-6xl px-4 py-10 sm:px-6 lg:px-8 lg:py-14">
        <CompetitorWatchBriefPanel labels={aiBriefLabels} />
        <div className="mt-10">
        <CompetitorWatchModule
          locale={locale}
          dashboard={dashboard}
          histories={histories}
          labels={labels}
        />
        </div>
      </div>
    </PageShell>
  );
}
