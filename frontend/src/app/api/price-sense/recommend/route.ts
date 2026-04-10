import { NextResponse } from "next/server";
import { staffFetchHeaders } from "@/lib/staff-headers";

/** Proxies POST to Spring so `EBF_STAFF_API_TOKEN` stays server-side. */
export async function POST(req: Request) {
  const base = process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080";
  let body: unknown;
  try {
    body = await req.json();
  } catch {
    return NextResponse.json({ error: "Invalid JSON" }, { status: 400 });
  }
  const res = await fetch(`${base}/api/v1/price-sense/recommend`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      ...staffFetchHeaders(),
    },
    body: JSON.stringify(body),
  });
  const text = await res.text();
  const ct = res.headers.get("Content-Type") ?? "application/json";
  return new NextResponse(text, { status: res.status, headers: { "Content-Type": ct } });
}
