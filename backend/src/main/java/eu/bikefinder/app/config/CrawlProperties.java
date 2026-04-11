package eu.bikefinder.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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

    private final UpwayDe upwayDe = new UpwayDe();

    /**
     * Extra Shopify storefronts (see {@code docs/PATRICK_WUNSCH_AND_SOURCES.md} §2–3). Non-Shopify sites need other
     * adapters.
     */
    private List<ShopifyTarget> shopifyTargets = new ArrayList<>();

    /** JSON-LD product pages discovered via regex on listing seeds (e.g. BibiBike, Veloplus). */
    private List<JsonLdLinkTarget> jsonLdLinkTargets = new ArrayList<>();

    private final VelocornerMarketplace velocorner = new VelocornerMarketplace();

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

    public UpwayDe getUpwayDe() {
        return upwayDe;
    }

    public List<ShopifyTarget> getShopifyTargets() {
        return shopifyTargets;
    }

    public void setShopifyTargets(List<ShopifyTarget> shopifyTargets) {
        this.shopifyTargets = shopifyTargets != null ? shopifyTargets : new ArrayList<>();
    }

    public List<JsonLdLinkTarget> getJsonLdLinkTargets() {
        return jsonLdLinkTargets;
    }

    public void setJsonLdLinkTargets(List<JsonLdLinkTarget> jsonLdLinkTargets) {
        this.jsonLdLinkTargets = jsonLdLinkTargets != null ? jsonLdLinkTargets : new ArrayList<>();
    }

    public VelocornerMarketplace getVelocorner() {
        return velocorner;
    }

    /**
     * Declarative Shopify crawl target (matches a {@code source.id} row).
     */
    public static class ShopifyTarget {
        private boolean enabled = true;

        /** UUID string, e.g. {@code a0000003-0000-0000-0000-000000000008} */
        private String sourceId;

        private String label;

        private String storefrontBase;

        private List<String> seedUrls = new ArrayList<>();

        private int maxProductsPerRun = 40;

        /** When empty, defaults to EUR only at runtime. */
        private List<String> allowedCurrencies = new ArrayList<>(List.of("EUR"));

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getSourceId() {
            return sourceId;
        }

        public void setSourceId(String sourceId) {
            this.sourceId = sourceId;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

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

        public List<String> getAllowedCurrencies() {
            return allowedCurrencies;
        }

        public void setAllowedCurrencies(List<String> allowedCurrencies) {
            this.allowedCurrencies = allowedCurrencies;
        }

        public Set<String> allowedCurrencySet() {
            if (allowedCurrencies == null || allowedCurrencies.isEmpty()) {
                return Set.of("EUR");
            }
            LinkedHashSet<String> s = new LinkedHashSet<>();
            for (String c : allowedCurrencies) {
                if (c != null && !c.isBlank()) {
                    s.add(c.trim().toUpperCase());
                }
            }
            return s.isEmpty() ? Set.of("EUR") : s;
        }
    }

    /** Regex must match full absolute product URLs discovered on {@link #seedUrls}. */
    public static class JsonLdLinkTarget {
        private boolean enabled = true;
        private String sourceId;
        private String label;
        private List<String> seedUrls = new ArrayList<>();
        private String linkRegex;
        private int maxProductsPerRun = 25;
        private List<String> allowedCurrencies = new ArrayList<>(List.of("EUR", "CHF"));

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getSourceId() {
            return sourceId;
        }

        public void setSourceId(String sourceId) {
            this.sourceId = sourceId;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public List<String> getSeedUrls() {
            return seedUrls;
        }

        public void setSeedUrls(List<String> seedUrls) {
            this.seedUrls = seedUrls;
        }

        public String getLinkRegex() {
            return linkRegex;
        }

        public void setLinkRegex(String linkRegex) {
            this.linkRegex = linkRegex;
        }

        public int getMaxProductsPerRun() {
            return maxProductsPerRun;
        }

        public void setMaxProductsPerRun(int maxProductsPerRun) {
            this.maxProductsPerRun = maxProductsPerRun;
        }

        public List<String> getAllowedCurrencies() {
            return allowedCurrencies;
        }

        public void setAllowedCurrencies(List<String> allowedCurrencies) {
            this.allowedCurrencies = allowedCurrencies;
        }

        public Set<String> allowedCurrencySet() {
            if (allowedCurrencies == null || allowedCurrencies.isEmpty()) {
                return Set.of("EUR", "CHF");
            }
            LinkedHashSet<String> s = new LinkedHashSet<>();
            for (String c : allowedCurrencies) {
                if (c != null && !c.isBlank()) {
                    s.add(c.trim().toUpperCase());
                }
            }
            return s.isEmpty() ? Set.of("EUR", "CHF") : s;
        }
    }

    public static class VelocornerMarketplace {
        private boolean enabled = true;
        private String sourceId = "a0000004-0000-0000-0000-000000000004";
        private String label = "Velocorner CH";
        private List<String> seedUrls =
                new ArrayList<>(
                        List.of(
                                "https://velocorner.ch/en/bicycle-marketplace",
                                "https://velocorner.ch/en/bicycle-marketplace?page=2"));
        private int maxProductsPerRun = 25;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getSourceId() {
            return sourceId;
        }

        public void setSourceId(String sourceId) {
            this.sourceId = sourceId;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
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

    public static class UpwayDe {
        private String storefrontBase = "https://www.upway.de";

        private List<String> seedUrls =
                new ArrayList<>(
                        List.of(
                                "https://www.upway.de/",
                                "https://www.upway.de/collections/all"));

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
