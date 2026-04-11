import type { Metadata } from "next";
import { Inter } from "next/font/google";
import "./globals.css";

const inter = Inter({
  subsets: ["latin"],
  variable: "--font-sans",
  display: "swap",
});

export const metadata: Metadata = {
  title: "EuropeBikeFinder",
  description:
    "EU e-bike listings with CHF landed price and savings vs typical Swiss prices.",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="de-CH" suppressHydrationWarning className={inter.variable}>
      <body className={`${inter.className} font-sans`}>
        {children}
      </body>
    </html>
  );
}
