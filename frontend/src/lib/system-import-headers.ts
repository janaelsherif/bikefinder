export function systemImportHeaders(): HeadersInit {
  const token = process.env.EBF_IMPORT_TOKEN?.trim();
  if (!token) {
    return {};
  }
  return { "X-Import-Token": token };
}
