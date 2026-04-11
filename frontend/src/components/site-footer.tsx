import { getTranslations } from "next-intl/server";
import { Link } from "@/i18n/navigation";

export async function SiteFooter() {
  const t = await getTranslations("Legal");

  return (
    <footer className="mt-auto border-t border-zinc-200 bg-white">
      <div className="mx-auto flex max-w-6xl flex-wrap items-center justify-between gap-4 px-4 py-10 text-sm text-zinc-600 sm:px-6 lg:px-8">
        <p className="max-w-md leading-relaxed text-zinc-500">{t("footerNote")}</p>
        <nav className="flex flex-wrap gap-6 font-medium">
          <Link
            href="/privacy"
            className="text-zinc-600 transition hover:text-zinc-900"
          >
            {t("privacyTitle")}
          </Link>
          <Link
            href="/imprint"
            className="text-zinc-600 transition hover:text-zinc-900"
          >
            {t("imprintTitle")}
          </Link>
        </nav>
      </div>
    </footer>
  );
}
