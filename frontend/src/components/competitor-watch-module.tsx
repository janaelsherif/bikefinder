import type {
  CompetitorWatchDashboardRow,
  CompetitorWatchSnapshot,
} from "@/lib/competitor-watch-api";

const ALERT_DELTA_THRESHOLD = 5;

/** Hamza Module 5 competitor order (matches DB seeds). */
export const COMPETITOR_WATCH_SLUGS = [
  "veloplus",
  "upway_ch",
  "rebike_ch",
  "bibibike",
  "velocorner",
] as const;

export type CompetitorWatchCopy = {
  title: string;
  subtitle: string;
  missionTitle: string;
  qInventory: string;
  qPricing: string;
  qMarket: string;
  briefTitle: string;
  colCompetitor: string;
  colMonitor: string;
  colSource: string;
  colAlert: string;
  rowVeloplus: { monitor: string; source: string; alert: string };
  rowUpway: { monitor: string; source: string; alert: string };
  rowRebike: { monitor: string; source: string; alert: string };
  rowBibibike: { monitor: string; source: string; alert: string };
  rowVelocorner: { monitor: string; source: string; alert: string };
  nameVeloplus: string;
  nameUpway: string;
  nameRebike: string;
  nameBibibike: string;
  nameVelocorner: string;
  signalsTitle: string;
  signalsHint: string;
  colLastSnapshot: string;
  colEstimate: string;
  colDelta: string;
  colHttp: string;
  colDuration: string;
  signalAlert: string;
  signalCalm: string;
  openShop: string;
  noSnapshot: string;
  error: string;
  historyTitle: string;
  historyWhen: string;
  historyEstimate: string;
  historyDelta: string;
  technicalNote: string;
};

function briefRow(
  slug: string,
  labels: CompetitorWatchCopy,
): { monitor: string; source: string; alert: string } | null {
  switch (slug) {
    case "veloplus":
      return labels.rowVeloplus;
    case "upway_ch":
      return labels.rowUpway;
    case "rebike_ch":
      return labels.rowRebike;
    case "bibibike":
      return labels.rowBibibike;
    case "velocorner":
      return labels.rowVelocorner;
    default:
      return null;
  }
}

function competitorDisplayName(slug: string, labels: CompetitorWatchCopy): string {
  switch (slug) {
    case "veloplus":
      return labels.nameVeloplus;
    case "upway_ch":
      return labels.nameUpway;
    case "rebike_ch":
      return labels.nameRebike;
    case "bibibike":
      return labels.nameBibibike;
    case "velocorner":
      return labels.nameVelocorner;
    default:
      return slug;
  }
}

function formatWhen(iso: string, locale: string) {
  try {
    const loc = locale === "en" ? "en-GB" : "de-CH";
    return new Intl.DateTimeFormat(loc, {
      dateStyle: "medium",
      timeStyle: "short",
    }).format(new Date(iso));
  } catch {
    return iso;
  }
}

export function CompetitorWatchModule({
  locale,
  dashboard,
  histories,
  labels,
}: {
  locale: string;
  dashboard: CompetitorWatchDashboardRow[] | null;
  histories: Record<string, CompetitorWatchSnapshot[]>;
  labels: CompetitorWatchCopy;
}) {
  return (
    <div className="space-y-12">
      <header className="rounded-2xl border border-zinc-200/80 bg-white/90 p-8 shadow-card backdrop-blur-sm sm:p-10">
        <p className="text-xs font-semibold uppercase tracking-widest text-zinc-500">
          Module 5
        </p>
        <h1 className="mt-2 text-3xl font-semibold tracking-tight text-zinc-900 sm:text-4xl">
          {labels.title}
        </h1>
        <p className="mt-3 max-w-3xl text-base leading-relaxed text-zinc-600">
          {labels.subtitle}
        </p>
        <div className="mt-8 rounded-xl border border-zinc-100 bg-zinc-50/80 p-6">
          <h2 className="text-sm font-semibold text-zinc-900">
            {labels.missionTitle}
          </h2>
          <ul className="mt-3 list-inside list-disc space-y-2 text-sm leading-relaxed text-zinc-700">
            <li>{labels.qInventory}</li>
            <li>{labels.qPricing}</li>
            <li>{labels.qMarket}</li>
          </ul>
        </div>
      </header>

      <section aria-labelledby="brief-table">
        <h2
          id="brief-table"
          className="mb-4 text-lg font-semibold text-zinc-900"
        >
          {labels.briefTitle}
        </h2>
        <div className="overflow-x-auto rounded-2xl border border-zinc-200/90 bg-white shadow-card">
          <table className="w-full min-w-[640px] text-left text-sm">
            <thead>
              <tr className="border-b border-zinc-200 bg-zinc-50/90 text-xs font-semibold uppercase tracking-wide text-zinc-600">
                <th className="px-4 py-3">{labels.colCompetitor}</th>
                <th className="px-4 py-3">{labels.colMonitor}</th>
                <th className="px-4 py-3">{labels.colSource}</th>
                <th className="px-4 py-3">{labels.colAlert}</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-zinc-100 text-zinc-800">
              {COMPETITOR_WATCH_SLUGS.map((slug) => {
                const b = briefRow(slug, labels);
                if (!b) {
                  return null;
                }
                const row = dashboard?.find((r) => r.target.slug === slug);
                const name = row?.target.displayName ?? competitorDisplayName(slug, labels);
                return (
                  <tr key={slug} className="align-top">
                    <td className="px-4 py-3 font-medium text-zinc-900">{name}</td>
                    <td className="px-4 py-3 text-zinc-700">{b.monitor}</td>
                    <td className="px-4 py-3 text-zinc-600">{b.source}</td>
                    <td className="px-4 py-3 text-zinc-700">{b.alert}</td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
        <p className="mt-3 text-xs leading-relaxed text-zinc-500">
          {labels.technicalNote}
        </p>
      </section>

      <section aria-labelledby="signals">
        <h2 id="signals" className="mb-2 text-lg font-semibold text-zinc-900">
          {labels.signalsTitle}
        </h2>
        <p className="mb-6 max-w-3xl text-sm text-zinc-600">{labels.signalsHint}</p>

        {!dashboard && (
          <p className="rounded-xl border border-amber-200 bg-amber-50 px-4 py-3 text-sm text-amber-950">
            {labels.error}
          </p>
        )}

        {dashboard && dashboard.length === 0 && (
          <p className="rounded-xl border border-zinc-200 bg-white px-4 py-6 text-sm text-zinc-600">
            {labels.noSnapshot}
          </p>
        )}

        {dashboard && dashboard.length > 0 && (
          <div className="space-y-6">
            {COMPETITOR_WATCH_SLUGS.map((slug) => {
              const row = dashboard.find((r) => r.target.slug === slug);
              if (!row) {
                return null;
              }
              const snap = row.latestSnapshot;
              const delta = snap?.deltaVsPrevious ?? null;
              const alertOn =
                delta != null && Math.abs(delta) >= ALERT_DELTA_THRESHOLD;

              return (
                <div
                  key={slug}
                  className="overflow-hidden rounded-2xl border border-zinc-200/90 bg-white shadow-card"
                >
                  <div className="flex flex-wrap items-start justify-between gap-4 border-b border-zinc-100 bg-zinc-50/80 px-5 py-4">
                    <div>
                      <h3 className="text-base font-semibold text-zinc-900">
                        {row.target.displayName}
                      </h3>
                      <a
                        href={row.target.watchUrl}
                        target="_blank"
                        rel="noopener noreferrer"
                        className="mt-1 inline-block text-sm font-medium text-sky-700 underline-offset-2 hover:underline"
                      >
                        {labels.openShop}
                      </a>
                    </div>
                    {snap ? (
                      <div className="flex flex-wrap items-center gap-2">
                        {alertOn ? (
                          <span className="rounded-full bg-amber-100 px-3 py-1 text-xs font-semibold text-amber-950 ring-1 ring-amber-200">
                            {labels.signalAlert}
                          </span>
                        ) : (
                          <span className="rounded-full bg-emerald-50 px-3 py-1 text-xs font-medium text-emerald-900 ring-1 ring-emerald-200/80">
                            {labels.signalCalm}
                          </span>
                        )}
                      </div>
                    ) : null}
                  </div>

                  <div className="grid gap-4 px-5 py-4 sm:grid-cols-2 lg:grid-cols-4">
                    <div>
                      <p className="text-xs font-medium uppercase tracking-wide text-zinc-500">
                        {labels.colLastSnapshot}
                      </p>
                      <p className="mt-1 text-sm font-medium text-zinc-900">
                        {snap
                          ? formatWhen(snap.capturedAt, locale)
                          : "—"}
                      </p>
                    </div>
                    <div>
                      <p className="text-xs font-medium uppercase tracking-wide text-zinc-500">
                        {labels.colEstimate}
                      </p>
                      <p className="mt-1 text-sm font-semibold tabular-nums text-zinc-900">
                        {snap?.listingCountEstimate != null
                          ? snap.listingCountEstimate
                          : "—"}
                      </p>
                    </div>
                    <div>
                      <p className="text-xs font-medium uppercase tracking-wide text-zinc-500">
                        {labels.colDelta}
                      </p>
                      <p
                        className={`mt-1 text-sm font-semibold tabular-nums ${
                          delta != null && delta > 0
                            ? "text-emerald-700"
                            : delta != null && delta < 0
                              ? "text-rose-700"
                              : "text-zinc-900"
                        }`}
                      >
                        {delta != null ? (delta > 0 ? `+${delta}` : `${delta}`) : "—"}
                      </p>
                    </div>
                    <div>
                      <p className="text-xs font-medium uppercase tracking-wide text-zinc-500">
                        {labels.colHttp} / {labels.colDuration}
                      </p>
                      <p className="mt-1 text-sm tabular-nums text-zinc-800">
                        {snap?.httpStatus != null ? snap.httpStatus : "—"}
                        {snap?.durationMs != null
                          ? ` · ${snap.durationMs} ms`
                          : ""}
                      </p>
                    </div>
                  </div>

                  {snap?.errorMessage ? (
                    <div className="border-t border-zinc-100 bg-rose-50/80 px-5 py-3 text-sm text-rose-950">
                      {snap.errorMessage}
                    </div>
                  ) : null}

                  <details className="group border-t border-zinc-100 bg-zinc-50/50">
                    <summary className="cursor-pointer px-5 py-3 text-sm font-medium text-zinc-800 hover:bg-zinc-100/80">
                      {labels.historyTitle}
                    </summary>
                    <div className="overflow-x-auto px-3 pb-4">
                      <SnapshotHistoryTable
                        rows={histories[row.target.slug] ?? []}
                        locale={locale}
                        labels={labels}
                      />
                    </div>
                  </details>
                </div>
              );
            })}
          </div>
        )}
      </section>
    </div>
  );
}

function SnapshotHistoryTable({
  rows,
  locale,
  labels,
}: {
  rows: CompetitorWatchSnapshot[];
  locale: string;
  labels: CompetitorWatchCopy;
}) {
  if (rows.length === 0) {
    return (
      <p className="px-2 py-3 text-sm text-zinc-500">{labels.noSnapshot}</p>
    );
  }
  return (
    <table className="w-full min-w-[480px] text-left text-xs">
      <thead>
        <tr className="border-b border-zinc-200 text-zinc-600">
          <th className="px-2 py-2 font-medium">{labels.historyWhen}</th>
          <th className="px-2 py-2 font-medium">{labels.historyEstimate}</th>
          <th className="px-2 py-2 font-medium">{labels.historyDelta}</th>
        </tr>
      </thead>
      <tbody className="divide-y divide-zinc-100">
        {rows.map((r) => (
          <tr key={r.id}>
            <td className="px-2 py-2 text-zinc-800">
              {formatWhen(r.capturedAt, locale)}
            </td>
            <td className="px-2 py-2 tabular-nums text-zinc-800">
              {r.listingCountEstimate ?? "—"}
            </td>
            <td className="px-2 py-2 tabular-nums text-zinc-800">
              {r.deltaVsPrevious != null
                ? r.deltaVsPrevious > 0
                  ? `+${r.deltaVsPrevious}`
                  : `${r.deltaVsPrevious}`
                : "—"}
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}
