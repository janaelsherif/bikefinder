import { NextResponse } from "next/server";
import { staffFetchHeaders } from "@/lib/staff-headers";

/** Proxies POST to Spring; keeps staff token and LLM keys server-side. */
export async function POST(req: Request) {
  const base = process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080";
  let body: unknown = {};
  try {
    const t = await req.text();
    if (t) {
      body = JSON.parse(t);
    }
  } catch {
    return NextResponse.json({ error: "Invalid JSON" }, { status: 400 });
  }
  const res = await fetch(`${base}/api/v1/competitor-watch/brief`, {
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
