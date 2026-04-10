"use client";

import { useEffect } from "react";

type Props = { locale: string };

/** Syncs <html lang> with the active locale (root layout stays generic). */
export function HtmlLang({ locale }: Props) {
  useEffect(() => {
    document.documentElement.lang = locale === "en" ? "en" : "de-CH";
  }, [locale]);

  return null;
}
