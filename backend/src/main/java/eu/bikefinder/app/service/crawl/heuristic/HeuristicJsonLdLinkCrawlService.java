package eu.bikefinder.app.service.crawl.heuristic;

import eu.bikefinder.app.config.CrawlProperties;
import eu.bikefinder.app.domain.Source;
import eu.bikefinder.app.repo.SourceRepository;
import eu.bikefinder.app.service.crawl.CrawlHttpFetchService;
import eu.bikefinder.app.service.crawl.CrawlOfferPersistence;
import eu.bikefinder.app.service.crawl.CrawlRunResult;
import eu.bikefinder.app.service.crawl.MarketplaceProductImporter;
import eu.bikefinder.app.service.crawl.RobotsAllowService;
import eu.bikefinder.app.service.crawl.jsonld.JsonLdProductExtractor;
import eu.bikefinder.app.service.crawl.jsonld.JsonLdProductExtractor.ParsedProduct;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Discover product URLs on listing pages via regex, then import {@link ParsedProduct} from JSON-LD on each page.
 */
@Service
public class HeuristicJsonLdLinkCrawlService {

    private static final Logger log = LoggerFactory.getLogger(HeuristicJsonLdLinkCrawlService.class);

    private final CrawlProperties crawlProperties;
    private final SourceRepository sourceRepository;
    private final CrawlHttpFetchService httpFetch;
    private final RobotsAllowService robotsAllowService;
    private final CrawlOfferPersistence crawlOfferPersistence;

    public HeuristicJsonLdLinkCrawlService(
            CrawlProperties crawlProperties,
            SourceRepository sourceRepository,
            CrawlHttpFetchService httpFetch,
            RobotsAllowService robotsAllowService,
            CrawlOfferPersistence crawlOfferPersistence) {
        this.crawlProperties = crawlProperties;
        this.sourceRepository = sourceRepository;
        this.httpFetch = httpFetch;
        this.robotsAllowService = robotsAllowService;
        this.crawlOfferPersistence = crawlOfferPersistence;
    }

    public CrawlRunResult crawl(
            UUID sourceId,
            String logLabel,
            List<String> seedUrls,
            String linkRegex,
            int maxProducts,
            Set<String> allowedCurrencies,
            boolean requireMasterSwitch) {
        if (requireMasterSwitch && !crawlProperties.isEnabled()) {
            return CrawlRunResult.skipped("ebf.crawl.enabled=false");
        }
        Optional<Source> sourceOpt = sourceRepository.findById(sourceId);
        if (sourceOpt.isEmpty()) {
            return CrawlRunResult.skipped(logLabel + " source row missing");
        }
        Source source = sourceOpt.get();
        if (!source.isCrawlEnabled()) {
            return CrawlRunResult.skipped("source.crawl_enabled=false");
        }
        if (!source.isRobotsCompliant()) {
            return CrawlRunResult.skipped("source.robots_compliant=false");
        }
        if (linkRegex == null || linkRegex.isBlank()) {
            return CrawlRunResult.skipped("link-regex missing");
        }
        final Pattern linkPattern;
        try {
            linkPattern = Pattern.compile(linkRegex.trim());
        } catch (Exception e) {
            return CrawlRunResult.failed("invalid link-regex: " + e.getMessage());
        }

        Set<String> currencies =
                allowedCurrencies == null || allowedCurrencies.isEmpty()
                        ? Set.of("EUR")
                        : allowedCurrencies;
        int discoveryCap = Math.min(200, Math.max(maxProducts * 3, maxProducts + 25));
        Set<String> productUrls = new LinkedHashSet<>();
        try {
            for (String seed : seedUrls) {
                if (productUrls.size() >= discoveryCap) {
                    break;
                }
                if (!robotsAllowService.isAllowed(seed)) {
                    log.warn("robots.txt disallows seed URL {}", seed);
                    continue;
                }
                String html = httpFetch.getUtf8(seed);
                collectMatchingLinks(html, seed, linkPattern, productUrls, discoveryCap);
            }
        } catch (Exception e) {
            log.error("{} heuristic discovery failed", logLabel, e);
            crawlOfferPersistence.recordSourceCrawl(sourceId, "failed", 0);
            return CrawlRunResult.failed(e.getMessage());
        }

        int imported = 0;
        int skippedDup = 0;
        int failed = 0;
        for (String productUrl : productUrls) {
            if (imported >= maxProducts) {
                break;
            }
            try {
                if (!robotsAllowService.isAllowed(productUrl)) {
                    failed++;
                    continue;
                }
                String page = httpFetch.getUtf8(productUrl);
                Optional<ParsedProduct> parsed = JsonLdProductExtractor.extract(page);
                if (parsed.isEmpty()) {
                    failed++;
                    continue;
                }
                MarketplaceProductImporter.Outcome imp =
                        MarketplaceProductImporter.tryImportJsonLd(
                                sourceId, productUrl, parsed.get(), currencies, crawlOfferPersistence);
                if (imp == MarketplaceProductImporter.Outcome.SKIP_BAD) {
                    failed++;
                } else if (imp == MarketplaceProductImporter.Outcome.INSERTED) {
                    imported++;
                } else {
                    skippedDup++;
                }
            } catch (Exception e) {
                log.warn("Failed product {}", productUrl, e);
                failed++;
            }
        }
        String status = failed > 0 ? "partial" : "success";
        crawlOfferPersistence.recordSourceCrawl(sourceId, status, imported);
        log.info(
                "{} heuristic JSON-LD crawl: imported={}, skippedDuplicates={}, failedOrSkipped={}",
                logLabel,
                imported,
                skippedDup,
                failed);
        return new CrawlRunResult(false, true, status, imported, skippedDup, failed, null);
    }

    private static void collectMatchingLinks(
            String html, String seedUrl, Pattern linkPattern, Set<String> out, int max) {
        Document doc = Jsoup.parse(html, seedUrl);
        for (Element a : doc.select("a[href]")) {
            if (out.size() >= max) {
                return;
            }
            String href = a.attr("abs:href");
            if (href.isEmpty()) {
                continue;
            }
            if (linkPattern.matcher(href).matches()) {
                int q = href.indexOf('?');
                String canon = q >= 0 ? href.substring(0, q) : href;
                out.add(canon);
            }
        }
    }
}
