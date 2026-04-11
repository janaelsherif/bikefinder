package eu.bikefinder.app.service.crawl;

import eu.bikefinder.app.service.crawl.jsonld.JsonLdProductExtractor.ParsedProduct;

import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/**
 * Shared JSON-LD → {@link CrawlOfferPersistence#importIfAbsent} path for Shopify and heuristic crawlers.
 */
public final class MarketplaceProductImporter {

    private MarketplaceProductImporter() {}

    public enum Outcome {
        INSERTED,
        DUPLICATE,
        SKIP_BAD
    }

    public static Outcome tryImportJsonLd(
            UUID sourceId,
            String productUrl,
            ParsedProduct p,
            Set<String> allowedCurrencies,
            CrawlOfferPersistence crawlOfferPersistence) {
        if (p.priceEur() == null || p.priceEur().signum() <= 0) {
            return Outcome.SKIP_BAD;
        }
        String offerCurrency =
                p.currencyCode() != null && !p.currencyCode().isBlank()
                        ? p.currencyCode().trim().toUpperCase(Locale.ROOT)
                        : "EUR";
        if (!allowedCurrencies.contains(offerCurrency)) {
            return Outcome.SKIP_BAD;
        }
        String sourceOfferId = truncateOfferId(resolveSourceOfferId(productUrl, p), 200);
        String brand = p.brand() != null ? p.brand() : guessBrandFromTitle(p.name());
        String model = p.model() != null ? p.model() : (p.name() != null ? p.name() : "Unknown");
        String[] images = p.imageUrl() != null ? new String[] {p.imageUrl()} : new String[0];
        boolean inserted =
                crawlOfferPersistence.importIfAbsent(
                        sourceId,
                        sourceOfferId,
                        productUrl,
                        brand,
                        model,
                        p.modelYear(),
                        p.bikeCategory(),
                        "refurbished",
                        p.motorBrand(),
                        p.batteryWh(),
                        p.mileageKm(),
                        p.priceEur(),
                        offerCurrency,
                        images);
        return inserted ? Outcome.INSERTED : Outcome.DUPLICATE;
    }

    private static String resolveSourceOfferId(String productUrl, ParsedProduct p) {
        if (p.mpn() != null && !p.mpn().isBlank()) {
            return p.mpn().trim();
        }
        if (p.sku() != null && !p.sku().isBlank()) {
            return p.sku().trim();
        }
        try {
            java.net.URI u = java.net.URI.create(productUrl);
            String path = u.getPath();
            int slash = path.lastIndexOf('/');
            return slash >= 0 ? path.substring(slash + 1) : path;
        } catch (Exception e) {
            return "unknown";
        }
    }

    private static String guessBrandFromTitle(String name) {
        if (name == null || name.isBlank()) {
            return "Unknown";
        }
        String[] parts = name.trim().split("\\s+");
        return parts.length > 0 ? parts[0] : "Unknown";
    }

    private static String truncateOfferId(String id, int maxLen) {
        if (id == null) {
            return "unknown";
        }
        return id.length() <= maxLen ? id : id.substring(0, maxLen);
    }
}
