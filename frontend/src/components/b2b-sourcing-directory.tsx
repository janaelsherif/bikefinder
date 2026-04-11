import {
  type SourcingConfidence,
  type SourcingRegionId,
  SOURCING_REGION_ORDER,
  b2bSourcingRows,
} from "@/data/b2b-sourcing-directory";

export type B2bSourcingLabels = {
  colPlatform: string;
  colType: string;
  colPrice: string;
  colB2b: string;
  colConfidence: string;
  confidence: Record<SourcingConfidence, string>;
  regions: Record<
    SourcingRegionId,
    { title: string; intro: string; benchmark: string }
  >;
  footnote: string;
};

function confidenceClass(c: SourcingConfidence): string {
  switch (c) {
    case "HIGH":
      return "bg-emerald-100 text-emerald-900 ring-1 ring-emerald-200";
    case "MEDIUM":
      return "bg-amber-100 text-amber-950 ring-1 ring-amber-200";
    default:
      return "bg-zinc-100 text-zinc-700 ring-1 ring-zinc-200";
  }
}

export function B2bSourcingDirectory({ labels }: { labels: B2bSourcingLabels }) {
  return (
    <div className="space-y-12">
      {SOURCING_REGION_ORDER.map((regionId) => {
        const block = labels.regions[regionId];
        const rows = b2bSourcingRows[regionId];
        return (
          <section
            key={regionId}
            id={regionId}
            className="scroll-mt-24 rounded-2xl border border-zinc-200/90 bg-white/95 p-6 shadow-card sm:p-8"
            aria-labelledby={`heading-${regionId}`}
          >
            <h2
              id={`heading-${regionId}`}
              className="text-xl font-semibold tracking-tight text-zinc-900 sm:text-2xl"
            >
              {block.title}
            </h2>
            <p className="mt-3 max-w-3xl text-sm leading-relaxed text-zinc-600 sm:text-base">
              {block.intro}
            </p>
            <p className="mt-4 rounded-lg border border-sky-100 bg-sky-50/80 px-4 py-3 text-sm font-medium text-sky-950">
              {block.benchmark}
            </p>
            <div className="mt-6 overflow-x-auto">
              <table className="w-full min-w-[720px] text-left text-sm">
                <thead>
                  <tr className="border-b border-zinc-200 text-xs font-semibold uppercase tracking-wide text-zinc-500">
                    <th className="py-3 pr-3">{labels.colPlatform}</th>
                    <th className="py-3 pr-3">{labels.colType}</th>
                    <th className="py-3 pr-3">{labels.colPrice}</th>
                    <th className="min-w-[200px] py-3 pr-3">{labels.colB2b}</th>
                    <th className="py-3 pr-3">{labels.colConfidence}</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-zinc-100">
                  {rows.map((row) => (
                    <tr key={`${regionId}-${row.platform}-${row.url}`}>
                      <td className="py-3 pr-3 align-top font-medium text-zinc-900">
                        <a
                          href={row.url}
                          target="_blank"
                          rel="noopener noreferrer"
                          className="text-sky-800 underline-offset-2 hover:underline"
                        >
                          {row.platform}
                        </a>
                        <span className="mt-1 block text-xs font-normal text-zinc-500">
                          {row.url.replace(/^https?:\/\//, "")}
                        </span>
                      </td>
                      <td className="py-3 pr-3 align-top text-zinc-700">
                        {row.type}
                      </td>
                      <td className="py-3 pr-3 align-top tabular-nums text-zinc-800">
                        {row.priceRangeEur}
                      </td>
                      <td className="py-3 pr-3 align-top text-zinc-700">
                        {row.b2bAccess}
                      </td>
                      <td className="py-3 align-top">
                        <span
                          className={`inline-flex rounded-full px-2.5 py-0.5 text-xs font-semibold ${confidenceClass(row.confidence)}`}
                        >
                          {labels.confidence[row.confidence]}
                        </span>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </section>
        );
      })}
      <p className="text-xs leading-relaxed text-zinc-500">{labels.footnote}</p>
    </div>
  );
}
