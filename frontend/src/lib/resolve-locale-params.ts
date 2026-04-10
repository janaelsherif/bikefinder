/** Next 15 uses Promise<{ locale }>; Next 14 uses a plain object. */
export async function resolveLocaleParams(
  params: { locale: string } | Promise<{ locale: string }>,
): Promise<{ locale: string }> {
  return params instanceof Promise ? await params : params;
}
