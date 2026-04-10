package eu.bikefinder.app.service.crawl;

import eu.bikefinder.app.config.CrawlProperties;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Service
public class CrawlHttpFetchService {

    public record HttpFetchResult(int statusCode, String bodyUtf8) {}

    private final CrawlProperties crawlProperties;
    private final HttpClient httpClient;
    private final Object delayLock = new Object();
    private long lastRequestEndMs;

    public CrawlHttpFetchService(CrawlProperties crawlProperties) {
        this.crawlProperties = crawlProperties;
        this.httpClient =
                HttpClient.newBuilder()
                        .connectTimeout(Duration.ofSeconds(20))
                        .followRedirects(HttpClient.Redirect.NORMAL)
                        .build();
    }

    /**
     * GET URL as UTF-8 text. Honors {@link CrawlProperties#getDelayMsBetweenRequests()} between calls.
     *
     * @throws IllegalStateException when status is not 2xx
     */
    public String getUtf8(String url) throws Exception {
        delayIfNeeded();
        HttpRequest req =
                HttpRequest.newBuilder(URI.create(url))
                        .timeout(Duration.ofSeconds(45))
                        .header("User-Agent", crawlProperties.getUserAgent())
                        .header("Accept", "text/html,application/xhtml+xml;q=0.9,*/*;q=0.8")
                        .header("Accept-Language", "de-DE,de;q=0.9,en;q=0.8")
                        .GET()
                        .build();
        HttpResponse<byte[]> res = httpClient.send(req, HttpResponse.BodyHandlers.ofByteArray());
        synchronized (delayLock) {
            lastRequestEndMs = System.currentTimeMillis();
        }
        if (res.statusCode() < 200 || res.statusCode() >= 300) {
            throw new IllegalStateException("HTTP " + res.statusCode() + " for " + url);
        }
        byte[] body = res.body() != null ? res.body() : new byte[0];
        return new String(body, StandardCharsets.UTF_8);
    }

    /**
     * GET URL and return status + body without throwing on non-2xx (for monitoring / competitor watch).
     */
    public HttpFetchResult fetchUtf8(String url) throws Exception {
        delayIfNeeded();
        HttpRequest req =
                HttpRequest.newBuilder(URI.create(url))
                        .timeout(Duration.ofSeconds(45))
                        .header("User-Agent", crawlProperties.getUserAgent())
                        .header("Accept", "text/html,application/xhtml+xml;q=0.9,*/*;q=0.8")
                        .header("Accept-Language", "de-CH,de;q=0.9,en;q=0.8")
                        .GET()
                        .build();
        HttpResponse<byte[]> res = httpClient.send(req, HttpResponse.BodyHandlers.ofByteArray());
        synchronized (delayLock) {
            lastRequestEndMs = System.currentTimeMillis();
        }
        byte[] body = res.body() != null ? res.body() : new byte[0];
        return new HttpFetchResult(res.statusCode(), new String(body, StandardCharsets.UTF_8));
    }

    private void delayIfNeeded() throws InterruptedException {
        long delay = crawlProperties.getDelayMsBetweenRequests();
        if (delay <= 0) {
            return;
        }
        synchronized (delayLock) {
            long elapsed = System.currentTimeMillis() - lastRequestEndMs;
            long wait = delay - elapsed;
            if (wait > 0) {
                Thread.sleep(wait);
            }
        }
    }
}
