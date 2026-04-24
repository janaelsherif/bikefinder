export const dynamic = "force-dynamic";

import { getTranslations } from "next-intl/server";
import { DisclaimerStrip } from "@/components/disclaimer-strip";
import { PageShell } from "@/components/page-shell";
import { VeloNewsFeed } from "@/components/velo-news-feed";

export default async function VeloNewsPage() {
  const t = await getTranslations("VeloNews");
  const th = await getTranslations("Home");

  return (
    <PageShell>
      <div className="mx-auto max-w-6xl px-4 py-10 sm:px-6 lg:px-8 lg:py-14">
        <DisclaimerStrip />
        <header className="mb-10 rounded-2xl border border-zinc-200/80 bg-white/90 p-8 shadow-card backdrop-blur-sm sm:p-10">
          <p className="text-xs font-semibold uppercase tracking-widest text-zinc-500">
            {t("heroKicker")}
          </p>
          <h1 className="mt-3 text-3xl font-semibold tracking-tight text-zinc-900 sm:text-4xl">
            {t("title")}
          </h1>
          <p className="mt-4 max-w-3xl text-base leading-relaxed text-zinc-600 sm:text-lg">
            {t("subtitle")}
          </p>
          <p className="mt-4 max-w-3xl text-sm leading-relaxed text-zinc-500">
            {t("footnote")}
          </p>
        </header>

        <VeloNewsFeed
          labels={{
            lastUpdated: t("lastUpdated"),
            refreshIn: t("refreshIn"),
            newBadge: t("newBadge"),
            polling: t("polling"),
            error: t("error"),
            empty: t("empty"),
            grid: {
              cta: th("cta"),
              bargain: th("bargain"),
              topDeal: th("topDeal"),
              discountVsCh: th("discountVsCh"),
            },
          }}
        />
      </div>
    </PageShell>
  );
}
