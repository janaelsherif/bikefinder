import { getTranslations } from "next-intl/server";

export async function DisclaimerStrip() {
  const t = await getTranslations("Disclaimer");

  return (
    <div className="mb-6 rounded-lg border border-amber-100 bg-amber-50/80 px-4 py-3 text-sm leading-relaxed text-amber-950">
      {t("body")}
    </div>
  );
}
