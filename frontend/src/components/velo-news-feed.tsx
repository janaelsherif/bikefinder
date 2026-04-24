"use client";

import { useCallback, useEffect, useRef, useState } from "react";
import { OfferCard } from "@/components/offer-grid";
import {
  VELO_NEWS_POLL_MS,
  fetchOffersPageViaProxy,
  type SpringPage,
} from "@/lib/offers-api";

export type VeloNewsFeedLabels = {
  lastUpdated: string;
  refreshIn: string;
  newBadge: string;
  polling: string;
  error: string;
  empty: string;
  grid: {
    cta: string;
    bargain: string;
    topDeal: string;
    discountVsCh: string;
  };
};

const FEED_PARAMS = {
  offerSort: "newest",
  nearbyMarkets: "true",
} as const;

export function VeloNewsFeed({ labels }: { labels: VeloNewsFeedLabels }) {
  const [data, setData] = useState<SpringPage | null | undefined>(undefined);
  const [lastOkAt, setLastOkAt] = useState<Date | null>(null);
  const [secondsToPoll, setSecondsToPoll] = useState(Math.ceil(VELO_NEWS_POLL_MS / 1000));
  const seenIdsRef = useRef<Set<string>>(new Set());
  const [freshIds, setFreshIds] = useState<Set<string>>(new Set());

  const load = useCallback(async () => {
    const page = await fetchOffersPageViaProxy(
      { ...FEED_PARAMS },
      { size: 30 },
    );
    setData(page ?? null);
    if (page) {
      setLastOkAt(new Date());
      const prev = seenIdsRef.current;
      const nextFresh = new Set<string>();
      if (prev.size > 0) {
        for (const o of page.content) {
          if (!prev.has(o.id)) {
            nextFresh.add(o.id);
          }
        }
      }
      seenIdsRef.current = new Set(page.content.map((o) => o.id));
      setFreshIds(nextFresh);
    }
  }, []);

  useEffect(() => {
    void load();
  }, [load]);

  useEffect(() => {
    const tick = window.setInterval(() => {
      setSecondsToPoll((s) => {
        if (s <= 1) {
          void load();
          return Math.ceil(VELO_NEWS_POLL_MS / 1000);
        }
        return s - 1;
      });
    }, 1000);
    return () => window.clearInterval(tick);
  }, [load]);

  if (data === undefined) {
    return (
      <p className="rounded-lg border border-zinc-200 bg-white px-4 py-6 text-sm text-zinc-600">
        {labels.polling}
      </p>
    );
  }

  if (data === null) {
    return (
      <p className="rounded-lg border border-amber-200 bg-amber-50 px-4 py-3 text-sm text-amber-900">
        {labels.error}
      </p>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-2 sm:flex-row sm:flex-wrap sm:items-center sm:justify-between">
        <p className="text-sm text-zinc-600">
          {lastOkAt
            ? `${labels.lastUpdated}: ${lastOkAt.toLocaleTimeString()}`
            : labels.polling}
        </p>
        <p className="text-sm font-medium text-sky-800">
          {labels.refreshIn.replace("{n}", String(secondsToPoll))}
        </p>
      </div>

      {data.content.length === 0 ? (
        <p className="rounded-lg border border-zinc-200 bg-white px-4 py-6 text-sm text-zinc-600">
          {labels.empty}
        </p>
      ) : (
        <ul className="grid gap-5 sm:grid-cols-2 lg:grid-cols-3">
          {data.content.map((o) => {
            const isFresh = freshIds.has(o.id);
            return (
              <li key={o.id} className="relative list-none">
                {isFresh && (
                  <span className="absolute right-3 top-3 z-10 rounded-full bg-sky-600 px-2.5 py-0.5 text-xs font-semibold text-white shadow">
                    {labels.newBadge}
                  </span>
                )}
                <OfferCard
                  offer={o}
                  labels={labels.grid}
                  className={
                    isFresh ? "border-sky-400/90 ring-1 ring-sky-300/60" : ""
                  }
                />
              </li>
            );
          })}
        </ul>
      )}
    </div>
  );
}
