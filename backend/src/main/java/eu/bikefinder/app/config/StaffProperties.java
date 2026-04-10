package eu.bikefinder.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ebf.staff")
public class StaffProperties {

    /**
     * When non-empty, {@code GET /api/v1/offers} and related endpoints require header {@code X-Staff-Token}
     * with this value (Hamza / internal procurement only).
     */
    private String apiToken = "";

    public String getApiToken() {
        return apiToken;
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }

    public boolean isApiProtectionEnabled() {
        return apiToken != null && !apiToken.isBlank();
    }
}
