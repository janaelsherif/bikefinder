package eu.bikefinder.app.service.competitorwatch;

import eu.bikefinder.app.config.CompetitorWatchProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled competitor snapshot when {@code ebf.competitor-watch.enabled=true} (default: daily 08:00
 * Europe/Zurich). Uses same robots + UA as other crawls.
 */
@Component
public class CompetitorWatchScheduler {

    private static final Logger log = LoggerFactory.getLogger(CompetitorWatchScheduler.class);

    private final CompetitorWatchProperties props;
    private final CompetitorWatchService competitorWatchService;

    public CompetitorWatchScheduler(
            CompetitorWatchProperties props, CompetitorWatchService competitorWatchService) {
        this.props = props;
        this.competitorWatchService = competitorWatchService;
    }

    @Scheduled(cron = "${ebf.competitor-watch.cron}", zone = "${ebf.competitor-watch.zone}")
    public void scheduledRun() {
        if (!props.isEnabled()) {
            return;
        }
        CompetitorWatchService.CompetitorWatchRunResult r = competitorWatchService.runAll(true);
        if (r.skipped()) {
            log.debug("Competitor watch skipped: {}", r.message());
        } else {
            log.info(
                    "Competitor watch ok={} fail={} — {}",
                    r.targetsOk(),
                    r.targetsFailed(),
                    r.message());
        }
    }
}
