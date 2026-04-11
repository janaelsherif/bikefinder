import { getTranslations } from "next-intl/server";
import type { B2bSourcingLabels } from "@/components/b2b-sourcing-directory";
import { B2bSourcingDirectory } from "@/components/b2b-sourcing-directory";
import { DisclaimerStrip } from "@/components/disclaimer-strip";
import { PageShell } from "@/components/page-shell";
import { resolveLocaleParams } from "@/lib/resolve-locale-params";

type Props = {
  params: { locale: string } | Promise<{ locale: string }>;
};

export default async function SourcingDirectoryPage({ params }: Props) {
  await resolveLocaleParams(params);
  const t = await getTranslations("Sourcing");

  const labels: B2bSourcingLabels = {
    colPlatform: t("colPlatform"),
    colType: t("colType"),
    colPrice: t("colPrice"),
    colB2b: t("colB2b"),
    colConfidence: t("colConfidence"),
    confidence: {
      HIGH: t("confHigh"),
      MEDIUM: t("confMedium"),
      LOW: t("confLow"),
    },
    footnote: t("footnote"),
    regions: {
      de: {
        title: t("regions.de.title"),
        intro: t("regions.de.intro"),
        benchmark: t("regions.de.benchmark"),
      },
      nl: {
        title: t("regions.nl.title"),
        intro: t("regions.nl.intro"),
        benchmark: t("regions.nl.benchmark"),
      },
      fr: {
        title: t("regions.fr.title"),
        intro: t("regions.fr.intro"),
        benchmark: t("regions.fr.benchmark"),
      },
      it: {
        title: t("regions.it.title"),
        intro: t("regions.it.intro"),
        benchmark: t("regions.it.benchmark"),
      },
      at: {
        title: t("regions.at.title"),
        intro: t("regions.at.intro"),
        benchmark: t("regions.at.benchmark"),
      },
      accessories: {
        title: t("regions.accessories.title"),
        intro: t("regions.accessories.intro"),
        benchmark: t("regions.accessories.benchmark"),
      },
    },
  };

  return (
    <PageShell>
      <div className="mx-auto max-w-6xl px-4 py-10 sm:px-6 lg:px-8 lg:py-14">
        <DisclaimerStrip />
        <header className="mb-10 rounded-2xl border border-zinc-200/80 bg-white/90 p-8 shadow-card backdrop-blur-sm sm:p-10">
          <p className="text-xs font-semibold uppercase tracking-widest text-zinc-500">
            {t("heroKicker")}
          </p>
          <h1 className="mt-2 text-3xl font-semibold tracking-tight text-zinc-900 sm:text-4xl">
            {t("title")}
          </h1>
          <p className="mt-4 max-w-3xl text-base leading-relaxed text-zinc-600 sm:text-lg">
            {t("subtitle")}
          </p>
        </header>
        <B2bSourcingDirectory labels={labels} />
      </div>
    </PageShell>
  );
}
