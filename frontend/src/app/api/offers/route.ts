import { NextResponse } from "next/server";
import { staffFetchHeaders } from "@/lib/staff-headers";

function apiBase(): string {
  return process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080";
}

export async function GET(req: Request) {
  const url = new URL(req.url);
  const qs = url.searchParams.toString();
  const target = `${apiBase()}/api/v1/offers${qs ? `?${qs}` : ""}`;
  const res = await fetch(target, {
    cache: "no-store",
    headers: staffFetchHeaders(),
  });
  const text = await res.text();
  const contentType = res.headers.get("Content-Type") ?? "application/json";
  return new NextResponse(text, {
    status: res.status,
    headers: { "Content-Type": contentType },
  });
}
