import { NextResponse } from "next/server";

/** Sets httpOnly cookie after shared password check (optional Hamza-only UI gate). */
export async function POST(req: Request) {
  const expected = process.env.STAFF_UI_PASSWORD?.trim();
  if (!expected) {
    return NextResponse.json({ error: "STAFF_UI_PASSWORD not configured" }, { status: 501 });
  }
  let body: { password?: string };
  try {
    body = await req.json();
  } catch {
    return NextResponse.json({ error: "Invalid JSON" }, { status: 400 });
  }
  const submitted = (body.password ?? "").trim();
  if (submitted !== expected) {
    return NextResponse.json({ error: "Unauthorized" }, { status: 401 });
  }
  const res = NextResponse.json({ ok: true });
  res.cookies.set("ebf_staff", "1", {
    httpOnly: true,
    sameSite: "lax",
    path: "/",
    maxAge: 60 * 60 * 24 * 7,
    secure: process.env.NODE_ENV === "production",
  });
  return res;
}

/** Clears the staff session cookie (httpOnly). */
export async function DELETE() {
  const res = NextResponse.json({ ok: true });
  res.cookies.set("ebf_staff", "", {
    httpOnly: true,
    sameSite: "lax",
    path: "/",
    maxAge: 0,
    secure: process.env.NODE_ENV === "production",
  });
  return res;
}
