package eu.bikefinder.app.service.crawl.heuristic;

import eu.bikefinder.app.config.CrawlProperties;
import eu.bikefinder.app.service.crawl.CrawlRunResult;
import org.springframework.stereotype.Service;

/**
 * Ricardo, Tutti, Kleinanzeigen (and similar) return Cloudflare / anti-bot challenges to server-side GET — no
 * stable listing HTML for our fetcher. Expose explicit skipped runs until an API or browser automation exists.
 */
@Service
public class ClassifiedsBlockedCrawlService {

    private final CrawlProperties crawlProperties;

    public ClassifiedsBlockedCrawlService(CrawlProperties crawlProperties) {
        this.crawlProperties = crawlProperties;
    }

    public CrawlRunResult skipRicardo(boolean requireMasterSwitch) {
        return skip(
                requireMasterSwitch,
                "Ricardo CH",
                "listing HTML not available to server fetch (challenge page); needs API or browser automation");
    }

    public CrawlRunResult skipTutti(boolean requireMasterSwitch) {
        return skip(
                requireMasterSwitch,
                "Tutti CH",
                "listing HTML not available to server fetch (challenge page); needs API or browser automation");
    }

    public CrawlRunResult skipKleinanzeigen(boolean requireMasterSwitch) {
        return skip(
                requireMasterSwitch,
                "Kleinanzeigen DE",
                "listing HTML not available to server fetch (anti-bot); needs API or browser automation");
    }

    private CrawlRunResult skip(boolean requireMasterSwitch, String label, String reason) {
        if (requireMasterSwitch && !crawlProperties.isEnabled()) {
            return CrawlRunResult.skipped("ebf.crawl.enabled=false");
        }
        return CrawlRunResult.skipped(label + ": " + reason);
    }
}
