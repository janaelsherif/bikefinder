package eu.bikefinder.app.service.crawl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

/**
 * Scheduled marketplace crawls (Shopify + heuristic HTML) based on persisted crawl settings.
 */
@Component
public class CrawlScheduler {

    private static final Logger log = LoggerFactory.getLogger(CrawlScheduler.class);

    private final CrawlSettingsService crawlSettingsService;
    private final FullMarketplaceCrawlCoordinatorService fullMarketplaceCrawlCoordinatorService;

    public CrawlScheduler(
            CrawlSettingsService crawlSettingsService,
            FullMarketplaceCrawlCoordinatorService fullMarketplaceCrawlCoordinatorService) {
        this.crawlSettingsService = crawlSettingsService;
        this.fullMarketplaceCrawlCoordinatorService = fullMarketplaceCrawlCoordinatorService;
    }

    @Scheduled(cron = "0 * * * * *", zone = "UTC")
    public void scheduledMarketplaceCrawls() {
        var settings = crawlSettingsService.getCurrent();
        ZonedDateTime now = ZonedDateTime.now();
        if (!crawlSettingsService.shouldRunNow(now, settings)) {
            return;
        }
        crawlSettingsService.markAutoRunAt(now.toInstant());
        log.info(
                "Running scheduled crawl batch at {} in timezone {}",
                CrawlSettingsService.formatAutoCrawlTime(settings.getAutoCrawlTime()),
                settings.getTimezone());
        for (var named : fullMarketplaceCrawlCoordinatorService.runEverything(true)) {
            if (named.result().skipped()) {
                log.debug("Scheduled crawl skipped [{}]: {}", named.label(), named.result().reason());
            }
        }
    }
}
