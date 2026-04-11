package eu.bikefinder.app.service.crawl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ShopifyUrlCanonicalizerTest {

    @Test
    void stripsLocalePrefix() {
        assertEquals(
                "https://rebike.com/products/foo-bar-123",
                ShopifyUrlCanonicalizer.canonicalProductUrl(
                        "https://rebike.com", "https://rebike.com/de/products/foo-bar-123"));
    }

    @Test
    void resolvesRelativeAgainstStorefront() {
        assertEquals(
                "https://rebike.com/products/slug-1",
                ShopifyUrlCanonicalizer.canonicalProductUrl("https://rebike.com", "/products/slug-1"));
    }

    @Test
    void ignoresNonProductLinks() {
        assertNull(ShopifyUrlCanonicalizer.canonicalProductUrl("https://rebike.com", "/collections/all"));
    }

    @Test
    void rewritesAlternateShopifyHostToConfiguredStorefront() {
        assertEquals(
                "https://www.upway.de/products/orbea-gain-d40",
                ShopifyUrlCanonicalizer.canonicalProductUrl(
                        "https://www.upway.de",
                        "https://upway-germany.myshopify.com/products/orbea-gain-d40"));
    }
}
