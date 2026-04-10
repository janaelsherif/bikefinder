package eu.bikefinder.app.service.crawl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class RebikeUrlCanonicalizerTest {

    @Test
    void stripsLocalePrefix() {
        assertEquals(
                "https://rebike.com/products/foo-bar-123",
                RebikeUrlCanonicalizer.canonicalProductUrl(
                        "https://rebike.com", "https://rebike.com/de/products/foo-bar-123"));
    }

    @Test
    void resolvesRelativeAgainstStorefront() {
        assertEquals(
                "https://rebike.com/products/slug-1",
                RebikeUrlCanonicalizer.canonicalProductUrl("https://rebike.com", "/products/slug-1"));
    }

    @Test
    void ignoresNonProductLinks() {
        assertNull(RebikeUrlCanonicalizer.canonicalProductUrl("https://rebike.com", "/collections/all"));
    }
}
