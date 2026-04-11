"use client";

import { useState } from "react";

export type CompetitorWatchAiBriefLabels = {
  title: string;
  hint: string;
  focusPlaceholder: string;
  button: string;
  loading: string;
  errorPrefix: string;
  perplexityNote: string;
  noPerplexityNote: string;
};

type BriefResponse = {
  markdown: string;
  usedPerplexity: boolean;
  anthropicModel: string;
  perplexityModel: string;
};

export function CompetitorWatchBriefPanel({
  labels,
}: {
  labels: CompetitorWatchAiBriefLabels;
}) {
  const [focus, setFocus] = useState("");
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState<string | null>(null);
  const [meta, setMeta] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  async function run() {
    setLoading(true);
    setError(null);
    setResult(null);
    setMeta(null);
    try {
      const res = await fetch("/api/competitor-watch/brief", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          focus: focus.trim() ? focus.trim() : undefined,
        }),
      });
      const text = await res.text();
      if (!res.ok) {
        setError(text || `${res.status} ${res.statusText}`);
        return;
      }
      const data = JSON.parse(text) as BriefResponse;
      setResult(data.markdown);
      const m = data.usedPerplexity
        ? `${labels.perplexityNote} · Claude ${data.anthropicModel} · Perplexity ${data.perplexityModel}`
        : `${labels.noPerplexityNote} · Claude ${data.anthropicModel}`;
      setMeta(m);
    } catch (e) {
      setError(e instanceof Error ? e.message : String(e));
    } finally {
      setLoading(false);
    }
  }

  return (
    <section
      aria-labelledby="ai-brief"
      className="rounded-2xl border border-sky-200/80 bg-gradient-to-b from-sky-50/90 to-white p-6 shadow-card sm:p-8"
    >
      <h2
        id="ai-brief"
        className="text-lg font-semibold tracking-tight text-zinc-900"
      >
        {labels.title}
      </h2>
      <p className="mt-2 max-w-3xl text-sm leading-relaxed text-zinc-600">
        {labels.hint}
      </p>
      <div className="mt-4 flex flex-col gap-3 sm:flex-row sm:items-end">
        <label className="block min-w-0 flex-1">
          <span className="sr-only">{labels.focusPlaceholder}</span>
          <textarea
            value={focus}
            onChange={(e) => setFocus(e.target.value)}
            placeholder={labels.focusPlaceholder}
            rows={2}
            className="w-full rounded-xl border border-zinc-200 bg-white px-3 py-2 text-sm text-zinc-900 shadow-sm placeholder:text-zinc-400 focus:border-sky-400 focus:outline-none focus:ring-2 focus:ring-sky-200"
          />
        </label>
        <button
          type="button"
          onClick={() => void run()}
          disabled={loading}
          className="shrink-0 rounded-xl bg-zinc-900 px-5 py-2.5 text-sm font-semibold text-white shadow-sm transition hover:bg-zinc-800 disabled:cursor-not-allowed disabled:opacity-60"
        >
          {loading ? labels.loading : labels.button}
        </button>
      </div>
      {error ? (
        <p
          className="mt-4 rounded-xl border border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-950"
          role="alert"
        >
          {labels.errorPrefix} {error}
        </p>
      ) : null}
      {result ? (
        <div className="mt-6 border-t border-zinc-100 pt-6">
          {meta ? (
            <p className="mb-3 text-xs text-zinc-500">{meta}</p>
          ) : null}
          <div className="rounded-xl border border-zinc-100 bg-zinc-50/80 p-4 text-sm leading-relaxed text-zinc-800">
            <pre className="whitespace-pre-wrap font-sans">{result}</pre>
          </div>
        </div>
      ) : null}
    </section>
  );
}
