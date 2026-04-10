"use client";

import { useState } from "react";
import type { PriceSenseResponseJson } from "@/lib/price-sense-types";
import { formatChf } from "@/lib/format-chf";

type Labels = {
  introTitle: string;
  introBody: string;
  brand: string;
  model: string;
  modelYear: string;
  modelYearHint: string;
  condition: string;
  conditionHint: string;
  buyIn: string;
  buyInHint: string;
  submit: string;
  loading: string;
  errorApi: string;
  errorHttp: string;
  validateForm: string;
  benchmarkTitle: string;
  median: string;
  p25: string;
  p75: string;
  recommendTitle: string;
  recommendHint: string;
  floor: string;
  margin: string;
  confidence: string;
  swissCount: string;
  deCount: string;
  fallback: string;
  comparablesTitle: string;
  colSource: string;
  colCountry: string;
  colBike: string;
  colYear: string;
  colCond: string;
  colPrice: string;
  linkListing: string;
  noComparables: string;
  insufficientTitle: string;
  marginConflict: string;
  gradeA: string;
  gradeB: string;
  gradeC: string;
  gradeD: string;
  liveProbesTitle: string;
  liveBenchmarkBadge: string;
  colLiveShop: string;
  colLivePrice: string;
  colLiveLink: string;
  colLiveError: string;
};

const GRADES = ["A", "B", "C", "D"] as const;

export function CompetitivePricingTool({ labels }: { labels: Labels }) {
  const [brand, setBrand] = useState("");
  const [model, setModel] = useState("");
  const [year, setYear] = useState("");
  const [grade, setGrade] = useState<(typeof GRADES)[number]>("B");
  const [buyIn, setBuyIn] = useState("");
  const [loading, setLoading] = useState(false);
  const [err, setErr] = useState<string | null>(null);
  const [data, setData] = useState<PriceSenseResponseJson | null>(null);

  async function onSubmit(e: React.FormEvent) {
    e.preventDefault();
    setErr(null);
    setData(null);
    const buyNum = Number(buyIn.replace(",", "."));
    if (!brand.trim() || !model.trim() || Number.isNaN(buyNum) || buyNum < 0) {
      setErr(labels.validateForm);
      return;
    }
    let y: number | null = parseInt(year, 10);
    if (year.trim() === "" || Number.isNaN(y)) {
      y = null;
    }
    setLoading(true);
    try {
      const res = await fetch("/api/price-sense/recommend", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          brand: brand.trim(),
          model: model.trim(),
          modelYear: y,
          conditionGrade: grade,
          buyInCostChf: buyNum,
        }),
      });
      const text = await res.text();
      if (!res.ok) {
        setErr(`${labels.errorHttp} (${res.status})`);
        try {
          const j = JSON.parse(text) as { message?: string };
          if (j.message) {
            setErr(j.message);
          }
        } catch {
          /* ignore */
        }
        return;
      }
      const parsed = JSON.parse(text) as PriceSenseResponseJson;
      setData({
        ...parsed,
        liveProbes: parsed.liveProbes ?? [],
        liveBenchmarkUsed: parsed.liveBenchmarkUsed ?? false,
      });
    } catch {
      setErr(labels.errorApi);
    } finally {
      setLoading(false);
    }
  }

  function gradeLabel(g: string) {
    switch (g) {
      case "A":
        return labels.gradeA;
      case "B":
        return labels.gradeB;
      case "C":
        return labels.gradeC;
      case "D":
        return labels.gradeD;
      default:
        return g;
    }
  }

  return (
    <div className="space-y-10">
      <section className="rounded-lg border border-zinc-200 bg-zinc-50/80 px-4 py-4 text-sm leading-relaxed text-zinc-700">
        <h2 className="font-medium text-zinc-900">{labels.introTitle}</h2>
        <p className="mt-2 whitespace-pre-line">{labels.introBody}</p>
      </section>

      <form onSubmit={onSubmit} className="space-y-4 max-w-xl">
        <div className="grid gap-4 sm:grid-cols-2">
          <label className="block text-sm">
            <span className="text-zinc-700">{labels.brand}</span>
            <input
              required
              className="mt-1 w-full rounded-md border border-zinc-300 px-3 py-2 text-zinc-900"
              value={brand}
              onChange={(e) => setBrand(e.target.value)}
              autoComplete="off"
            />
          </label>
          <label className="block text-sm">
            <span className="text-zinc-700">{labels.model}</span>
            <input
              required
              className="mt-1 w-full rounded-md border border-zinc-300 px-3 py-2 text-zinc-900"
              value={model}
              onChange={(e) => setModel(e.target.value)}
              autoComplete="off"
            />
          </label>
        </div>
        <div className="grid gap-4 sm:grid-cols-2">
          <label className="block text-sm">
            <span className="text-zinc-700">{labels.modelYear}</span>
            <input
              type="number"
              min={1990}
              max={2035}
              className="mt-1 w-full rounded-md border border-zinc-300 px-3 py-2 text-zinc-900"
              value={year}
              onChange={(e) => setYear(e.target.value)}
              placeholder="2023"
            />
            <span className="mt-1 block text-xs text-zinc-500">{labels.modelYearHint}</span>
          </label>
          <label className="block text-sm">
            <span className="text-zinc-700">{labels.condition}</span>
            <select
              className="mt-1 w-full rounded-md border border-zinc-300 px-3 py-2 text-zinc-900"
              value={grade}
              onChange={(e) => setGrade(e.target.value as (typeof GRADES)[number])}
            >
              {GRADES.map((g) => (
                <option key={g} value={g}>
                  {g} — {gradeLabel(g)}
                </option>
              ))}
            </select>
            <span className="mt-1 block text-xs text-zinc-500">{labels.conditionHint}</span>
          </label>
        </div>
        <label className="block text-sm">
          <span className="text-zinc-700">{labels.buyIn}</span>
          <input
            required
            type="text"
            inputMode="decimal"
            className="mt-1 w-full max-w-xs rounded-md border border-zinc-300 px-3 py-2 text-zinc-900"
            value={buyIn}
            onChange={(e) => setBuyIn(e.target.value)}
            placeholder="1800"
          />
          <span className="mt-1 block text-xs text-zinc-500">{labels.buyInHint}</span>
        </label>
        <button
          type="submit"
          disabled={loading}
          className="rounded-lg bg-zinc-900 px-4 py-2.5 text-sm font-medium text-white hover:bg-zinc-800 disabled:opacity-60"
        >
          {loading ? labels.loading : labels.submit}
        </button>
      </form>

      {err && (
        <p className="rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-900">
          {err}
        </p>
      )}

      {data?.insufficientData && (
        <div className="rounded-lg border border-amber-200 bg-amber-50 px-4 py-3 text-sm text-amber-950">
          <p className="font-medium">{labels.insufficientTitle}</p>
          {data.explanation && <p className="mt-2 whitespace-pre-line">{data.explanation}</p>}
        </div>
      )}

      {data && data.liveProbes.length > 0 && (
        <section className="space-y-3">
          <h3 className="text-lg font-semibold text-zinc-900">{labels.liveProbesTitle}</h3>
          <div className="overflow-x-auto rounded-lg border border-zinc-200">
            <table className="w-full min-w-[520px] text-left text-sm">
              <thead className="border-b border-zinc-200 bg-zinc-50 text-xs uppercase tracking-wide text-zinc-600">
                <tr>
                  <th className="px-3 py-2">{labels.colLiveShop}</th>
                  <th className="px-3 py-2">{labels.colLivePrice}</th>
                  <th className="px-3 py-2">{labels.colLiveLink}</th>
                  <th className="px-3 py-2">{labels.colLiveError}</th>
                </tr>
              </thead>
              <tbody>
                {data.liveProbes.map((row) => (
                  <tr key={row.slug} className="border-b border-zinc-100">
                    <td className="px-3 py-2 text-zinc-800">{row.displayName}</td>
                    <td className="px-3 py-2 tabular-nums text-zinc-900">
                      {row.priceChf != null ? formatChf(row.priceChf) : "—"}
                    </td>
                    <td className="px-3 py-2">
                      {row.productUrl ? (
                        <a
                          href={row.productUrl}
                          target="_blank"
                          rel="noopener noreferrer"
                          className="text-zinc-700 underline underline-offset-2 hover:text-zinc-900"
                        >
                          {labels.colLiveLink}
                        </a>
                      ) : (
                        "—"
                      )}
                    </td>
                    <td className="px-3 py-2 text-xs text-zinc-600">
                      {row.errorMessage ?? "—"}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </section>
      )}

      {data && !data.insufficientData && (
        <div className="space-y-8">
          <section>
            <h3 className="text-lg font-semibold text-zinc-900">{labels.benchmarkTitle}</h3>
            <dl className="mt-3 grid gap-2 text-sm sm:grid-cols-3">
              <div className="rounded-lg border border-zinc-200 bg-white px-3 py-2">
                <dt className="text-zinc-500">{labels.median}</dt>
                <dd className="font-medium tabular-nums text-zinc-900">
                  {data.pMedianChf != null ? formatChf(data.pMedianChf) : "—"}
                </dd>
              </div>
              <div className="rounded-lg border border-zinc-200 bg-white px-3 py-2">
                <dt className="text-zinc-500">{labels.p25}</dt>
                <dd className="font-medium tabular-nums text-zinc-900">
                  {data.pP25Chf != null ? formatChf(data.pP25Chf) : "—"}
                </dd>
              </div>
              <div className="rounded-lg border border-zinc-200 bg-white px-3 py-2">
                <dt className="text-zinc-500">{labels.p75}</dt>
                <dd className="font-medium tabular-nums text-zinc-900">
                  {data.pP75Chf != null ? formatChf(data.pP75Chf) : "—"}
                </dd>
              </div>
            </dl>
            {data.explanation && (
              <p className="mt-3 text-sm leading-relaxed text-zinc-600 whitespace-pre-line">
                {data.explanation}
              </p>
            )}
            {data.liveBenchmarkUsed && (
              <p className="mt-2 rounded-md border border-sky-200 bg-sky-50 px-3 py-2 text-xs text-sky-950">
                {labels.liveBenchmarkBadge}
              </p>
            )}
            <p className="mt-2 text-xs text-zinc-500">
              {labels.swissCount}: {data.nSwissListings} · {labels.deCount}: {data.nGermanListings}
              {data.fallbackUsed ? ` · ${labels.fallback}` : ""} · {labels.confidence}:{" "}
              {data.confidence}
            </p>
          </section>

          <section className="rounded-xl border border-emerald-200 bg-emerald-50/60 px-4 py-4">
            <h3 className="text-lg font-semibold text-emerald-950">{labels.recommendTitle}</h3>
            <p className="mt-1 text-sm text-emerald-900/90">{labels.recommendHint}</p>
            <p className="mt-3 text-3xl font-semibold tabular-nums text-emerald-950">
              {data.pRecommendChf != null ? formatChf(data.pRecommendChf) : "—"}
            </p>
            <dl className="mt-4 grid gap-2 text-sm text-emerald-900 sm:grid-cols-2">
              <div>
                <dt className="text-emerald-800/80">{labels.floor}</dt>
                <dd className="font-medium tabular-nums">
                  {data.pFloorChf != null ? formatChf(data.pFloorChf) : "—"}
                </dd>
              </div>
              <div>
                <dt className="text-emerald-800/80">{labels.margin}</dt>
                <dd className="font-medium tabular-nums">
                  {data.grossMarginPct != null ? `${data.grossMarginPct.toFixed(1)}%` : "—"}
                </dd>
              </div>
            </dl>
            {data.marginConflict && data.marginMessage && (
              <p className="mt-3 text-sm font-medium text-amber-900">{labels.marginConflict}</p>
            )}
            {data.marginConflict && data.marginMessage && (
              <p className="mt-1 text-sm text-amber-950/90 whitespace-pre-line">
                {data.marginMessage}
              </p>
            )}
          </section>

          <section>
            <h3 className="text-lg font-semibold text-zinc-900">{labels.comparablesTitle}</h3>
            {data.sampleComparables.length === 0 ? (
              <p className="mt-2 text-sm text-zinc-600">{labels.noComparables}</p>
            ) : (
              <div className="mt-3 overflow-x-auto rounded-lg border border-zinc-200">
                <table className="w-full min-w-[640px] text-left text-sm">
                  <thead className="border-b border-zinc-200 bg-zinc-50 text-xs uppercase tracking-wide text-zinc-600">
                    <tr>
                      <th className="px-3 py-2">{labels.colSource}</th>
                      <th className="px-3 py-2">{labels.colCountry}</th>
                      <th className="px-3 py-2">{labels.colBike}</th>
                      <th className="px-3 py-2">{labels.colYear}</th>
                      <th className="px-3 py-2">{labels.colCond}</th>
                      <th className="px-3 py-2">{labels.colPrice}</th>
                      <th className="px-3 py-2" />
                    </tr>
                  </thead>
                  <tbody>
                    {data.sampleComparables.map((c, i) => (
                      <tr key={`${c.sourceUrl}-${i}`} className="border-b border-zinc-100">
                        <td className="px-3 py-2 text-zinc-800">{c.sourceName}</td>
                        <td className="px-3 py-2 text-zinc-600">{c.countryCode}</td>
                        <td className="px-3 py-2">
                          {[c.brand, c.model].filter(Boolean).join(" ") || "—"}
                        </td>
                        <td className="px-3 py-2 tabular-nums text-zinc-600">
                          {c.modelYear ?? "—"}
                        </td>
                        <td className="px-3 py-2 text-zinc-600">{c.bikeCondition}</td>
                        <td className="px-3 py-2 tabular-nums text-zinc-900">
                          {c.landedPriceChf != null ? formatChf(c.landedPriceChf) : "—"}
                        </td>
                        <td className="px-3 py-2">
                          <a
                            href={c.sourceUrl}
                            target="_blank"
                            rel="noopener noreferrer"
                            className="text-zinc-700 underline underline-offset-2 hover:text-zinc-900"
                          >
                            {labels.linkListing}
                          </a>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </section>
        </div>
      )}
    </div>
  );
}
