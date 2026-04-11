import createNextIntlPlugin from "next-intl/plugin";

const withNextIntl = createNextIntlPlugin("./src/i18n/request.ts");

/**
 * Dev: if you see "Cannot find module './682.js'" / "./vendor-chunks/next-intl.js" or random 404s,
 * stop all `next dev` instances, run `npm run clean`, then `npm run dev` (avoid `--turbo` unless
 * needed — Turbopack ignores the webpack tweaks below).
 *
 * We disable `splitChunks` in dev so HMR does not reference evicted numeric chunks after partial
 * rebuilds (common with next-intl). Production keeps Next defaults (`dev` is false).
 */
/** @type {import('next').NextConfig} */
const nextConfig = {
  /** Keep compiled pages in memory longer to reduce rapid invalidate → missing chunk races. */
  onDemandEntries: {
    maxInactiveAge: 120 * 1000,
    pagesBufferLength: 10,
  },
  // Must stay in sync: do not list next-intl in serverComponentsExternalPackages — Next forbids the
  // same package in transpilePackages + external, and externalizing breaks setRequestLocale in RSC.
  transpilePackages: ["next-intl"],
  experimental: {
    serverComponentsExternalPackages: [
      "intl-messageformat",
      "@formatjs/fast-memoize",
      "@formatjs/intl-localematcher",
    ],
  },
  /**
   * Dev-only: use named chunk ids so HMR does not reference stale numeric chunks (e.g. ./682.js)
   * after partial rebuilds or mixed next dev instances.
   * Mutate in place — replacing `config.optimization` wholesale can drop Next-internal fields and
   * break CSS extraction (symptom: HTML loads but Tailwind/global styles never apply).
   */
  webpack: (config, { dev }) => {
    if (dev && config.optimization) {
      config.optimization.chunkIds = "named";
      config.optimization.moduleIds = "named";
      // Single dev bundle graph → no stale `./682.js` requires from deleted split chunks.
      config.optimization.splitChunks = false;
    }
    return config;
  },
};

export default withNextIntl(nextConfig);
