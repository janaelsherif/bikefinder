import { getTranslations, setRequestLocale } from "next-intl/server";
import type { Metadata } from "next";
import { PageShell } from "@/components/page-shell";
import { resolveLocaleParams } from "@/lib/resolve-locale-params";

type Props = { params: { locale: string } | Promise<{ locale: string }> };

export async function generateMetadata({ params }: Props): Promise<Metadata> {
  const { locale } = await resolveLocaleParams(params);
  setRequestLocale(locale);
  const t = await getTranslations({ locale, namespace: "Legal" });
  return {
    title: `${t("imprintTitle")} · EuropeBikeFinder`,
  };
}

export default async function ImprintPage({ params }: Props) {
  const { locale } = await resolveLocaleParams(params);
  setRequestLocale(locale);
  const t = await getTranslations("Legal");

  return (
    <PageShell>
      <div className="mx-auto max-w-3xl px-4 py-10 sm:px-6">
        <h1 className="text-2xl font-semibold text-zinc-900">{t("imprintTitle")}</h1>
        <p className="mt-6 whitespace-pre-line text-sm leading-relaxed text-zinc-600">
          {t("imprintBody")}
        </p>
      </div>
    </PageShell>
  );
}
