import { getTranslations } from "next-intl/server";
import { Link } from "@/i18n/navigation";

export async function SiteFooter() {
  const t = await getTranslations("Legal");

  return (
    <footer className="mt-16 border-t border-zinc-200 bg-white">
      <div className="mx-auto flex max-w-5xl flex-wrap items-center justify-between gap-4 px-4 py-8 text-sm text-zinc-600 sm:px-6">
        <p className="text-zinc-500">{t("footerNote")}</p>
        <nav className="flex flex-wrap gap-4">
          <Link href="/privacy" className="hover:text-zinc-900">
            {t("privacyTitle")}
          </Link>
          <Link href="/imprint" className="hover:text-zinc-900">
            {t("imprintTitle")}
          </Link>
        </nav>
      </div>
    </footer>
  );
}
