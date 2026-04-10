package eu.bikefinder.app.service.crawl.jsonld;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonLdProductExtractorTest {

    @Test
    void extractsShopifyProductBlock() {
        String html =
                """
                <!DOCTYPE html><html><head>
                <meta property="og:title" content="Test Bike" />
                </head><body>
                <script type="application/ld+json">
                {"@context":"http://schema.org/","@type":"Product","brand":{"@type":"Brand","name":"Moustache Bikes"},
                "name":"Moustache Bikes Game 7 Fully E-Bike 2024","mpn":"290689","sku":"290689",
                "description":"Motor: Bosch (250 W)\\nBattery: 750 Wh\\nOdometer: 597 km",
                "category":"Elektrofahrräder",
                "offers":{"@type":"Offer","price":"4439.00","priceCurrency":"EUR"}}
                </script>
                </body></html>
                """;
        var p = JsonLdProductExtractor.extract(html).orElseThrow();
        assertEquals("Moustache Bikes", p.brand());
        assertEquals(new BigDecimal("4439.00"), p.priceEur());
        assertEquals("EUR", p.currencyCode());
        assertEquals(750, p.batteryWh());
        assertEquals(597, p.mileageKm());
        assertEquals("Bosch", p.motorBrand());
        assertEquals("290689", p.mpn());
        assertEquals("mtb", p.bikeCategory());
    }

    @Test
    void rejects404PageByOgTitle() {
        String html =
                """
                <html><head>
                <meta property="og:title" content="404 Nicht gefunden" />
                </head><body>
                <script type="application/ld+json">{"@type":"Organization","name":"X"}</script>
                </body></html>
                """;
        assertTrue(JsonLdProductExtractor.extract(html).isEmpty());
    }
}
