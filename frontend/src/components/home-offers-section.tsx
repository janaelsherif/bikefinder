"use client";

import { useEffect, useMemo, useState } from "react";
import { useSearchParams } from "next/navigation";
import { CountryFilterBar, type CountryFilterLabels } from "@/components/country-filter-bar";
import { OfferGrid, type OfferGridLabels } from "@/components/offer-grid";
import { OfferSortBar, type OfferSortLabels } from "@/components/offer-sort-bar";
import { LISTING_COUNTRY_CODES } from "@/lib/country-options";
import type { SpringPage } from "@/lib/offers-api";

type HomeOffersSectionLabels = {
  sort: OfferSortLabels;
  country: CountryFilterLabels;
  grid: OfferGridLabels;
  empty: string;
  error: string;
  loading: string;
};

const ALLOWED_KEYS = new Set(["offerSort", "countryCode", "size", "page"]);

function buildOffersQuery(searchParams: URLSearchParams): string {
  const p = new URLSearchParams();
  p.set("size", "12");
  searchParams.forEach((value, key) => {
    if (!ALLOWED_KEYS.has(key)) {
      return;
    }
    if (!value) {
      return;
    }
    p.set(key, value);
  });
  return p.toString();
}

export function HomeOffersSection({ labels }: { labels: HomeOffersSectionLabels }) {
  const searchParams = useSearchParams();
  const [data, setData] = useState<SpringPage | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [loadError, setLoadError] = useState(false);

  const query = useMemo(() => buildOffersQuery(searchParams), [searchParams]);

  useEffect(() => {
    let cancelled = false;
    async function loadOffers() {
      setIsLoading(true);
      setLoadError(false);
      try {
        const res = await fetch(`/api/offers?${query}`, { cache: "no-store" });
        if (!res.ok) {
          if (!cancelled) {
            setLoadError(true);
            setData(null);
          }
          return;
        }
        const next = (await res.json()) as SpringPage;
        if (!cancelled) {
          setData(next);
        }
      } catch {
        if (!cancelled) {
          setLoadError(true);
          setData(null);
        }
      } finally {
        if (!cancelled) {
          setIsLoading(false);
        }
      }
    }
    void loadOffers();
    return () => {
      cancelled = true;
    };
  }, [query]);

  const byCode: Record<string, string> = {};
  for (const code of LISTING_COUNTRY_CODES) {
    byCode[code] = labels.country.byCode[code] ?? code;
  }

  return (
    <>
      <div className="mb-6 flex flex-col gap-4 sm:flex-row sm:flex-wrap sm:items-center sm:gap-8">
        <OfferSortBar labels={labels.sort} />
        <CountryFilterBar labels={{ ...labels.country, byCode }} />
      </div>

      {isLoading && (
        <p className="rounded-lg border border-zinc-200 bg-white px-4 py-6 text-sm text-zinc-600">
          {labels.loading}
        </p>
      )}

      {!isLoading && loadError && (
        <p className="rounded-lg border border-amber-200 bg-amber-50 px-4 py-3 text-sm text-amber-900">
          {labels.error}
        </p>
      )}

      {!isLoading && !loadError && data && data.content.length === 0 && (
        <p className="rounded-lg border border-zinc-200 bg-white px-4 py-6 text-sm text-zinc-600">
          {labels.empty}
        </p>
      )}

      {!isLoading && !loadError && data && data.content.length > 0 && (
        <OfferGrid offers={data.content} labels={labels.grid} />
      )}
    </>
  );
}
