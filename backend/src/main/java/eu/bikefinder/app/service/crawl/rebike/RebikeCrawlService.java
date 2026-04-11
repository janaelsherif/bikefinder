package eu.bikefinder.app.service.crawl.rebike;

import eu.bikefinder.app.config.CrawlProperties;
import eu.bikefinder.app.service.crawl.CrawlRunResult;
import eu.bikefinder.app.service.crawl.ShopifyStorefrontCrawlService;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

/**
 * HTML listing crawl for Rebike (Shopify): delegates to {@link ShopifyStorefrontCrawlService}.
 */
@Service
public class RebikeCrawlService {

    /** Matches {@code V3__dev_seed_data.sql} Rebike row. */
    public static final UUID REBIKE_SOURCE_ID = UUID.fromString("a0000001-0000-0000-0000-000000000001");

    private final CrawlProperties crawlProperties;
    private final ShopifyStorefrontCrawlService shopifyStorefrontCrawlService;

    public RebikeCrawlService(
            CrawlProperties crawlProperties, ShopifyStorefrontCrawlService shopifyStorefrontCrawlService) {
        this.crawlProperties = crawlProperties;
        this.shopifyStorefrontCrawlService = shopifyStorefrontCrawlService;
    }

    /**
     * @param requireMasterSwitch when true (scheduled job), {@code ebf.crawl.enabled} must be true. Manual API calls
     *     pass false so ops can run a crawl without enabling the cron flag.
     */
    public CrawlRunResult crawlRebikeOffers(boolean requireMasterSwitch) {
        return shopifyStorefrontCrawlService.crawl(
                REBIKE_SOURCE_ID,
                "Rebike",
                crawlProperties.getRebike().getStorefrontBase(),
                crawlProperties.getRebike().getSeedUrls(),
                crawlProperties.getRebike().getMaxProductsPerRun(),
                Set.of("EUR"),
                requireMasterSwitch);
    }
}
