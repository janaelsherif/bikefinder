import { NextResponse } from "next/server";
import { staffFetchHeaders } from "@/lib/staff-headers";

/** Proxies GET /api/v1/offers so the browser can poll without exposing EBF_STAFF_API_TOKEN. */
export async function GET(req: Request) {
  const base = process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080";
  const url = new URL(req.url);
  const qs = url.searchParams.toString();
  const target = `${base}/api/v1/offers${qs ? `?${qs}` : ""}`;
  const res = await fetch(target, {
    cache: "no-store",
    headers: staffFetchHeaders(),
  });
  const text = await res.text();
  const ct = res.headers.get("Content-Type") ?? "application/json";
  return new NextResponse(text, {
    status: res.status,
    headers: { "Content-Type": ct },
  });
}
