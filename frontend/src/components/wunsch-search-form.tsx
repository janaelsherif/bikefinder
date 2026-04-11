import { getTranslations } from "next-intl/server";
import { Link } from "@/i18n/navigation";
import { localePath } from "@/lib/locale-path";

function val(
  searchParams: Record<string, string | string[] | undefined>,
  key: string,
): string {
  const raw = searchParams[key];
  if (Array.isArray(raw)) {
    return raw[0] ?? "";
  }
  return raw ?? "";
}

export async function WunschSearchForm({
  searchParams,
  locale,
}: {
  searchParams: Record<string, string | string[] | undefined>;
  locale: string;
}) {
  const t = await getTranslations("Search");
  const v = (key: string) => val(searchParams, key);
  const formAction = localePath(locale, "/suche");

  return (
    <form
      method="get"
      action={formAction}
      className="rounded-2xl border border-zinc-200/90 bg-white/95 p-5 shadow-card backdrop-blur-sm sm:p-8"
    >
      <h2 className="mb-2 text-xl font-semibold tracking-tight text-zinc-900 sm:text-2xl">
        {t("title")}
      </h2>
      <p className="mb-8 max-w-3xl text-sm leading-relaxed text-zinc-600 sm:text-base">
        {t("subtitle")}
      </p>

      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
        <label className="flex flex-col gap-1">
          <span className="text-sm font-medium text-zinc-800">{t("brand")}</span>
          <input
            name="brand"
            defaultValue={v("brand")}
            placeholder={t("phBrand")}
            className="rounded-lg border border-zinc-300 bg-white px-3 py-2 text-sm shadow-sm transition focus:border-zinc-500 focus:outline-none focus:ring-2 focus:ring-zinc-400/25"
          />
        </label>
        <label className="flex flex-col gap-1">
          <span className="text-sm font-medium text-zinc-800">{t("model")}</span>
          <input
            name="model"
            defaultValue={v("model")}
            placeholder={t("phModel")}
            className="rounded-lg border border-zinc-300 bg-white px-3 py-2 text-sm shadow-sm transition focus:border-zinc-500 focus:outline-none focus:ring-2 focus:ring-zinc-400/25"
          />
        </label>
        <label className="flex flex-col gap-1">
          <span className="text-sm font-medium text-zinc-800">{t("bikeCategory")}</span>
          <select
            name="bikeCategory"
            defaultValue={v("bikeCategory")}
            className="rounded-lg border border-zinc-300 bg-white px-3 py-2 text-sm shadow-sm transition focus:border-zinc-500 focus:outline-none focus:ring-2 focus:ring-zinc-400/25"
          >
            <option value="">{t("any")}</option>
            <option value="city">{t("catCity")}</option>
            <option value="trekking">{t("catTrekking")}</option>
            <option value="cargo">{t("catCargo")}</option>
            <option value="mtb">{t("catMtb")}</option>
            <option value="road">{t("catRoad")}</option>
            <option value="gravel">{t("catGravel")}</option>
            <option value="kids">{t("catKids")}</option>
          </select>
        </label>
        <label className="flex flex-col gap-1">
          <span className="text-sm font-medium text-zinc-800">{t("bikeCondition")}</span>
          <select
            name="bikeCondition"
            defaultValue={v("bikeCondition")}
            className="rounded-lg border border-zinc-300 bg-white px-3 py-2 text-sm shadow-sm transition focus:border-zinc-500 focus:outline-none focus:ring-2 focus:ring-zinc-400/25"
          >
            <option value="">{t("any")}</option>
            <option value="new">{t("condNew")}</option>
            <option value="like_new">{t("condLikeNew")}</option>
            <option value="refurbished">{t("condRefurbished")}</option>
            <option value="used">{t("condUsed")}</option>
          </select>
        </label>
        <label className="flex flex-col gap-1">
          <span className="text-sm font-medium text-zinc-800">{t("motorBrand")}</span>
          <input
            name="motorBrand"
            defaultValue={v("motorBrand")}
            placeholder={t("phMotor")}
            className="rounded-lg border border-zinc-300 bg-white px-3 py-2 text-sm shadow-sm transition focus:border-zinc-500 focus:outline-none focus:ring-2 focus:ring-zinc-400/25"
          />
        </label>
        <label className="flex flex-col gap-1">
          <span className="text-sm font-medium text-zinc-800">{t("motorPosition")}</span>
          <select
            name="motorPosition"
            defaultValue={v("motorPosition")}
            className="rounded-lg border border-zinc-300 bg-white px-3 py-2 text-sm shadow-sm transition focus:border-zinc-500 focus:outline-none focus:ring-2 focus:ring-zinc-400/25"
          >
            <option value="">{t("any")}</option>
            <option value="mid">{t("motorMid")}</option>
            <option value="rear">{t("motorRear")}</option>
            <option value="front">{t("motorFront")}</option>
          </select>
        </label>
        <label className="flex flex-col gap-1">
          <span className="text-sm font-medium text-zinc-800">{t("minBatteryWh")}</span>
          <select
            name="minBatteryWh"
            defaultValue={v("minBatteryWh")}
            className="rounded-lg border border-zinc-300 bg-white px-3 py-2 text-sm shadow-sm transition focus:border-zinc-500 focus:outline-none focus:ring-2 focus:ring-zinc-400/25"
          >
            <option value="">{t("any")}</option>
            <option value="400">400 Wh</option>
            <option value="500">500 Wh</option>
            <option value="625">625 Wh</option>
            <option value="750">750 Wh</option>
          </select>
        </label>
        <label className="flex flex-col gap-1">
          <span className="text-sm font-medium text-zinc-800">{t("maxLandedPriceChf")}</span>
          <select
            name="maxLandedPriceChf"
            defaultValue={v("maxLandedPriceChf")}
            className="rounded-lg border border-zinc-300 bg-white px-3 py-2 text-sm shadow-sm transition focus:border-zinc-500 focus:outline-none focus:ring-2 focus:ring-zinc-400/25"
          >
            <option value="">{t("any")}</option>
            <option value="800">{t("budget800")}</option>
            <option value="1500">{t("budget1500")}</option>
            <option value="3000">{t("budget3000")}</option>
            <option value="5000">{t("budget5000")}</option>
            <option value="8000">{t("budget8000")}</option>
          </select>
        </label>
        <label className="flex flex-col gap-1">
          <span className="text-sm font-medium text-zinc-800">{t("maxMileageKm")}</span>
          <input
            name="maxMileageKm"
            type="number"
            min={0}
            defaultValue={v("maxMileageKm")}
            placeholder={t("phKm")}
            className="rounded-lg border border-zinc-300 bg-white px-3 py-2 text-sm shadow-sm transition focus:border-zinc-500 focus:outline-none focus:ring-2 focus:ring-zinc-400/25"
          />
        </label>
        <label className="flex flex-col gap-1">
          <span className="text-sm font-medium text-zinc-800">{t("countryCode")}</span>
          <select
            name="countryCode"
            defaultValue={v("countryCode")}
            className="rounded-lg border border-zinc-300 bg-white px-3 py-2 text-sm shadow-sm transition focus:border-zinc-500 focus:outline-none focus:ring-2 focus:ring-zinc-400/25"
          >
            <option value="">{t("any")}</option>
            <option value="DE">DE</option>
            <option value="NL">NL</option>
            <option value="FR">FR</option>
            <option value="IT">IT</option>
            <option value="AT">AT</option>
          </select>
        </label>
      </div>

      <div className="mt-4 flex flex-wrap items-center gap-6">
        <label className="flex items-center gap-2 text-sm text-zinc-800">
          <input
            type="checkbox"
            name="warrantyPresent"
            value="true"
            defaultChecked={v("warrantyPresent") === "true"}
          />
          {t("warrantyPresent")}
        </label>
        <label className="flex items-center gap-2 text-sm text-zinc-800">
          <input
            type="checkbox"
            name="bargainOnly"
            value="true"
            defaultChecked={v("bargainOnly") === "true"}
          />
          {t("bargainOnly")}
        </label>
      </div>

      <div className="mt-6 flex flex-wrap gap-3">
        <button
          type="submit"
          className="rounded-lg bg-zinc-900 px-4 py-2 text-sm font-medium text-white hover:bg-zinc-800"
        >
          {t("submit")}
        </button>
        <Link
          href="/suche"
          className="rounded-lg border border-zinc-300 px-4 py-2 text-sm font-medium text-zinc-700 hover:bg-zinc-50"
        >
          {t("reset")}
        </Link>
      </div>
    </form>
  );
}
