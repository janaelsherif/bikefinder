package eu.bikefinder.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ebf.search")
public class SearchProperties {

    /**
     * When false (default), staff wish search hides {@code bike_offer.is_demo} rows (e.g. dev seed).
     */
    private boolean includeDemoListings = false;

    /**
     * When true (default), {@code GET /api/v1/offers/wish} runs a relaxed query if strict filters return nothing.
     */
    private boolean nearMatchFallback = true;

    public boolean isIncludeDemoListings() {
        return includeDemoListings;
    }

    public void setIncludeDemoListings(boolean includeDemoListings) {
        this.includeDemoListings = includeDemoListings;
    }

    public boolean isNearMatchFallback() {
        return nearMatchFallback;
    }

    public void setNearMatchFallback(boolean nearMatchFallback) {
        this.nearMatchFallback = nearMatchFallback;
    }
}
