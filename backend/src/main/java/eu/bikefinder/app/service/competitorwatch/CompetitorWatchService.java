package eu.bikefinder.app.service.competitorwatch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.bikefinder.app.config.CompetitorWatchProperties;
import eu.bikefinder.app.config.CrawlProperties;
import eu.bikefinder.app.domain.CompetitorWatchSnapshot;
import eu.bikefinder.app.domain.CompetitorWatchTarget;
import eu.bikefinder.app.repo.CompetitorWatchSnapshotRepository;
import eu.bikefinder.app.repo.CompetitorWatchTargetRepository;
import eu.bikefinder.app.service.crawl.CrawlHttpFetchService;
import eu.bikefinder.app.service.crawl.RobotsAllowService;
import eu.bikefinder.app.web.dto.CompetitorWatchDashboardRow;
import eu.bikefinder.app.web.dto.CompetitorWatchSnapshotDto;
import eu.bikefinder.app.web.dto.CompetitorWatchTargetDto;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CompetitorWatchService {

    private static final Logger log = LoggerFactory.getLogger(CompetitorWatchService.class);

    private final CompetitorWatchProperties props;
    private final CrawlProperties crawlProperties;
    private final RobotsAllowService robotsAllowService;
    private final CrawlHttpFetchService httpFetch;
    private final CompetitorWatchTargetRepository targetRepository;
    private final CompetitorWatchSnapshotRepository snapshotRepository;
    private final ObjectMapper objectMapper;

    public CompetitorWatchService(
            CompetitorWatchProperties props,
            CrawlProperties crawlProperties,
            RobotsAllowService robotsAllowService,
            CrawlHttpFetchService httpFetch,
            CompetitorWatchTargetRepository targetRepository,
            CompetitorWatchSnapshotRepository snapshotRepository,
            ObjectMapper objectMapper) {
        this.props = props;
        this.crawlProperties = crawlProperties;
        this.robotsAllowService = robotsAllowService;
        this.httpFetch = httpFetch;
        this.targetRepository = targetRepository;
        this.snapshotRepository = snapshotRepository;
        this.objectMapper = objectMapper;
    }

    /** Manual or scheduled run of all active targets. */
    @Transactional
    public CompetitorWatchRunResult runAll(boolean requireMasterSwitch) {
        if (requireMasterSwitch && !props.isEnabled()) {
            return CompetitorWatchRunResult.skipped("ebf.competitor-watch.enabled=false");
        }
        List<String> lines = new ArrayList<>();
        int ok = 0;
        int fail = 0;
        for (CompetitorWatchTarget t : targetRepository.findByActiveIsTrueOrderBySlugAsc()) {
            try {
                boolean one = runOne(t);
                if (one) {
                    ok++;
                } else {
                    fail++;
                }
                lines.add(t.getSlug() + (one ? ":ok" : ":fail"));
            } catch (Exception e) {
                fail++;
                lines.add(t.getSlug() + ":error:" + e.getMessage());
                log.warn("competitor watch {} failed", t.getSlug(), e);
            }
        }
        return new CompetitorWatchRunResult(false, String.join("; ", lines), ok, fail);
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<CompetitorWatchDashboardRow> dashboard() {
        List<CompetitorWatchDashboardRow> out = new ArrayList<>();
        for (CompetitorWatchTarget t : targetRepository.findByActiveIsTrueOrderBySlugAsc()) {
            CompetitorWatchTargetDto td =
                    new CompetitorWatchTargetDto(
                            t.getId(), t.getSlug(), t.getDisplayName(), t.getWatchUrl(), t.isActive());
            Optional<CompetitorWatchSnapshot> latest =
                    snapshotRepository.findFirstByTarget_IdOrderByCapturedAtDesc(t.getId());
            CompetitorWatchSnapshotDto sd =
                    latest.map(this::toSnapshotDto).orElse(null);
            out.add(new CompetitorWatchDashboardRow(td, sd));
        }
        return out;
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<CompetitorWatchSnapshotDto> history(String slug) {
        return snapshotRepository.findTop50ByTarget_SlugOrderByCapturedAtDesc(slug).stream()
                .map(this::toSnapshotDto)
                .toList();
    }

    private CompetitorWatchSnapshotDto toSnapshotDto(CompetitorWatchSnapshot s) {
        return new CompetitorWatchSnapshotDto(
                s.getId(),
                s.getTarget().getSlug(),
                s.getCapturedAt(),
                s.getHttpStatus(),
                s.getListingCountEstimate(),
                s.getDeltaVsPrevious(),
                s.getErrorMessage(),
                s.getDurationMs());
    }

    /** @return true if snapshot saved without transport error */
    public boolean runOne(CompetitorWatchTarget t) throws Exception {
        long start = System.currentTimeMillis();
        String url = t.getWatchUrl();
        if (!robotsAllowService.isAllowed(url)) {
            saveSnapshot(t, null, null, null, "robots.txt disallows " + url, (int) (System.currentTimeMillis() - start));
            return false;
        }
        CrawlHttpFetchService.HttpFetchResult res = httpFetch.fetchUtf8(url);
        int ms = (int) (System.currentTimeMillis() - start);
        if (res.statusCode() < 200 || res.statusCode() >= 300) {
            saveSnapshot(t, res.statusCode(), null, null, "HTTP " + res.statusCode(), ms);
            return false;
        }
        Document doc = Jsoup.parse(res.bodyUtf8(), url);
        int n = CompetitorListingEstimator.estimate(doc);
        Optional<CompetitorWatchSnapshot> prev = snapshotRepository.findFirstByTarget_IdOrderByCapturedAtDesc(t.getId());
        Integer delta = null;
        if (prev.isPresent() && prev.get().getListingCountEstimate() != null) {
            delta = n - prev.get().getListingCountEstimate();
        }
        ObjectNode summary = objectMapper.createObjectNode();
        summary.put("userAgentProduct", crawlProperties.getUserAgent());
        summary.put("method", "shopify_paths_or_generic_dom");
        saveSnapshotOk(t, res.statusCode(), n, delta, summary, ms);
        if (delta != null && Math.abs(delta) >= props.getAlertDeltaLogThreshold()) {
            log.warn(
                    "Competitor watch signal {} delta={} (estimate {} vs prior); verify site HTML unchanged",
                    t.getSlug(),
                    delta,
                    n);
        }
        return true;
    }

    private void saveSnapshot(
            CompetitorWatchTarget t,
            Integer httpStatus,
            Integer count,
            Integer delta,
            String error,
            int durationMs) {
        CompetitorWatchSnapshot s = new CompetitorWatchSnapshot();
        s.setTarget(t);
        s.setHttpStatus(httpStatus);
        s.setListingCountEstimate(count);
        s.setDeltaVsPrevious(delta);
        s.setErrorMessage(error);
        s.setDurationMs(durationMs);
        snapshotRepository.save(s);
    }

    private void saveSnapshotOk(
            CompetitorWatchTarget t,
            int httpStatus,
            int count,
            Integer delta,
            ObjectNode summary,
            int durationMs) {
        CompetitorWatchSnapshot s = new CompetitorWatchSnapshot();
        s.setTarget(t);
        s.setHttpStatus(httpStatus);
        s.setListingCountEstimate(count);
        s.setDeltaVsPrevious(delta);
        s.setSummaryJson(summary);
        s.setDurationMs(durationMs);
        snapshotRepository.save(s);
    }

    public record CompetitorWatchRunResult(boolean skipped, String message, int targetsOk, int targetsFailed) {
        public static CompetitorWatchRunResult skipped(String reason) {
            return new CompetitorWatchRunResult(true, reason, 0, 0);
        }
    }
}
