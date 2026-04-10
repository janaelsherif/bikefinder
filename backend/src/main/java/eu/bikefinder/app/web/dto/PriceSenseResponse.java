package eu.bikefinder.app.web.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Competitive sell-price recommendation (PriceSense-aligned). Uses on-demand live competitor checks when
 * enabled, otherwise {@code bike_offer} + ECB rate; can fall back from live to database comparables.
 */
public record PriceSenseResponse(
        BigDecimal pMedianChf,
        BigDecimal pP25Chf,
        BigDecimal pP75Chf,
        BigDecimal gradeAdjustedBenchmarkChf,
        BigDecimal pTargetRawChf,
        BigDecimal pFloorChf,
        BigDecimal pRecommendChf,
        BigDecimal maxBuyInChf,
        BigDecimal grossMarginPct,
        boolean marginConflict,
        String marginMessage,
        int nSwissListings,
        int nGermanListings,
        boolean fallbackUsed,
        String confidence,
        BigDecimal eurChfRateUsed,
        BigDecimal swissPremiumFactorApplied,
        BigDecimal importAllowanceChf,
        boolean insufficientData,
        String explanation,
        List<ComparableListingDto> sampleComparables,
        List<LiveProbeRowDto> liveProbes,
        boolean liveBenchmarkUsed) {

    public record ComparableListingDto(
            String sourceName,
            String countryCode,
            String brand,
            String model,
            Integer modelYear,
            String bikeCondition,
            BigDecimal landedPriceChf,
            String sourceUrl) {}
}
