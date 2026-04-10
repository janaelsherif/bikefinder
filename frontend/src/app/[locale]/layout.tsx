import { NextIntlClientProvider } from "next-intl";
import { getMessages, getTranslations, setRequestLocale } from "next-intl/server";
import type { Metadata } from "next";
import { notFound } from "next/navigation";
import { HtmlLang } from "@/components/html-lang";
import { routing } from "@/i18n/routing";
import { resolveLocaleParams } from "@/lib/resolve-locale-params";

type Props = {
  children: React.ReactNode;
  params: { locale: string } | Promise<{ locale: string }>;
};

export function generateStaticParams() {
  return routing.locales.map((locale) => ({ locale }));
}

function isValidLocale(
  x: string,
): x is (typeof routing.locales)[number] {
  return (routing.locales as readonly string[]).includes(x);
}

export async function generateMetadata({
  params,
}: {
  params: { locale: string } | Promise<{ locale: string }>;
}): Promise<Metadata> {
  const { locale } = await resolveLocaleParams(params);
  if (!isValidLocale(locale)) {
    return { title: "EuropeBikeFinder" };
  }
  setRequestLocale(locale);
  const t = await getTranslations({ locale, namespace: "Meta" });
  return {
    title: "EuropeBikeFinder",
    description: t("description"),
  };
}

export default async function LocaleLayout({ children, params }: Props) {
  const { locale } = await resolveLocaleParams(params);
  if (!isValidLocale(locale)) {
    notFound();
  }
  setRequestLocale(locale);
  const messages = await getMessages();
  return (
    <NextIntlClientProvider locale={locale} messages={messages}>
      <HtmlLang locale={locale} />
      {children}
    </NextIntlClientProvider>
  );
}
