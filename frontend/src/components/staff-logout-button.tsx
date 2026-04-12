"use client";

import { useRouter } from "@/i18n/navigation";
import { useState } from "react";

type Props = {
  label: string;
  className?: string;
};

export function StaffLogoutButton({ label, className }: Props) {
  const router = useRouter();
  const [pending, setPending] = useState(false);

  async function logout() {
    setPending(true);
    try {
      await fetch("/api/staff-session", { method: "DELETE" });
      router.refresh();
      router.replace("/staff-login");
    } finally {
      setPending(false);
    }
  }

  return (
    <button
      type="button"
      disabled={pending}
      onClick={() => void logout()}
      className={`cursor-pointer border-0 bg-transparent p-0 font-inherit ${className ?? ""}`}
    >
      {label}
    </button>
  );
}
