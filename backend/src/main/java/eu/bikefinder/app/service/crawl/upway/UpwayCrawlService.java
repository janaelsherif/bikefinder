package eu.bikefinder.app.service.crawl.upway;

import eu.bikefinder.app.config.CrawlProperties;
import eu.bikefinder.app.service.crawl.CrawlRunResult;
import eu.bikefinder.app.service.crawl.ShopifyStorefrontCrawlService;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

/**
 * HTML listing crawl for Upway Germany (Shopify at {@code www.upway.de}).
 */
@Service
public class UpwayCrawlService {

    /** Matches {@code V16__upway_de_source.sql}. */
    public static final UUID UPWAY_DE_SOURCE_ID = UUID.fromString("a0000004-0000-0000-0000-000000000006");

    private final CrawlProperties crawlProperties;
    private final ShopifyStorefrontCrawlService shopifyStorefrontCrawlService;

    public UpwayCrawlService(
            CrawlProperties crawlProperties, ShopifyStorefrontCrawlService shopifyStorefrontCrawlService) {
        this.crawlProperties = crawlProperties;
        this.shopifyStorefrontCrawlService = shopifyStorefrontCrawlService;
    }

    /**
     * @param requireMasterSwitch when true (scheduled job), {@code ebf.crawl.enabled} must be true. Manual API calls
     *     pass false so ops can run a crawl without enabling the cron flag.
     */
    public CrawlRunResult crawlUpwayDeOffers(boolean requireMasterSwitch) {
        return shopifyStorefrontCrawlService.crawl(
                UPWAY_DE_SOURCE_ID,
                "Upway DE",
                crawlProperties.getUpwayDe().getStorefrontBase(),
                crawlProperties.getUpwayDe().getSeedUrls(),
                crawlProperties.getUpwayDe().getMaxProductsPerRun(),
                Set.of("EUR"),
                requireMasterSwitch);
    }
}
