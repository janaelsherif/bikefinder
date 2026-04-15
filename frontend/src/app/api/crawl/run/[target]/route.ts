import { NextResponse } from "next/server";
import { systemImportHeaders } from "@/lib/system-import-headers";

type CrawlTarget = "rebike" | "upway-de" | "shopify-all" | "marketplace-all";

function apiBase(): string {
  return process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080";
}

function toBackendPath(target: CrawlTarget): string {
  switch (target) {
    case "rebike":
      return "/api/v1/system/crawl/rebike";
    case "upway-de":
      return "/api/v1/system/crawl/upway-de";
    case "shopify-all":
      return "/api/v1/system/crawl/shopify-all";
    case "marketplace-all":
      return "/api/v1/system/crawl/marketplace-all";
    default: {
      const _exhaustive: never = target;
      return _exhaustive;
    }
  }
}

export async function POST(
  _req: Request,
  { params }: { params: { target: string } },
) {
  const target = params.target as CrawlTarget;
  if (
    target !== "rebike" &&
    target !== "upway-de" &&
    target !== "shopify-all" &&
    target !== "marketplace-all"
  ) {
    return NextResponse.json({ error: "Unknown crawl target" }, { status: 404 });
  }
  const res = await fetch(`${apiBase()}${toBackendPath(target)}`, {
    method: "POST",
    headers: systemImportHeaders(),
  });
  const text = await res.text();
  const contentType = res.headers.get("Content-Type") ?? "application/json";
  return new NextResponse(text, {
    status: res.status,
    headers: { "Content-Type": contentType },
  });
}
