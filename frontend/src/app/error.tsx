"use client";

/**
 * Segment error boundary (Next.js App Router). Prevents dev "missing required error components" when
 * a child route throws; pair with {@link global-error.tsx}.
 */
export default function Error({
  reset,
}: {
  error: Error & { digest?: string };
  reset: () => void;
}) {
  return (
    <div className="mx-auto max-w-lg px-4 py-16 text-center">
      <h2 className="text-lg font-semibold text-zinc-900">Something went wrong</h2>
      <p className="mt-2 text-sm text-zinc-600">
        Try again, or run <code className="rounded bg-zinc-100 px-1">npm run clean</code> then{" "}
        <code className="rounded bg-zinc-100 px-1">npm run dev</code> if this persists.
      </p>
      <button
        type="button"
        onClick={() => reset()}
        className="mt-6 rounded-lg bg-zinc-900 px-4 py-2 text-sm font-medium text-white"
      >
        Try again
      </button>
    </div>
  );
}
