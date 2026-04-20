import { NextResponse } from "next/server";
import { systemImportHeaders } from "@/lib/system-import-headers";

function apiBase(): string {
  return process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080";
}

export async function GET() {
  const res = await fetch(`${apiBase()}/api/v1/system/crawl/settings`, {
    cache: "no-store",
    headers: systemImportHeaders(),
  });
  const text = await res.text();
  const contentType = res.headers.get("Content-Type") ?? "application/json";
  return new NextResponse(text, {
    status: res.status,
    headers: { "Content-Type": contentType },
  });
}

export async function PATCH(req: Request) {
  let body: unknown = {};
  try {
    const raw = await req.text();
    if (raw) {
      body = JSON.parse(raw);
    }
  } catch {
    return NextResponse.json({ error: "Invalid JSON" }, { status: 400 });
  }
  const res = await fetch(`${apiBase()}/api/v1/system/crawl/settings`, {
    method: "PATCH",
    headers: {
      "Content-Type": "application/json",
      ...systemImportHeaders(),
    },
    body: JSON.stringify(body),
  });
  const text = await res.text();
  const contentType = res.headers.get("Content-Type") ?? "application/json";
  return new NextResponse(text, {
    status: res.status,
    headers: { "Content-Type": contentType },
  });
}
