package eu.bikefinder.app.service.pricesense.live;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PriceSenseLiveProbeServiceTest {

    @Test
    void originFromWatchUrl_stripsPath() {
        assertEquals("https://rebike.ch", PriceSenseLiveProbeService.originFromWatchUrl("https://rebike.ch/de"));
        assertEquals("https://www.veloplus.ch", PriceSenseLiveProbeService.originFromWatchUrl("https://www.veloplus.ch/"));
    }

    @Test
    void firstProductUrl_findsShopifyProductLink() {
        String html =
                "<html><body><a href=\"/products/foo-bar\">x</a></body></html>";
        String base = "https://shop.example";
        assertEquals(
                "https://shop.example/products/foo-bar",
                PriceSenseLiveProbeService.firstProductUrl(html, base));
    }
}
