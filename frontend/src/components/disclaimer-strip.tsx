import { getTranslations } from "next-intl/server";

export async function DisclaimerStrip() {
  const t = await getTranslations("Disclaimer");

  return (
    <div className="mb-8 flex gap-3 rounded-xl border border-amber-200/60 bg-amber-50/90 px-4 py-3.5 text-sm leading-relaxed text-amber-950 shadow-sm backdrop-blur-sm">
      <span
        className="mt-0.5 shrink-0 text-amber-600"
        aria-hidden
      >
        <svg
          className="h-5 w-5"
          fill="none"
          viewBox="0 0 24 24"
          stroke="currentColor"
          strokeWidth={1.5}
        >
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            d="M12 9v3.75m9-.75a9 9 0 11-18 0 9 9 0 0118 0zm-9 3.75h.008v.008H12v-.008z"
          />
        </svg>
      </span>
      <p>{t("body")}</p>
    </div>
  );
}
