import { Suspense } from "react";
import { StaffLoginForm } from "./staff-login-form";

export default function StaffLoginPage() {
  return (
    <Suspense fallback={<div className="mx-auto max-w-md px-4 py-16 text-sm text-zinc-600">…</div>}>
      <StaffLoginForm />
    </Suspense>
  );
}
