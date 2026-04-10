import createNextIntlPlugin from "next-intl/plugin";

const withNextIntl = createNextIntlPlugin("./src/i18n/request.ts");

/** @type {import('next').NextConfig} */
const nextConfig = {
  // Avoid broken ./vendor-chunks/@formatjs.js resolution in dev (next-intl → use-intl → intl-messageformat).
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
    }
    return config;
  },
};

export default withNextIntl(nextConfig);
