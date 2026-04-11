package eu.bikefinder.app.service.crawl.heuristic;

import eu.bikefinder.app.config.CrawlProperties;
import eu.bikefinder.app.domain.Source;
import eu.bikefinder.app.repo.SourceRepository;
import eu.bikefinder.app.service.crawl.CrawlHttpFetchService;
import eu.bikefinder.app.service.crawl.CrawlOfferPersistence;
import eu.bikefinder.app.service.crawl.CrawlRunResult;
import eu.bikefinder.app.service.crawl.RobotsAllowService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Velocorner bicycle marketplace: listing pages contain absolute links to {@code /ebike/...-id}; detail pages expose
 * title + CHF price in HTML (no Product JSON-LD).
 */
@Service
public class VelocornerMarketplaceCrawlService {

    private static final Logger log = LoggerFactory.getLogger(VelocornerMarketplaceCrawlService.class);

    private static final Pattern LISTING_PATH =
            Pattern.compile("https://velocorner\\.ch/(?:en|de|fr|it)/ebike/[^/]+-\\d+(?:\\?.*)?$");

    private final CrawlProperties crawlProperties;
    private final SourceRepository sourceRepository;
    private final CrawlHttpFetchService httpFetch;
    private final RobotsAllowService robotsAllowService;
    private final CrawlOfferPersistence crawlOfferPersistence;

    public VelocornerMarketplaceCrawlService(
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

    public CrawlRunResult crawl(UUID sourceId, String logLabel, List<String> seedUrls, int maxProducts, boolean requireMasterSwitch) {
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

        int discoveryCap = Math.min(200, Math.max(maxProducts * 3, maxProducts + 25));
        Set<String> listingUrls = new LinkedHashSet<>();
        try {
            for (String seed : seedUrls) {
                if (listingUrls.size() >= discoveryCap) {
                    break;
                }
                if (!robotsAllowService.isAllowed(seed)) {
                    continue;
                }
                String html = httpFetch.getUtf8(seed);
                collectListingLinks(html, seed, listingUrls, discoveryCap);
            }
        } catch (Exception e) {
            log.error("{} discovery failed", logLabel, e);
            crawlOfferPersistence.recordSourceCrawl(sourceId, "failed", 0);
            return CrawlRunResult.failed(e.getMessage());
        }

        int imported = 0;
        int skippedDup = 0;
        int failed = 0;
        for (String url : listingUrls) {
            if (imported >= maxProducts) {
                break;
            }
            try {
                if (!robotsAllowService.isAllowed(url)) {
                    failed++;
                    continue;
                }
                String page = httpFetch.getUtf8(url);
                Document doc = Jsoup.parse(page, url);
                Element h1 = doc.selectFirst("h1");
                String title = h1 != null ? h1.text().trim() : "Unknown";
                Element priceSpan = doc.selectFirst("span.text-brand-green");
                String priceRaw = priceSpan != null ? priceSpan.text() : null;
                BigDecimal chf = SwissDisplayPriceParser.parseFirstChf(priceRaw);
                if (chf == null || chf.signum() <= 0) {
                    failed++;
                    continue;
                }
                String img =
                        doc.select("meta[property=og:image]").attr("content");
                String[] images = img != null && !img.isBlank() ? new String[] {img} : new String[0];
                String offerId = url.substring(url.lastIndexOf('/') + 1);
                int q = offerId.indexOf('?');
                if (q >= 0) {
                    offerId = offerId.substring(0, q);
                }
                String brand = guessBrand(title);
                boolean inserted =
                        crawlOfferPersistence.importIfAbsent(
                                sourceId,
                                truncate(offerId, 200),
                                url,
                                brand,
                                title,
                                extractYear(title),
                                "trekking",
                                "used",
                                null,
                                null,
                                null,
                                chf,
                                "CHF",
                                images);
                if (inserted) {
                    imported++;
                } else {
                    skippedDup++;
                }
            } catch (Exception e) {
                log.warn("Velocorner product failed {}", url, e);
                failed++;
            }
        }
        String status = failed > 0 ? "partial" : "success";
        crawlOfferPersistence.recordSourceCrawl(sourceId, status, imported);
        return new CrawlRunResult(false, true, status, imported, skippedDup, failed, null);
    }

    private static void collectListingLinks(String html, String baseUrl, Set<String> out, int max) {
        Document doc = Jsoup.parse(html, baseUrl);
        for (Element a : doc.select("a[href]")) {
            if (out.size() >= max) {
                return;
            }
            String href = a.attr("abs:href");
            if (href.isEmpty()) {
                continue;
            }
            if (LISTING_PATH.matcher(href).matches()) {
                int q = href.indexOf('?');
                out.add(q >= 0 ? href.substring(0, q) : href);
            }
        }
    }

    private static Integer extractYear(String title) {
        if (title == null) {
            return null;
        }
        var m = Pattern.compile("(19|20)\\d{2}").matcher(title);
        if (m.find()) {
            try {
                return Integer.parseInt(m.group());
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    private static String guessBrand(String title) {
        if (title == null || title.isBlank()) {
            return "Unknown";
        }
        String[] p = title.replaceFirst("^\\d{4}\\s*", "").trim().split("\\s+");
        return p.length > 0 ? p[0] : "Unknown";
    }

    private static String truncate(String s, int max) {
        return s.length() <= max ? s : s.substring(0, max);
    }
}
