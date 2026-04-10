package eu.bikefinder.app.service.crawl.rebike;

import eu.bikefinder.app.config.CrawlProperties;
import eu.bikefinder.app.domain.Source;
import eu.bikefinder.app.repo.SourceRepository;
import eu.bikefinder.app.service.crawl.CrawlHttpFetchService;
import eu.bikefinder.app.service.crawl.CrawlOfferPersistence;
import eu.bikefinder.app.service.crawl.RebikeUrlCanonicalizer;
import eu.bikefinder.app.service.crawl.RobotsAllowService;
import eu.bikefinder.app.service.crawl.jsonld.JsonLdProductExtractor;
import eu.bikefinder.app.service.crawl.jsonld.JsonLdProductExtractor.ParsedProduct;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * HTML listing crawl for Rebike (Shopify): discover {@code /products/} links from seed pages, fetch
 * {@code rebike.com/products/...} (no {@code /de/} prefix) and import via JSON-LD {@code Product}.
 */
@Service
public class RebikeCrawlService {

    private static final Logger log = LoggerFactory.getLogger(RebikeCrawlService.class);

    /** Matches {@code V3__dev_seed_data.sql} Rebike row. */
    public static final UUID REBIKE_SOURCE_ID = UUID.fromString("a0000001-0000-0000-0000-000000000001");

    private final CrawlProperties crawlProperties;
    private final SourceRepository sourceRepository;
    private final CrawlHttpFetchService httpFetch;
    private final RobotsAllowService robotsAllowService;
    private final CrawlOfferPersistence crawlOfferPersistence;

    public RebikeCrawlService(
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
     * @param requireMasterSwitch when true (scheduled job), {@code ebf.crawl.enabled} must be true. Manual API calls pass false so ops can run a crawl without enabling the cron flag.
     */
    public CrawlRunResult crawlRebikeOffers(boolean requireMasterSwitch) {
        if (requireMasterSwitch && !crawlProperties.isEnabled()) {
            return CrawlRunResult.skipped("ebf.crawl.enabled=false");
        }
        Optional<Source> sourceOpt = sourceRepository.findById(REBIKE_SOURCE_ID);
        if (sourceOpt.isEmpty()) {
            return CrawlRunResult.skipped("Rebike source row missing");
        }
        Source source = sourceOpt.get();
        if (!source.isCrawlEnabled()) {
            return CrawlRunResult.skipped("source.crawl_enabled=false");
        }
        if (!source.isRobotsCompliant()) {
            return CrawlRunResult.skipped("source.robots_compliant=false");
        }

        String storefront = crawlProperties.getRebike().getStorefrontBase();
        List<String> seeds = crawlProperties.getRebike().getSeedUrls();
        int max = crawlProperties.getRebike().getMaxProductsPerRun();

        Set<String> productUrls = new LinkedHashSet<>();
        int discoveryCap = Math.min(200, Math.max(max * 3, max + 25));
        try {
            for (String seed : seeds) {
                if (productUrls.size() >= discoveryCap) {
                    break;
                }
                if (!robotsAllowService.isAllowed(seed)) {
                    log.warn("robots.txt disallows seed URL {}", seed);
                    continue;
                }
                String html = httpFetch.getUtf8(seed);
                collectProductLinks(html, seed, storefront, productUrls, discoveryCap);
            }
        } catch (Exception e) {
            log.error("Rebike crawl failed during discovery", e);
            crawlOfferPersistence.recordSourceCrawl(REBIKE_SOURCE_ID, "failed", 0);
            return CrawlRunResult.failed(e.getMessage());
        }

        int imported = 0;
        int skippedDup = 0;
        int failed = 0;

        for (String productUrl : productUrls) {
            if (imported >= max) {
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
                if (p.priceEur() == null || p.priceEur().signum() <= 0) {
                    failed++;
                    continue;
                }
                if (p.currencyCode() != null && !"EUR".equalsIgnoreCase(p.currencyCode())) {
                    log.debug("Skip non-EUR offer {}", productUrl);
                    failed++;
                    continue;
                }
                String sourceOfferId = truncateOfferId(resolveSourceOfferId(productUrl, p), 200);
                String brand = p.brand() != null ? p.brand() : guessBrandFromTitle(p.name());
                String model = p.model() != null ? p.model() : (p.name() != null ? p.name() : "Unknown");
                String[] images =
                        p.imageUrl() != null ? new String[] {p.imageUrl()} : new String[0];
                boolean inserted =
                        crawlOfferPersistence.importIfAbsent(
                                REBIKE_SOURCE_ID,
                                sourceOfferId,
                                productUrl,
                                brand,
                                model,
                                p.modelYear(),
                                p.bikeCategory(),
                                "refurbished",
                                p.motorBrand(),
                                p.batteryWh(),
                                p.mileageKm(),
                                p.priceEur(),
                                "EUR",
                                images);
                if (inserted) {
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
        crawlOfferPersistence.recordSourceCrawl(REBIKE_SOURCE_ID, status, imported);
        log.info(
                "Rebike crawl finished: imported={}, skippedDuplicates={}, failedOrSkippedParse={}",
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
            String canonical = RebikeUrlCanonicalizer.canonicalProductUrl(storefront, href);
            if (canonical != null) {
                out.add(canonical);
            }
        }
    }

    private static String resolveSourceOfferId(String canonicalProductUrl, ParsedProduct p) {
        if (p.mpn() != null && !p.mpn().isBlank()) {
            return p.mpn().trim();
        }
        if (p.sku() != null && !p.sku().isBlank()) {
            return p.sku().trim();
        }
        return slugFromProductUrl(canonicalProductUrl);
    }

    private static String slugFromProductUrl(String url) {
        try {
            URI u = URI.create(url);
            String path = u.getPath();
            int idx = path.indexOf("/products/");
            if (idx >= 0) {
                return path.substring(idx + "/products/".length());
            }
            int slash = path.lastIndexOf('/');
            return slash >= 0 ? path.substring(slash + 1) : path;
        } catch (Exception e) {
            return "unknown";
        }
    }

    private static String guessBrandFromTitle(String name) {
        if (name == null || name.isBlank()) {
            return "Unknown";
        }
        String[] parts = name.trim().split("\\s+");
        return parts.length > 0 ? parts[0] : "Unknown";
    }

    private static String truncateOfferId(String id, int maxLen) {
        if (id == null) {
            return "unknown";
        }
        return id.length() <= maxLen ? id : id.substring(0, maxLen);
    }

    public record CrawlRunResult(
            boolean skipped,
            boolean ran,
            String status,
            int imported,
            int skippedDuplicates,
            int failed,
            String reason) {

        static CrawlRunResult skipped(String reason) {
            return new CrawlRunResult(true, false, "skipped", 0, 0, 0, reason);
        }

        static CrawlRunResult failed(String message) {
            return new CrawlRunResult(false, true, "failed", 0, 0, 0, message);
        }
    }
}
