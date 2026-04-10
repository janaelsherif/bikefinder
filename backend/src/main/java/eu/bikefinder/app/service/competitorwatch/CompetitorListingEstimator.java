package eu.bikefinder.app.service.competitorwatch;

import org.jsoup.nodes.Document;

import java.util.HashSet;
import java.util.Set;

/**
 * Heuristic listing-surface count from a single HTML page (homepage or category). Not “new listings
 * this week” — that needs stable listing IDs over time. Use week-over-week delta of this signal as a
 * rough activity proxy after robots/terms review.
 */
public final class CompetitorListingEstimator {

    private static final int CAP = 8000;

    private CompetitorListingEstimator() {}

    public static int estimate(Document doc) {
        int shopify = countDistinctShopifyProductPaths(doc);
        if (shopify >= 12) {
            return Math.min(shopify, CAP);
        }
        int generic = countGenericProductSignals(doc);
        return Math.min(Math.max(shopify, generic), CAP);
    }

    static int countDistinctShopifyProductPaths(Document doc) {
        Set<String> keys = new HashSet<>();
        for (var a : doc.select("a[href]")) {
            String href = a.attr("abs:href");
            if (href.isEmpty()) {
                continue;
            }
            int i = href.indexOf("/products/");
            if (i < 0) {
                continue;
            }
            int end = href.length();
            for (int q = i; q < href.length(); q++) {
                char c = href.charAt(q);
                if (c == '?' || c == '#') {
                    end = q;
                    break;
                }
            }
            keys.add(href.substring(0, end));
            if (keys.size() >= CAP) {
                break;
            }
        }
        return keys.size();
    }

    static int countGenericProductSignals(Document doc) {
        int structured =
                doc.select("[itemtype*='Product'], [data-product-id], [data-product], .product-card")
                        .size();
        // Do not match `/products/` here — {@link #countDistinctShopifyProductPaths} covers Shopify.
        int linkish =
                doc.select("a[href*='/p/'], a[href*='/bike/'], a[href*='/occasion/']")
                        .size();
        return Math.max(structured, Math.min(linkish, 2000));
    }
}
