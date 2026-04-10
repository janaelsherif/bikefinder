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
        className="rounded-md border border-zinc-300 bg-white px-2 py-1.5 text-sm text-zinc-900 focus:border-zinc-500 focus:outline-none focus:ring-1 focus:ring-zinc-500"
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
