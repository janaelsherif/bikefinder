"use client";

/**
 * Root-level error UI when the root layout fails. Must define html/body (Next.js requirement).
 */
export default function GlobalError({
  reset,
}: {
  error: Error & { digest?: string };
  reset: () => void;
}) {
  return (
    <html lang="en">
      <body className="font-sans antialiased">
        <div className="mx-auto max-w-lg px-4 py-16 text-center">
          <h2 className="text-lg font-semibold text-zinc-900">Something went wrong</h2>
          <button
            type="button"
            onClick={() => reset()}
            className="mt-6 rounded-lg bg-zinc-900 px-4 py-2 text-sm font-medium text-white"
          >
            Try again
          </button>
        </div>
      </body>
    </html>
  );
}
