package eu.bikefinder.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Arrays;
import java.util.List;

/**
 * Browser CORS for {@code /api/**} when the Next.js UI is on another origin (e.g. Vercel). Comma-separated
 * list in {@code EBF_CORS_ORIGINS}.
 */
@ConfigurationProperties(prefix = "ebf.cors")
public class CorsProperties {

    /**
     * Comma-separated allowed origins (scheme + host, no path). Example:
     * {@code https://your-app.vercel.app,https://yourdomain.com}
     */
    private String allowedOrigins = "http://localhost:3000,http://127.0.0.1:3000";

    private boolean allowCredentials = true;

    public List<String> resolvedOrigins() {
        return Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    public String getAllowedOrigins() {
        return allowedOrigins;
    }

    public void setAllowedOrigins(String allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    public boolean isAllowCredentials() {
        return allowCredentials;
    }

    public void setAllowCredentials(boolean allowCredentials) {
        this.allowCredentials = allowCredentials;
    }
}
