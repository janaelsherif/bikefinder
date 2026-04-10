package eu.bikefinder.app.service.competitorwatch;

import org.jsoup.Jsoup;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CompetitorListingEstimatorTest {

    @Test
    void countsShopifyProductPaths() {
        String html =
                "<html><body><a href=\"https://x.com/products/a\">1</a>"
                        + "<a href=\"https://x.com/products/a?variant=1\">dup</a>"
                        + "<a href=\"https://x.com/products/b\">2</a></body></html>";
        var doc = Jsoup.parse(html, "https://x.com/");
        assertThat(CompetitorListingEstimator.estimate(doc)).isEqualTo(2);
    }
}
