import { NextResponse } from "next/server";
import { systemImportHeaders } from "@/lib/system-import-headers";

function apiBase(): string {
  return process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080";
}

export async function GET(
  _req: Request,
  { params }: { params: { taskId: string } },
) {
  const taskId = encodeURIComponent(params.taskId);
  const res = await fetch(`${apiBase()}/api/v1/system/crawl/tasks/${taskId}`, {
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
