package eu.bikefinder.app.service.crawl;

/**
 * Outcome of a marketplace HTML crawl run (scheduled or manual).
 */
public record CrawlRunResult(
        boolean skipped,
        boolean ran,
        String status,
        int imported,
        int skippedDuplicates,
        int failed,
        String reason) {

    public static CrawlRunResult skipped(String reason) {
        return new CrawlRunResult(true, false, "skipped", 0, 0, 0, reason);
    }

    public static CrawlRunResult failed(String message) {
        return new CrawlRunResult(false, true, "failed", 0, 0, 0, message);
    }
}
