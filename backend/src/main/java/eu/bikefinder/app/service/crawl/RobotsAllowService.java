package eu.bikefinder.app.service.crawl;

import crawlercommons.robots.BaseRobotRules;
import crawlercommons.robots.SimpleRobotRulesParser;
import eu.bikefinder.app.config.CrawlProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RobotsAllowService {

    private static final Logger log = LoggerFactory.getLogger(RobotsAllowService.class);

    private final CrawlProperties crawlProperties;
    private final HttpClient httpClient;
    private final SimpleRobotRulesParser parser = new SimpleRobotRulesParser();
    private final Map<String, BaseRobotRules> cache = new ConcurrentHashMap<>();

    public RobotsAllowService(CrawlProperties crawlProperties) {
        this.crawlProperties = crawlProperties;
        this.httpClient =
                HttpClient.newBuilder()
                        .connectTimeout(Duration.ofSeconds(15))
                        .followRedirects(HttpClient.Redirect.NORMAL)
                        .build();
    }

    public boolean isAllowed(String absoluteUrl) {
        try {
            URI uri = URI.create(absoluteUrl);
            String host = uri.getHost();
            if (host == null) {
                return false;
            }
            String origin = uri.getScheme() + "://" + host + (uri.getPort() > 0 ? ":" + uri.getPort() : "");
            BaseRobotRules rules = cache.computeIfAbsent(origin, this::fetchRules);
            if (rules == null) {
                log.warn("No robots.txt for {}; denying fetch of {}", origin, absoluteUrl);
                return false;
            }
            return rules.isAllowed(absoluteUrl);
        } catch (Exception e) {
            log.warn("robots check failed for {}: {}", absoluteUrl, e.getMessage());
            return false;
        }
    }

    private BaseRobotRules fetchRules(String origin) {
        String robotsUrl = origin + "/robots.txt";
        try {
            HttpRequest req =
                    HttpRequest.newBuilder(URI.create(robotsUrl))
                            .timeout(Duration.ofSeconds(20))
                            .header("User-Agent", crawlProperties.getUserAgent())
                            .GET()
                            .build();
            HttpResponse<byte[]> res = httpClient.send(req, HttpResponse.BodyHandlers.ofByteArray());
            if (res.statusCode() == 404) {
                return parser.failedFetch(404);
            }
            if (res.statusCode() >= 400) {
                return parser.failedFetch(res.statusCode());
            }
            byte[] body = res.body() != null ? res.body() : new byte[0];
            return parser.parseContent(robotsUrl, body, "text/plain", robotNameProductTokens());
        } catch (Exception e) {
            log.warn("Could not load robots.txt from {}: {}", robotsUrl, e.getMessage());
            return null;
        }
    }

    /**
     * crawler-commons 1.5+ expects lower-case, single-token product names (RFC 9309-style), not full User-Agent strings.
     */
    private List<String> robotNameProductTokens() {
        String ua = crawlProperties.getUserAgent();
        if (ua == null || ua.isBlank()) {
            return List.of("europebikefinderbot");
        }
        String first = ua.split("[\\s/]+")[0].toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9\\-]", "");
        if (first.isEmpty()) {
            return List.of("europebikefinderbot");
        }
        return List.of(first);
    }
}
