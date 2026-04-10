package eu.bikefinder.app.web;

import eu.bikefinder.app.config.AdminProperties;
import eu.bikefinder.app.service.OfferImportService;
import eu.bikefinder.app.service.competitorwatch.CompetitorWatchService;
import eu.bikefinder.app.service.crawl.rebike.RebikeCrawlService;
import eu.bikefinder.app.service.crawl.rebike.RebikeCrawlService.CrawlRunResult;
import eu.bikefinder.app.web.dto.OfferImportBatchRequest;
import eu.bikefinder.app.web.dto.OfferImportResultDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Dev / ops: bulk insert {@code bike_offer} rows and reprice; optional Rebike crawl trigger. Disabled unless
 * {@code ebf.admin.import-token} is set or {@code ebf.admin.dev-open-system-endpoints=true} (localhost dev only).
 */
@RestController
@RequestMapping("/api/v1/system")
public class SystemImportController {

    private final AdminProperties adminProperties;
    private final OfferImportService offerImportService;
    private final RebikeCrawlService rebikeCrawlService;
    private final CompetitorWatchService competitorWatchService;

    public SystemImportController(
            AdminProperties adminProperties,
            OfferImportService offerImportService,
            RebikeCrawlService rebikeCrawlService,
            CompetitorWatchService competitorWatchService) {
        this.adminProperties = adminProperties;
        this.offerImportService = offerImportService;
        this.rebikeCrawlService = rebikeCrawlService;
        this.competitorWatchService = competitorWatchService;
    }

    @PostMapping("/import-offers")
    public ResponseEntity<OfferImportResultDto> importOffers(
            @RequestHeader(value = "X-Import-Token", required = false) String token,
            @Valid @RequestBody OfferImportBatchRequest body) {
        if (!adminProperties.isSystemEndpointsAvailable()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        if (!adminProperties.isTokenValid(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(offerImportService.importBatch(body));
    }

    /** Manual Rebike crawl (same token as import). Ignores {@code ebf.crawl.enabled} so ops can test without enabling cron. */
    @PostMapping("/crawl/rebike")
    public ResponseEntity<CrawlRunResult> crawlRebike(
            @RequestHeader(value = "X-Import-Token", required = false) String token) {
        if (!adminProperties.isSystemEndpointsAvailable()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        if (!adminProperties.isTokenValid(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(rebikeCrawlService.crawlRebikeOffers(false));
    }

    /** Manual competitor snapshot run (same auth as import). Ignores {@code ebf.competitor-watch.enabled}. */
    @PostMapping("/competitor-watch/run")
    public ResponseEntity<CompetitorWatchService.CompetitorWatchRunResult> runCompetitorWatch(
            @RequestHeader(value = "X-Import-Token", required = false) String token) {
        if (!adminProperties.isSystemEndpointsAvailable()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        if (!adminProperties.isTokenValid(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(competitorWatchService.runAll(false));
    }
}
