"use client";

import { usePathname, useRouter } from "@/i18n/navigation";
import { useSearchParams } from "next/navigation";
import { Suspense } from "react";

import { isListingCountryCode, LISTING_COUNTRY_CODES } from "@/lib/country-options";

export type CountryFilterLabels = {
  label: string;
  any: string;
  /** ISO-2 → display name */
  byCode: Record<string, string>;
};

function CountryFilterBarInner({ labels }: { labels: CountryFilterLabels }) {
  const router = useRouter();
  const pathname = usePathname();
  const searchParams = useSearchParams();

  const raw = searchParams.get("countryCode");
  const upper = raw?.trim().toUpperCase() ?? "";
  const current = isListingCountryCode(upper) ? upper : "";

  function onChange(next: string) {
    const p = new URLSearchParams(searchParams.toString());
    if (!next) {
      p.delete("countryCode");
    } else {
      p.set("countryCode", next);
    }
    const q = p.toString();
    router.replace(q ? `${pathname}?${q}` : pathname);
  }

  return (
    <div className="flex flex-wrap items-center gap-2">
      <label className="flex flex-wrap items-center gap-2 text-sm text-zinc-700">
        <span className="font-medium">{labels.label}</span>
        <select
          className="min-w-[12rem] rounded-lg border border-zinc-300 bg-white px-3 py-2 text-sm font-medium text-zinc-900 shadow-sm transition focus:border-zinc-500 focus:outline-none focus:ring-2 focus:ring-zinc-400/25"
          value={current}
          onChange={(e) => onChange(e.target.value)}
        >
          <option value="">{labels.any}</option>
          {LISTING_COUNTRY_CODES.map((code) => (
            <option key={code} value={code}>
              {labels.byCode[code] ?? code}
            </option>
          ))}
        </select>
      </label>
    </div>
  );
}

/** URL-driven filter `countryCode` for listing pages (home). */
export function CountryFilterBar({ labels }: { labels: CountryFilterLabels }) {
  return (
    <Suspense fallback={null}>
      <CountryFilterBarInner labels={labels} />
    </Suspense>
  );
}
