package eu.bikefinder.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;

/**
 * PatrickBike PriceSense-style competitive pricing (see product spec: median −10%, floor at 30% margin on
 * buy-in, CH vs DE fallback).
 */
@ConfigurationProperties(prefix = "ebf.pricesense")
public class PriceSenseProperties {

    /** Minimum Swiss (CH) listings to use Swiss median path (else DE fallback). */
    private int swissMinListings = 5;

    /** Multiply market benchmark by this for recommended list price before floor (0.90 = 10% below median). */
    private BigDecimal competitorDiscountMultiplier = new BigDecimal("0.90");

    /** Minimum sell price vs buy-in (1.30 = 30% gross margin floor on cost). */
    private BigDecimal marginFloorMultiplier = new BigDecimal("1.30");

    /** Switzerland premium on German EUR median: P_CH ≈ P_DE_eur × eurChf × fCh + importAllowanceChf. */
    private BigDecimal swissPremiumFactor = new BigDecimal("1.20");

    /** Added after EUR→CHF × F_CH (self-pickup vs delivery — configurable). */
    private BigDecimal importAllowanceChf = new BigDecimal("80");

    /** Model year ± tolerance when matching {@code bike_offer}. */
    private int yearTolerance = 1;

    /** On-demand live checks on competitor shop fronts (Shopify-style search + JSON-LD). */
    private LiveCompetitorSearch liveCompetitorSearch = new LiveCompetitorSearch();

    public static class LiveCompetitorSearch {
        /**
         * When true, each recommendation first probes active {@code competitor_watch_target} rows (parallel,
         * robots.txt respected). Default false until ops enable (load on competitor sites).
         */
        private boolean enabled = false;

        /** Per-probe future timeout (each target runs in parallel). */
        private int timeoutSeconds = 18;

        /** Minimum successful live CHF prices to use live median as benchmark (else fall back to DB). */
        private int minSuccessfulPrices = 1;

        /** Delay before each HTTP step inside a probe (search + product); politeness. */
        private int delayMsBetweenRequests = 200;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public int getTimeoutSeconds() {
            return timeoutSeconds;
        }

        public void setTimeoutSeconds(int timeoutSeconds) {
            this.timeoutSeconds = timeoutSeconds;
        }

        public int getMinSuccessfulPrices() {
            return minSuccessfulPrices;
        }

        public void setMinSuccessfulPrices(int minSuccessfulPrices) {
            this.minSuccessfulPrices = minSuccessfulPrices;
        }

        public int getDelayMsBetweenRequests() {
            return delayMsBetweenRequests;
        }

        public void setDelayMsBetweenRequests(int delayMsBetweenRequests) {
            this.delayMsBetweenRequests = delayMsBetweenRequests;
        }
    }

    public LiveCompetitorSearch getLiveCompetitorSearch() {
        return liveCompetitorSearch;
    }

    public void setLiveCompetitorSearch(LiveCompetitorSearch liveCompetitorSearch) {
        this.liveCompetitorSearch = liveCompetitorSearch;
    }

    public int getSwissMinListings() {
        return swissMinListings;
    }

    public void setSwissMinListings(int swissMinListings) {
        this.swissMinListings = swissMinListings;
    }

    public BigDecimal getCompetitorDiscountMultiplier() {
        return competitorDiscountMultiplier;
    }

    public void setCompetitorDiscountMultiplier(BigDecimal competitorDiscountMultiplier) {
        this.competitorDiscountMultiplier = competitorDiscountMultiplier;
    }

    public BigDecimal getMarginFloorMultiplier() {
        return marginFloorMultiplier;
    }

    public void setMarginFloorMultiplier(BigDecimal marginFloorMultiplier) {
        this.marginFloorMultiplier = marginFloorMultiplier;
    }

    public BigDecimal getSwissPremiumFactor() {
        return swissPremiumFactor;
    }

    public void setSwissPremiumFactor(BigDecimal swissPremiumFactor) {
        this.swissPremiumFactor = swissPremiumFactor;
    }

    public BigDecimal getImportAllowanceChf() {
        return importAllowanceChf;
    }

    public void setImportAllowanceChf(BigDecimal importAllowanceChf) {
        this.importAllowanceChf = importAllowanceChf;
    }

    public int getYearTolerance() {
        return yearTolerance;
    }

    public void setYearTolerance(int yearTolerance) {
        this.yearTolerance = yearTolerance;
    }
}
