"use client";

import { usePathname, useRouter } from "@/i18n/navigation";
import { useSearchParams } from "next/navigation";
import { Suspense } from "react";

export type OfferSortLabels = {
  label: string;
  newest: string;
  priceAsc: string;
  priceDesc: string;
  countryAsc: string;
  countryDesc: string;
};

const OPTIONS: { value: string; labelKey: keyof OfferSortLabels }[] = [
  { value: "newest", labelKey: "newest" },
  { value: "price_asc", labelKey: "priceAsc" },
  { value: "price_desc", labelKey: "priceDesc" },
  { value: "country_asc", labelKey: "countryAsc" },
  { value: "country_desc", labelKey: "countryDesc" },
];

function OfferSortBarInner({ labels }: { labels: OfferSortLabels }) {
  const router = useRouter();
  const pathname = usePathname();
  const searchParams = useSearchParams();

  const raw = searchParams.get("offerSort");
  const current =
    raw && OPTIONS.some((o) => o.value === raw) ? raw : "newest";

  function onChange(nextSort: string) {
    const p = new URLSearchParams(searchParams.toString());
    if (nextSort === "newest") {
      p.delete("offerSort");
    } else {
      p.set("offerSort", nextSort);
    }
    const q = p.toString();
    router.replace(q ? `${pathname}?${q}` : pathname);
  }

  return (
    <div className="flex flex-wrap items-center gap-2">
      <label className="flex flex-wrap items-center gap-2 text-sm text-zinc-700">
        <span className="font-medium">{labels.label}</span>
        <select
          className="rounded-lg border border-zinc-300 bg-white px-3 py-2 text-sm font-medium text-zinc-900 shadow-sm transition focus:border-zinc-500 focus:outline-none focus:ring-2 focus:ring-zinc-400/25"
          value={current}
          onChange={(e) => onChange(e.target.value)}
        >
          {OPTIONS.map((o) => (
            <option key={o.value} value={o.value}>
              {labels[o.labelKey]}
            </option>
          ))}
        </select>
      </label>
    </div>
  );
}

/** URL-driven sort for `/` and `/suche` (passes Spring Data `sort=` to the API). */
export function OfferSortBar({ labels }: { labels: OfferSortLabels }) {
  return (
    <Suspense fallback={null}>
      <OfferSortBarInner labels={labels} />
    </Suspense>
  );
}
