package eu.bikefinder.app.service.crawl;

import eu.bikefinder.app.service.crawl.FullMarketplaceCrawlCoordinatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled marketplace crawls (Shopify + heuristic HTML) when {@code ebf.crawl.enabled=true}.
 */
@Component
public class CrawlScheduler {

    private static final Logger log = LoggerFactory.getLogger(CrawlScheduler.class);

    private final FullMarketplaceCrawlCoordinatorService fullMarketplaceCrawlCoordinatorService;

    public CrawlScheduler(FullMarketplaceCrawlCoordinatorService fullMarketplaceCrawlCoordinatorService) {
        this.fullMarketplaceCrawlCoordinatorService = fullMarketplaceCrawlCoordinatorService;
    }

    @Scheduled(cron = "${ebf.crawl.cron}", zone = "Europe/Zurich")
    public void scheduledMarketplaceCrawls() {
        for (var named : fullMarketplaceCrawlCoordinatorService.runEverything(true)) {
            if (named.result().skipped()) {
                log.debug("Scheduled crawl skipped [{}]: {}", named.label(), named.result().reason());
            }
        }
    }
}
