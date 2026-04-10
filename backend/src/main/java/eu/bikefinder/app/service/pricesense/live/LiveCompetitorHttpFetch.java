package eu.bikefinder.app.service.pricesense.live;

import eu.bikefinder.app.config.CrawlProperties;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * HTTP GET for live price probes — separate from {@link eu.bikefinder.app.service.crawl.CrawlHttpFetchService}
 * so we do not serialize through the global Rebike crawl inter-request delay.
 */
@Component
public class LiveCompetitorHttpFetch {

    public record HttpResult(int statusCode, String bodyUtf8) {}

    private final CrawlProperties crawlProperties;
    private final HttpClient httpClient =
            HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(12))
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build();

    public LiveCompetitorHttpFetch(CrawlProperties crawlProperties) {
        this.crawlProperties = crawlProperties;
    }

    public HttpResult get(String url, int delayMsBefore) throws Exception {
        if (delayMsBefore > 0) {
            Thread.sleep(delayMsBefore);
        }
        HttpRequest req =
                HttpRequest.newBuilder(URI.create(url))
                        .timeout(Duration.ofSeconds(28))
                        .header("User-Agent", crawlProperties.getUserAgent())
                        .header("Accept", "text/html,application/xhtml+xml;q=0.9,*/*;q=0.8")
                        .header("Accept-Language", "de-CH,de;q=0.9,en;q=0.8")
                        .GET()
                        .build();
        HttpResponse<byte[]> res = httpClient.send(req, HttpResponse.BodyHandlers.ofByteArray());
        byte[] body = res.body() != null ? res.body() : new byte[0];
        return new HttpResult(res.statusCode(), new String(body, StandardCharsets.UTF_8));
    }
}
