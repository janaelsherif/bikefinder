package eu.bikefinder.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "ebf.crawl")
public class CrawlProperties {

    /**
     * Master switch: when false, scheduled and manual crawl endpoints do nothing.
     */
    private boolean enabled = false;

    private String userAgent = "EuropeBikeFinderBot/0.1 (dev; +https://example.invalid/contact)";

    private long delayMsBetweenRequests = 1500;

    /** Spring cron when {@link #enabled} is true. */
    private String cron = "0 0 3 * * *";

    private final Rebike rebike = new Rebike();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public long getDelayMsBetweenRequests() {
        return delayMsBetweenRequests;
    }

    public void setDelayMsBetweenRequests(long delayMsBetweenRequests) {
        this.delayMsBetweenRequests = delayMsBetweenRequests;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public Rebike getRebike() {
        return rebike;
    }

    public static class Rebike {
        /**
         * Shopify storefront host where product HTML and JSON-LD resolve (not www.rebike.de, which
         * redirects to locale paths that 404 for crawlers).
         */
        private String storefrontBase = "https://rebike.com";

        private List<String> seedUrls = new ArrayList<>(List.of("https://www.rebike.de/"));

        private int maxProductsPerRun = 40;

        public String getStorefrontBase() {
            return storefrontBase;
        }

        public void setStorefrontBase(String storefrontBase) {
            this.storefrontBase = storefrontBase;
        }

        public List<String> getSeedUrls() {
            return seedUrls;
        }

        public void setSeedUrls(List<String> seedUrls) {
            this.seedUrls = seedUrls;
        }

        public int getMaxProductsPerRun() {
            return maxProductsPerRun;
        }

        public void setMaxProductsPerRun(int maxProductsPerRun) {
            this.maxProductsPerRun = maxProductsPerRun;
        }
    }
}
