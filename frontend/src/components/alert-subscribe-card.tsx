"use client";

import { useSearchParams } from "next/navigation";
import { useState } from "react";

const API_BASE =
  typeof window !== "undefined"
    ? process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080"
    : "";

export type AlertSubscribeLabels = {
  title: string;
  hint: string;
  email: string;
  emailPlaceholder: string;
  submit: string;
  success: string;
  error: string;
};

export function AlertSubscribeCard({
  labels,
  locale,
}: {
  labels: AlertSubscribeLabels;
  locale: string;
}) {
  const searchParams = useSearchParams();
  const [email, setEmail] = useState("");
  const [status, setStatus] = useState<"idle" | "ok" | "err">("idle");

  async function submit(e: React.FormEvent) {
    e.preventDefault();
    setStatus("idle");
    const filter: Record<string, string> = {};
    searchParams.forEach((v, k) => {
      if (v) {
        filter[k] = v;
      }
    });
    try {
      const res = await fetch(`${API_BASE}/api/v1/alert-subscriptions`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, filter, locale }),
      });
      if (res.ok) {
        setStatus("ok");
        setEmail("");
      } else {
        setStatus("err");
      }
    } catch {
      setStatus("err");
    }
  }

  return (
    <div className="mt-8 rounded-xl border border-zinc-200 bg-zinc-50 p-4 sm:p-5">
      <h3 className="text-sm font-semibold text-zinc-900">{labels.title}</h3>
      <p className="mt-1 text-sm text-zinc-600">{labels.hint}</p>
      <form
        onSubmit={submit}
        className="mt-3 flex flex-col gap-2 sm:flex-row sm:items-end"
      >
        <label className="flex flex-1 flex-col gap-1">
          <span className="text-xs font-medium text-zinc-700">{labels.email}</span>
          <input
            type="email"
            required
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            className="rounded-lg border border-zinc-300 px-3 py-2 text-sm"
            placeholder={labels.emailPlaceholder}
          />
        </label>
        <button
          type="submit"
          className="rounded-lg bg-zinc-900 px-4 py-2 text-sm font-medium text-white hover:bg-zinc-800"
        >
          {labels.submit}
        </button>
      </form>
      {status === "ok" && (
        <p className="mt-2 text-sm text-emerald-700">{labels.success}</p>
      )}
      {status === "err" && (
        <p className="mt-2 text-sm text-red-700">{labels.error}</p>
      )}
    </div>
  );
}
