package eu.bikefinder.app.service.crawl;

import eu.bikefinder.app.service.crawl.rebike.RebikeCrawlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled Rebike crawl when {@code ebf.crawl.enabled=true}. Respects robots.txt inside {@link RobotsAllowService}.
 */
@Component
public class CrawlScheduler {

    private static final Logger log = LoggerFactory.getLogger(CrawlScheduler.class);

    private final RebikeCrawlService rebikeCrawlService;

    public CrawlScheduler(RebikeCrawlService rebikeCrawlService) {
        this.rebikeCrawlService = rebikeCrawlService;
    }

    @Scheduled(cron = "${ebf.crawl.cron}", zone = "Europe/Zurich")
    public void scheduledRebikeCrawl() {
        var result = rebikeCrawlService.crawlRebikeOffers(true);
        if (result.skipped()) {
            log.debug("Scheduled crawl skipped: {}", result.reason());
        }
    }
}
