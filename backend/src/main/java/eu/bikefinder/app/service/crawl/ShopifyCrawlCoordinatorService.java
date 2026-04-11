package eu.bikefinder.app.service.crawl;

import eu.bikefinder.app.config.CrawlProperties;
import eu.bikefinder.app.service.crawl.rebike.RebikeCrawlService;
import eu.bikefinder.app.service.crawl.upway.UpwayCrawlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Runs Rebike DE, Upway DE, then every enabled {@link CrawlProperties.ShopifyTarget} (Hamza / sourcing doc
 * Shopify subset).
 */
@Service
public class ShopifyCrawlCoordinatorService {

    private static final Logger log = LoggerFactory.getLogger(ShopifyCrawlCoordinatorService.class);

    private final CrawlProperties crawlProperties;
    private final RebikeCrawlService rebikeCrawlService;
    private final UpwayCrawlService upwayCrawlService;
    private final ShopifyStorefrontCrawlService shopifyStorefrontCrawlService;

    public ShopifyCrawlCoordinatorService(
            CrawlProperties crawlProperties,
            RebikeCrawlService rebikeCrawlService,
            UpwayCrawlService upwayCrawlService,
            ShopifyStorefrontCrawlService shopifyStorefrontCrawlService) {
        this.crawlProperties = crawlProperties;
        this.rebikeCrawlService = rebikeCrawlService;
        this.upwayCrawlService = upwayCrawlService;
        this.shopifyStorefrontCrawlService = shopifyStorefrontCrawlService;
    }

    /** Scheduled + manual “run all” — same {@code requireMasterSwitch} semantics as single-target crawls. */
    public List<NamedCrawlRun> runAllConfiguredShopify(boolean requireMasterSwitch) {
        List<NamedCrawlRun> out = new ArrayList<>();
        out.add(new NamedCrawlRun("Rebike DE", rebikeCrawlService.crawlRebikeOffers(requireMasterSwitch)));
        out.add(new NamedCrawlRun("Upway DE", upwayCrawlService.crawlUpwayDeOffers(requireMasterSwitch)));
        for (CrawlProperties.ShopifyTarget t : crawlProperties.getShopifyTargets()) {
            if (!t.isEnabled()
                    || t.getSourceId() == null
                    || t.getSourceId().isBlank()
                    || t.getStorefrontBase() == null
                    || t.getStorefrontBase().isBlank()
                    || t.getSeedUrls() == null
                    || t.getSeedUrls().isEmpty()) {
                continue;
            }
            UUID sourceId;
            try {
                sourceId = UUID.fromString(t.getSourceId().trim());
            } catch (IllegalArgumentException e) {
                log.warn("Skipping Shopify target with invalid source-id: {}", t.getLabel());
                continue;
            }
            String label = t.getLabel() != null && !t.getLabel().isBlank() ? t.getLabel() : t.getSourceId();
            CrawlRunResult r =
                    shopifyStorefrontCrawlService.crawl(
                            sourceId,
                            label,
                            t.getStorefrontBase().trim(),
                            t.getSeedUrls(),
                            t.getMaxProductsPerRun(),
                            t.allowedCurrencySet(),
                            requireMasterSwitch);
            out.add(new NamedCrawlRun(label, r));
        }
        return out;
    }

    public record NamedCrawlRun(String label, CrawlRunResult result) {}
}
