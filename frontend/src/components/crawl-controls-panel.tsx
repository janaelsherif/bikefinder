"use client";

import { useEffect, useMemo, useState } from "react";

type CrawlTarget = "marketplace-all" | "shopify-all" | "rebike" | "upway-de";

type CrawlSettings = {
  autoCrawlEnabled: boolean;
  autoCrawlTime: string;
  timezone: string;
  lastAutoRunAt: string | null;
};

type AsyncTaskAccepted = {
  taskId: string;
  taskType: string;
  status: "queued" | "running" | "succeeded" | "failed" | string;
};

type AsyncTaskStatus = {
  taskId: string;
  taskType: string;
  status: "queued" | "running" | "succeeded" | "failed" | string;
  errorMessage: string | null;
};

export type CrawlControlsCopy = {
  title: string;
  subtitle: string;
  runMarketplaceAll: string;
  runShopifyAll: string;
  runRebike: string;
  runUpwayDe: string;
  autoEnabledLabel: string;
  autoTimeLabel: string;
  timezoneLabel: string;
  saveSettings: string;
  loading: string;
  notConfigured: string;
  settingsSaved: string;
  lastRunLabel: string;
  neverRun: string;
  runSuccessPrefix: string;
  runErrorPrefix: string;
  settingsErrorPrefix: string;
};

function formatWhen(iso: string, locale: string): string {
  try {
    const dateLocale = locale === "en" ? "en-GB" : "de-CH";
    return new Intl.DateTimeFormat(dateLocale, {
      dateStyle: "medium",
      timeStyle: "short",
    }).format(new Date(iso));
  } catch {
    return iso;
  }
}

async function parseJsonSafe(res: Response): Promise<unknown> {
  const text = await res.text();
  if (!text) {
    return {};
  }
  try {
    return JSON.parse(text) as unknown;
  } catch {
    return { raw: text };
  }
}

export function CrawlControlsPanel({
  locale,
  labels,
}: {
  locale: string;
  labels: CrawlControlsCopy;
}) {
  const [settings, setSettings] = useState<CrawlSettings | null>(null);
  const [autoEnabled, setAutoEnabled] = useState(false);
  const [autoTime, setAutoTime] = useState("03:00");
  const [isLoadingSettings, setIsLoadingSettings] = useState(true);
  const [isSavingSettings, setIsSavingSettings] = useState(false);
  const [isRunningTarget, setIsRunningTarget] = useState<CrawlTarget | null>(null);
  const [status, setStatus] = useState<string | null>(null);

  const saveDisabled = isSavingSettings || isLoadingSettings;

  async function loadSettings() {
    setIsLoadingSettings(true);
    setStatus(null);
    try {
      const res = await fetch("/api/crawl/settings", { cache: "no-store" });
      const data = await parseJsonSafe(res);
      if (!res.ok) {
        const reason =
          typeof data === "object" && data != null && "error" in data
            ? String((data as { error: unknown }).error)
            : `HTTP ${res.status}`;
        setStatus(`${labels.settingsErrorPrefix} ${reason}`);
        return;
      }
      const next = data as CrawlSettings;
      setSettings(next);
      setAutoEnabled(Boolean(next.autoCrawlEnabled));
      setAutoTime(next.autoCrawlTime ?? "03:00");
    } catch (err) {
      setStatus(`${labels.settingsErrorPrefix} ${String(err)}`);
    } finally {
      setIsLoadingSettings(false);
    }
  }

  useEffect(() => {
    loadSettings();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  async function runCrawl(target: CrawlTarget) {
    setStatus(null);
    setIsRunningTarget(target);
    try {
      const res = await fetch(`/api/crawl/run/${target}`, { method: "POST" });
      const data = await parseJsonSafe(res);
      if (!res.ok) {
        const reason =
          typeof data === "object" && data != null && "error" in data
            ? String((data as { error: unknown }).error)
            : `HTTP ${res.status}`;
        setStatus(`${labels.runErrorPrefix} ${reason}`);
        return;
      }
      const accepted = data as Partial<AsyncTaskAccepted>;
      if (accepted.taskId) {
        setStatus(`${labels.runSuccessPrefix} ${target} (${accepted.taskId})`);
        void pollTaskUntilDone(accepted.taskId, target);
      } else {
        setStatus(`${labels.runSuccessPrefix} ${target}`);
      }
    } catch (err) {
      setStatus(`${labels.runErrorPrefix} ${String(err)}`);
    } finally {
      setIsRunningTarget(null);
      await loadSettings();
    }
  }

  async function pollTaskUntilDone(taskId: string, target: CrawlTarget) {
    for (let attempt = 0; attempt < 30; attempt++) {
      await new Promise((r) => setTimeout(r, 4000));
      try {
        const res = await fetch(`/api/crawl/tasks/${encodeURIComponent(taskId)}`, {
          cache: "no-store",
        });
        if (!res.ok) {
          return;
        }
        const data = (await parseJsonSafe(res)) as Partial<AsyncTaskStatus>;
        if (data.status === "succeeded") {
          setStatus(`${labels.runSuccessPrefix} ${target} (${taskId})`);
          await loadSettings();
          return;
        }
        if (data.status === "failed") {
          setStatus(
            `${labels.runErrorPrefix} ${data.errorMessage ?? `task ${taskId} failed`}`,
          );
          return;
        }
      } catch {
        return;
      }
    }
  }

  async function saveSettings() {
    setStatus(null);
    setIsSavingSettings(true);
    try {
      const res = await fetch("/api/crawl/settings", {
        method: "PATCH",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          autoCrawlEnabled: autoEnabled,
          autoCrawlTime: autoTime,
        }),
      });
      const data = await parseJsonSafe(res);
      if (!res.ok) {
        const reason =
          typeof data === "object" && data != null && "error" in data
            ? String((data as { error: unknown }).error)
            : `HTTP ${res.status}`;
        setStatus(`${labels.settingsErrorPrefix} ${reason}`);
        return;
      }
      const next = data as CrawlSettings;
      setSettings(next);
      setAutoEnabled(Boolean(next.autoCrawlEnabled));
      setAutoTime(next.autoCrawlTime ?? autoTime);
      setStatus(labels.settingsSaved);
    } catch (err) {
      setStatus(`${labels.settingsErrorPrefix} ${String(err)}`);
    } finally {
      setIsSavingSettings(false);
    }
  }

  const actionButtons = useMemo(
    () =>
      [
        { target: "marketplace-all" as const, label: labels.runMarketplaceAll },
        { target: "shopify-all" as const, label: labels.runShopifyAll },
        { target: "rebike" as const, label: labels.runRebike },
        { target: "upway-de" as const, label: labels.runUpwayDe },
      ] satisfies ReadonlyArray<{ target: CrawlTarget; label: string }>,
    [labels],
  );

  return (
    <section className="rounded-2xl border border-zinc-200/90 bg-white p-6 shadow-card sm:p-8">
      <h2 className="text-lg font-semibold text-zinc-900">{labels.title}</h2>
      <p className="mt-2 text-sm text-zinc-600">{labels.subtitle}</p>

      <div className="mt-5 grid gap-3 sm:grid-cols-2">
        {actionButtons.map((btn) => (
          <button
            key={btn.target}
            type="button"
            onClick={() => runCrawl(btn.target)}
            disabled={isRunningTarget != null || isLoadingSettings}
            className="rounded-lg border border-zinc-300 bg-zinc-50 px-4 py-2 text-sm font-medium text-zinc-900 transition hover:bg-zinc-100 disabled:cursor-not-allowed disabled:opacity-60"
          >
            {isRunningTarget === btn.target ? labels.loading : btn.label}
          </button>
        ))}
      </div>

      <div className="mt-6 grid gap-4 rounded-xl border border-zinc-200 bg-zinc-50/70 p-4 sm:grid-cols-2">
        <label className="flex items-center gap-3 text-sm font-medium text-zinc-800">
          <input
            type="checkbox"
            checked={autoEnabled}
            onChange={(e) => setAutoEnabled(e.currentTarget.checked)}
            disabled={saveDisabled}
            className="h-4 w-4 rounded border-zinc-400"
          />
          {labels.autoEnabledLabel}
        </label>
        <label className="flex flex-col gap-1 text-sm font-medium text-zinc-800">
          <span>{labels.autoTimeLabel}</span>
          <input
            type="time"
            value={autoTime}
            onChange={(e) => setAutoTime(e.currentTarget.value)}
            disabled={saveDisabled}
            className="rounded-md border border-zinc-300 bg-white px-3 py-2 text-sm"
          />
        </label>
        <div className="text-sm text-zinc-700">
          <span className="font-medium">{labels.timezoneLabel}: </span>
          {settings?.timezone ?? labels.notConfigured}
        </div>
        <div className="text-sm text-zinc-700">
          <span className="font-medium">{labels.lastRunLabel}: </span>
          {settings?.lastAutoRunAt
            ? formatWhen(settings.lastAutoRunAt, locale)
            : labels.neverRun}
        </div>
      </div>

      <div className="mt-4 flex items-center gap-3">
        <button
          type="button"
          onClick={saveSettings}
          disabled={saveDisabled}
          className="rounded-lg bg-zinc-900 px-4 py-2 text-sm font-semibold text-white transition hover:bg-zinc-800 disabled:cursor-not-allowed disabled:opacity-60"
        >
          {isSavingSettings ? labels.loading : labels.saveSettings}
        </button>
        {isLoadingSettings && (
          <span className="text-xs text-zinc-600">{labels.loading}</span>
        )}
      </div>

      {status && (
        <p className="mt-4 rounded-md border border-zinc-200 bg-white px-3 py-2 text-sm text-zinc-800">
          {status}
        </p>
      )}
    </section>
  );
}
