"use client";

import { usePathname, useRouter } from "@/i18n/navigation";
import { routing } from "@/i18n/routing";

type Props = {
  locale: string;
  labelLanguage: string;
  labelDeCH: string;
  labelEn: string;
};

export function LanguageSwitcher({
  locale,
  labelLanguage,
  labelDeCH,
  labelEn,
}: Props) {
  const pathname = usePathname();
  const router = useRouter();

  const labels: Record<string, string> = {
    "de-CH": labelDeCH,
    en: labelEn,
  };

  return (
    <label className="flex items-center gap-2 text-sm text-zinc-600">
      <span className="sr-only">{labelLanguage}</span>
      <select
        aria-label={labelLanguage}
        value={locale}
        onChange={(e) => {
          const next = e.target.value;
          router.replace(pathname, { locale: next });
        }}
        className="cursor-pointer rounded-lg border border-zinc-300 bg-white px-3 py-2 text-sm font-medium text-zinc-900 shadow-sm transition hover:border-zinc-400 focus:border-zinc-500 focus:outline-none focus:ring-2 focus:ring-zinc-400/30"
      >
        {routing.locales.map((loc) => (
          <option key={loc} value={loc}>
            {labels[loc] ?? loc}
          </option>
        ))}
      </select>
    </label>
  );
}
