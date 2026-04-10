package eu.bikefinder.app.service.pricesense.live;

import eu.bikefinder.app.config.PriceSenseProperties;
import eu.bikefinder.app.domain.CompetitorWatchTarget;
import eu.bikefinder.app.repo.CompetitorWatchTargetRepository;
import eu.bikefinder.app.service.crawl.RobotsAllowService;
import eu.bikefinder.app.service.crawl.jsonld.JsonLdProductExtractor;
import eu.bikefinder.app.service.crawl.jsonld.JsonLdProductExtractor.ParsedProduct;
import eu.bikefinder.app.web.dto.LiveProbeRowDto;
import eu.bikefinder.app.web.dto.PriceSenseRequest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * On-demand live checks: Shopify-style {@code /search?q=} then first {@code /products/…} page with JSON-LD
 * {@code Product}. Non-Shopify storefronts may return errors until a dedicated adapter exists.
 */
@Service
public class PriceSenseLiveProbeService {

    private static final Logger log = LoggerFactory.getLogger(PriceSenseLiveProbeService.class);
    private static final MathContext MC = new MathContext(12, RoundingMode.HALF_UP);

    private final CompetitorWatchTargetRepository targetRepository;
    private final RobotsAllowService robotsAllowService;
    private final LiveCompetitorHttpFetch httpFetch;
    private final PriceSenseProperties priceSenseProperties;

    public PriceSenseLiveProbeService(
            CompetitorWatchTargetRepository targetRepository,
            RobotsAllowService robotsAllowService,
            LiveCompetitorHttpFetch httpFetch,
            PriceSenseProperties priceSenseProperties) {
        this.targetRepository = targetRepository;
        this.robotsAllowService = robotsAllowService;
        this.httpFetch = httpFetch;
        this.priceSenseProperties = priceSenseProperties;
    }

    public List<LiveProbeRowDto> probeAll(PriceSenseRequest request, BigDecimal eurChf) {
        if (!priceSenseProperties.getLiveCompetitorSearch().isEnabled()) {
            return List.of();
        }
        List<CompetitorWatchTarget> targets =
                targetRepository.findByActiveIsTrueAndLivePriceProbeEnabledIsTrueOrderBySlugAsc();
        if (targets.isEmpty()) {
            return List.of();
        }
        int timeoutSec = Math.max(5, priceSenseProperties.getLiveCompetitorSearch().getTimeoutSeconds());
        int delayMs = Math.max(0, priceSenseProperties.getLiveCompetitorSearch().getDelayMsBetweenRequests());

        try (ExecutorService pool = Executors.newVirtualThreadPerTaskExecutor()) {
            List<CompletableFuture<LiveProbeRowDto>> futures = new ArrayList<>();
            for (CompetitorWatchTarget t : targets) {
                futures.add(
                        CompletableFuture.supplyAsync(
                                () -> probeOne(t, request, eurChf, delayMs), pool));
            }
            List<LiveProbeRowDto> out = new ArrayList<>();
            for (int i = 0; i < targets.size(); i++) {
                CompetitorWatchTarget t = targets.get(i);
                CompletableFuture<LiveProbeRowDto> f = futures.get(i);
                try {
                    out.add(f.get(timeoutSec, TimeUnit.SECONDS));
                } catch (TimeoutException e) {
                    f.cancel(true);
                    out.add(
                            new LiveProbeRowDto(
                                    t.getSlug(),
                                    t.getDisplayName(),
                                    null,
                                    null,
                                    "timeout after " + timeoutSec + "s"));
                } catch (ExecutionException e) {
                    Throwable c = e.getCause() != null ? e.getCause() : e;
                    out.add(
                            new LiveProbeRowDto(
                                    t.getSlug(),
                                    t.getDisplayName(),
                                    null,
                                    null,
                                    c.getMessage()));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    out.add(
                            new LiveProbeRowDto(
                                    t.getSlug(),
                                    t.getDisplayName(),
                                    null,
                                    null,
                                    "interrupted"));
                }
            }
            return out;
        }
    }

    private LiveProbeRowDto probeOne(
            CompetitorWatchTarget t, PriceSenseRequest request, BigDecimal eurChf, int delayMs) {
        String slug = t.getSlug();
        String name = t.getDisplayName();
        try {
            String origin = originFromWatchUrl(t.getWatchUrl());
            String q =
                    (request.brand().trim() + " " + request.model().trim()).trim();
            if (q.isBlank()) {
                return new LiveProbeRowDto(slug, name, null, null, "empty brand/model");
            }
            String enc = URLEncoder.encode(q, StandardCharsets.UTF_8);
            String searchUrl = origin + "/search?q=" + enc;
            if (!robotsAllowService.isAllowed(searchUrl)) {
                return new LiveProbeRowDto(slug, name, null, null, "robots.txt disallows search");
            }
            LiveCompetitorHttpFetch.HttpResult searchRes = httpFetch.get(searchUrl, delayMs);
            if (searchRes.statusCode() < 200 || searchRes.statusCode() >= 400) {
                return new LiveProbeRowDto(
                        slug, name, null, null, "search HTTP " + searchRes.statusCode());
            }
            String productUrl = firstProductUrl(searchRes.bodyUtf8(), searchUrl);
            if (productUrl == null) {
                return new LiveProbeRowDto(
                        slug,
                        name,
                        null,
                        null,
                        "no /products/ link on search results (storefront may not be Shopify-style)");
            }
            if (!robotsAllowService.isAllowed(productUrl)) {
                return new LiveProbeRowDto(slug, name, null, null, "robots.txt disallows product URL");
            }
            LiveCompetitorHttpFetch.HttpResult productRes = httpFetch.get(productUrl, delayMs);
            if (productRes.statusCode() < 200 || productRes.statusCode() >= 400) {
                return new LiveProbeRowDto(
                        slug, name, null, null, "product HTTP " + productRes.statusCode());
            }
            var parsedOpt = JsonLdProductExtractor.extract(productRes.bodyUtf8());
            if (parsedOpt.isEmpty()) {
                return new LiveProbeRowDto(
                        slug, name, null, productUrl, "no Product JSON-LD on first result");
            }
            ParsedProduct p = parsedOpt.get();
            if (p.priceEur() == null || p.priceEur().signum() <= 0) {
                return new LiveProbeRowDto(slug, name, null, productUrl, "no price in JSON-LD");
            }
            BigDecimal chf = toChf(p.priceEur(), p.currencyCode(), eurChf);
            if (chf == null) {
                return new LiveProbeRowDto(
                        slug, name, null, productUrl, "unsupported currency: " + p.currencyCode());
            }
            return new LiveProbeRowDto(slug, name, chf, productUrl, null);
        } catch (Exception e) {
            log.debug("live probe {} failed", slug, e);
            return new LiveProbeRowDto(slug, name, null, null, e.getMessage());
        }
    }

    static String originFromWatchUrl(String watchUrl) {
        URI u = URI.create(watchUrl.trim());
        String scheme = u.getScheme() != null ? u.getScheme() : "https";
        String host = u.getHost();
        if (host == null) {
            throw new IllegalArgumentException("invalid watch URL: " + watchUrl);
        }
        int port = u.getPort();
        if (port > 0 && port != 443 && port != 80) {
            return scheme + "://" + host + ":" + port;
        }
        return scheme + "://" + host;
    }

    static String firstProductUrl(String html, String baseUri) {
        Document doc = Jsoup.parse(html, baseUri);
        for (Element a : doc.select("a[href]")) {
            String href = a.attr("abs:href");
            if (href.isEmpty()) {
                continue;
            }
            String path = URI.create(href).getPath();
            if (path != null && path.contains("/products/") && !path.contains("/collections/")) {
                return href;
            }
        }
        return null;
    }

    private static BigDecimal toChf(BigDecimal amount, String currency, BigDecimal eurChf) {
        if (amount == null) {
            return null;
        }
        String c = currency != null ? currency.trim().toUpperCase(Locale.ROOT) : "";
        if ("CHF".equals(c)) {
            return amount.setScale(2, RoundingMode.HALF_UP);
        }
        if ("EUR".equals(c) && eurChf != null) {
            return amount.multiply(eurChf, MC).setScale(2, RoundingMode.HALF_UP);
        }
        return null;
    }

    public static List<BigDecimal> successfulChfPrices(List<LiveProbeRowDto> rows) {
        return rows.stream()
                .map(LiveProbeRowDto::priceChf)
                .filter(Objects::nonNull)
                .sorted()
                .toList();
    }
}
