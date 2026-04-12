import createNextIntlPlugin from "next-intl/plugin";

const withNextIntl = createNextIntlPlugin("./src/i18n/request.ts");

/**
 * Dev: run **`npm run dev` once**, then use browser refresh only. If you see `./948.js` / missing chunks,
 * stop the server and run **`npm run dev:fresh`** (clears `.next`). Avoid `next dev --turbo` with next-intl.
 * Server-only `splitChunks: false` in dev; client keeps default chunks (Tailwind/CSS).
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
  webpack: (config, { dev, isServer }) => {
    if (dev && config.optimization) {
      config.optimization.chunkIds = "named";
      config.optimization.moduleIds = "named";
      if (isServer) {
        config.optimization.splitChunks = false;
      }
    }
    return config;
  },
};

export default withNextIntl(nextConfig);
