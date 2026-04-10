/** Server-side headers for Spring optional staff token (Hamza / procurement). */
export function staffFetchHeaders(): HeadersInit {
  const t = process.env.EBF_STAFF_API_TOKEN ?? process.env.STAFF_API_TOKEN;
  if (!t) {
    return {};
  }
  return { "X-Staff-Token": t };
}
