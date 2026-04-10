package eu.bikefinder.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ebf.admin")
public class AdminProperties {

    /**
     * Shared secret for {@code POST /api/v1/system/import-offers}. Empty = endpoint disabled unless
     * {@link #isDevOpenSystemEndpoints()} is true.
     */
    private String importToken = "";

    /**
     * When true, {@code /api/v1/system/*} import and crawl work <strong>without</strong> {@code X-Import-Token}.
     * Intended for localhost development only — never enable in production.
     */
    private boolean devOpenSystemEndpoints = false;

    public String getImportToken() {
        return importToken;
    }

    public void setImportToken(String importToken) {
        this.importToken = importToken;
    }

    public boolean isDevOpenSystemEndpoints() {
        return devOpenSystemEndpoints;
    }

    public void setDevOpenSystemEndpoints(boolean devOpenSystemEndpoints) {
        this.devOpenSystemEndpoints = devOpenSystemEndpoints;
    }

    /** Legacy: token configured (strict mode). */
    public boolean isImportEnabled() {
        return importToken != null && !importToken.isBlank();
    }

    /** System import/crawl routes are available (token or dev-open). */
    public boolean isSystemEndpointsAvailable() {
        return isImportEnabled() || devOpenSystemEndpoints;
    }

    public boolean isTokenValid(String providedToken) {
        if (devOpenSystemEndpoints) {
            return true;
        }
        if (!isImportEnabled()) {
            return false;
        }
        return providedToken != null && importToken.equals(providedToken);
    }
}
