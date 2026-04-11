package eu.bikefinder.app.service.crawl;

import eu.bikefinder.app.config.CrawlProperties;
import eu.bikefinder.app.domain.Source;
import eu.bikefinder.app.repo.SourceRepository;
import eu.bikefinder.app.service.crawl.jsonld.JsonLdProductExtractor;
import eu.bikefinder.app.service.crawl.jsonld.JsonLdProductExtractor.ParsedProduct;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Shared Shopify storefront crawl: seed pages → {@code /products/} links → JSON-LD {@code Product} →
 * {@link CrawlOfferPersistence}.
 */
@Service
public class ShopifyStorefrontCrawlService {

    private static final Logger log = LoggerFactory.getLogger(ShopifyStorefrontCrawlService.class);

    private final CrawlProperties crawlProperties;
    private final SourceRepository sourceRepository;
    private final CrawlHttpFetchService httpFetch;
    private final RobotsAllowService robotsAllowService;
    private final CrawlOfferPersistence crawlOfferPersistence;

    public ShopifyStorefrontCrawlService(
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

    /**
     * @param allowedCurrencies upper-case ISO 4217 codes (e.g. EUR, CHF). When null or empty, defaults to EUR only.
     * @param requireMasterSwitch when true (scheduled job), {@code ebf.crawl.enabled} must be true. Manual API calls
     *     pass false so ops can run a crawl without enabling the cron flag.
     */
    public CrawlRunResult crawl(
            UUID sourceId,
            String logLabel,
            String storefrontBase,
            List<String> seedUrls,
            int maxProducts,
            Set<String> allowedCurrencies,
            boolean requireMasterSwitch) {
        Set<String> currencies =
                allowedCurrencies == null || allowedCurrencies.isEmpty()
                        ? Set.of("EUR")
                        : allowedCurrencies.stream()
                                .map(c -> c.toUpperCase(Locale.ROOT))
                                .collect(Collectors.toSet());
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

        Set<String> productUrls = new LinkedHashSet<>();
        int discoveryCap = Math.min(200, Math.max(maxProducts * 3, maxProducts + 25));
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
                collectProductLinks(html, seed, storefrontBase, productUrls, discoveryCap);
            }
        } catch (Exception e) {
            log.error("{} crawl failed during discovery", logLabel, e);
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
                    log.debug("robots disallow {}", productUrl);
                    failed++;
                    continue;
                }
                String page = httpFetch.getUtf8(productUrl);
                Optional<ParsedProduct> parsed = JsonLdProductExtractor.extract(page);
                if (parsed.isEmpty()) {
                    log.debug("No Product JSON-LD for {}", productUrl);
                    failed++;
                    continue;
                }
                ParsedProduct p = parsed.get();
                MarketplaceProductImporter.Outcome imp =
                        MarketplaceProductImporter.tryImportJsonLd(
                                sourceId, productUrl, p, currencies, crawlOfferPersistence);
                if (imp == MarketplaceProductImporter.Outcome.SKIP_BAD) {
                    failed++;
                    continue;
                }
                if (imp == MarketplaceProductImporter.Outcome.INSERTED) {
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
                "{} crawl finished: imported={}, skippedDuplicates={}, failedOrSkippedParse={}",
                logLabel,
                imported,
                skippedDup,
                failed);
        return new CrawlRunResult(false, true, status, imported, skippedDup, failed, null);
    }

    private static void collectProductLinks(
            String html, String seedUrl, String storefront, Set<String> out, int max) {
        Document doc = Jsoup.parse(html, seedUrl);
        for (Element a : doc.select("a[href]")) {
            if (out.size() >= max) {
                return;
            }
            String href = a.attr("abs:href");
            if (href.isEmpty()) {
                continue;
            }
            String canonical = ShopifyUrlCanonicalizer.canonicalProductUrl(storefront, href);
            if (canonical != null) {
                out.add(canonical);
            }
        }
    }

}
