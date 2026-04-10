package eu.bikefinder.app.service.crawl;

import java.net.URI;
import java.util.Locale;

/**
 * www.rebike.de links and HTTP redirects often point at {@code /de/products/...}, which returns
 * 404 for server-side GETs. Canonical product pages live at {@code https://rebike.com/products/...}
 * (no locale prefix).
 */
public final class RebikeUrlCanonicalizer {

    private RebikeUrlCanonicalizer() {}

    /**
     * Returns canonical absolute product URL on the Shopify storefront, or null if not a product path.
     */
    public static String canonicalProductUrl(String storefrontBase, String href) {
        if (href == null || href.isBlank()) {
            return null;
        }
        String trimmed = href.trim();
        if (trimmed.startsWith("//")) {
            trimmed = "https:" + trimmed;
        }
        URI uri;
        try {
            uri = URI.create(trimmed);
        } catch (IllegalArgumentException e) {
            return null;
        }
        String path = uri.getPath();
        if (path == null || path.isEmpty()) {
            return null;
        }
        String normalized = path;
        for (String lp : new String[] {"/de/", "/en/", "/fr/", "/nl/", "/it/"}) {
            String needle = lp + "products/";
            if (normalized.startsWith(needle)) {
                normalized = "/products/" + normalized.substring(needle.length());
                break;
            }
        }
        if (!normalized.startsWith("/products/")) {
            return null;
        }
        int q = normalized.indexOf('?');
        if (q >= 0) {
            normalized = normalized.substring(0, q);
        }
        if (normalized.length() <= "/products/".length()) {
            return null;
        }
        String base = storefrontBase.endsWith("/") ? storefrontBase.substring(0, storefrontBase.length() - 1) : storefrontBase;
        return base + normalized.toLowerCase(Locale.ROOT);
    }
}
