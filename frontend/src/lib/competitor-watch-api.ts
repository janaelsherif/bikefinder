import { staffFetchHeaders } from "./staff-headers";

export type CompetitorWatchTarget = {
  id: string;
  slug: string;
  displayName: string;
  watchUrl: string;
  active: boolean;
};

export type CompetitorWatchSnapshot = {
  id: string;
  targetSlug: string;
  capturedAt: string;
  httpStatus: number | null;
  listingCountEstimate: number | null;
  deltaVsPrevious: number | null;
  errorMessage: string | null;
  durationMs: number | null;
};

export type CompetitorWatchDashboardRow = {
  target: CompetitorWatchTarget;
  latestSnapshot: CompetitorWatchSnapshot | null;
};

function apiBase(): string {
  return process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080";
}

export async function fetchCompetitorWatchDashboard(): Promise<
  CompetitorWatchDashboardRow[] | null
> {
  const url = `${apiBase()}/api/v1/competitor-watch/dashboard`;
  try {
    const res = await fetch(url, {
      cache: "no-store",
      headers: staffFetchHeaders(),
    });
    if (!res.ok) {
      return null;
    }
    return res.json();
  } catch {
    return null;
  }
}

export async function fetchCompetitorWatchHistory(
  slug: string,
): Promise<CompetitorWatchSnapshot[] | null> {
  const enc = encodeURIComponent(slug);
  const url = `${apiBase()}/api/v1/competitor-watch/history/${enc}`;
  try {
    const res = await fetch(url, {
      cache: "no-store",
      headers: staffFetchHeaders(),
    });
    if (!res.ok) {
      return null;
    }
    return res.json();
  } catch {
    return null;
  }
}

/** Dashboard plus recent history rows per target (for the Module 5 page). */
export async function fetchCompetitorWatchPageData(historyLimit = 12): Promise<{
  dashboard: CompetitorWatchDashboardRow[] | null;
  histories: Record<string, CompetitorWatchSnapshot[]>;
}> {
  const dashboard = await fetchCompetitorWatchDashboard();
  const histories: Record<string, CompetitorWatchSnapshot[]> = {};
  if (!dashboard) {
    return { dashboard: null, histories };
  }
  await Promise.all(
    dashboard.map(async (row) => {
      const slug = row.target.slug;
      const h = await fetchCompetitorWatchHistory(slug);
      histories[slug] = h?.slice(0, historyLimit) ?? [];
    }),
  );
  return { dashboard, histories };
}
