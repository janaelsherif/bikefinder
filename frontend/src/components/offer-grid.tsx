import { formatChf } from "@/lib/format-chf";
import type { OfferSummary } from "@/lib/offers-api";

export type OfferGridLabels = {
  cta: string;
  bargain: string;
  topDeal: string;
  discountVsCh: string;
};

export function OfferCard({
  offer: o,
  labels,
  className = "",
}: {
  offer: OfferSummary;
  labels: OfferGridLabels;
  /** Extra classes on the card root (e.g. border highlight). */
  className?: string;
}) {
  return (
    <div
      className={`group flex h-full flex-col overflow-hidden rounded-2xl border border-zinc-200/90 bg-white shadow-card transition-shadow duration-200 hover:shadow-card-hover ${className}`.trim()}
    >
      <div className="relative aspect-[16/10] overflow-hidden bg-zinc-100">
        {o.imageUrl ? (
          // eslint-disable-next-line @next/next/no-img-element
          <img
            src={o.imageUrl}
            alt=""
            className="h-full w-full object-cover transition duration-300 group-hover:scale-[1.02]"
          />
        ) : (
          <div className="flex h-full items-center justify-center text-xs text-zinc-400">
            E-Bike
          </div>
        )}
        <div className="absolute left-2 top-2 flex flex-wrap gap-1">
          {o.topDeal && (
            <span className="rounded bg-emerald-600 px-2 py-0.5 text-xs font-medium text-white">
              {labels.topDeal}
            </span>
          )}
          {o.bargain && !o.topDeal && (
            <span className="rounded bg-sky-600 px-2 py-0.5 text-xs font-medium text-white">
              {labels.bargain}
            </span>
          )}
        </div>
      </div>
      <div className="flex flex-1 flex-col gap-2 p-4">
        <div>
          <p className="text-xs uppercase tracking-wide text-zinc-500">
            {o.sourceName} · {o.countryCode}
          </p>
          <h2 className="text-lg font-medium text-zinc-900">
            {[o.brand, o.model].filter(Boolean).join(" ") || "—"}
            {o.modelYear ? ` · ${o.modelYear}` : ""}
          </h2>
        </div>
        <p className="text-sm text-zinc-600">
          {[o.motorBrand, o.batteryWh ? `${o.batteryWh} Wh` : null]
            .filter(Boolean)
            .join(" · ")}
          {o.mileageKm != null ? ` · ${o.mileageKm} km` : ""}
        </p>
        <div className="mt-auto flex flex-wrap items-end justify-between gap-2 pt-2">
          <div>
            <p className="text-2xl font-semibold tabular-nums text-zinc-900">
              {formatChf(o.landedPriceChf)}
            </p>
            {o.discountVsSwissPct != null && (
              <p className="text-sm font-medium text-emerald-700">
                {o.discountVsSwissPct.toFixed(0)}% {labels.discountVsCh}
              </p>
            )}
          </div>
          <a
            href={o.sourceUrl}
            target="_blank"
            rel="noopener noreferrer"
            className="rounded-lg bg-zinc-900 px-3 py-2 text-sm font-medium text-white hover:bg-zinc-800"
          >
            {labels.cta}
          </a>
        </div>
      </div>
    </div>
  );
}

export function OfferGrid({
  offers,
  labels,
}: {
  offers: OfferSummary[];
  labels: OfferGridLabels;
}) {
  return (
    <ul className="grid gap-5 sm:grid-cols-2 lg:grid-cols-3">
      {offers.map((o) => (
        <li key={o.id}>
          <OfferCard offer={o} labels={labels} />
        </li>
      ))}
    </ul>
  );
}
