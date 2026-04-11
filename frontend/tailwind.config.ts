import type { Config } from "tailwindcss";

const config: Config = {
  content: [
    "./src/pages/**/*.{js,ts,jsx,tsx,mdx}",
    "./src/components/**/*.{js,ts,jsx,tsx,mdx}",
    "./src/app/**/*.{js,ts,jsx,tsx,mdx}",
  ],
  theme: {
    extend: {
      colors: {
        background: "var(--background)",
        foreground: "var(--foreground)",
      },
      fontFamily: {
        sans: ["var(--font-sans)", "ui-sans-serif", "system-ui", "sans-serif"],
      },
      boxShadow: {
        card: "0 1px 2px rgb(0 0 0 / 0.04), 0 4px 12px rgb(0 0 0 / 0.06)",
        "card-hover":
          "0 4px 6px rgb(0 0 0 / 0.05), 0 12px 24px rgb(0 0 0 / 0.08)",
      },
      backgroundImage: {
        "hero-mesh":
          "radial-gradient(ellipse 80% 60% at 50% -30%, rgb(228 228 231 / 0.9), transparent), radial-gradient(ellipse 60% 50% at 100% 0%, rgb(212 212 216 / 0.35), transparent)",
      },
    },
  },
  plugins: [],
};
export default config;
