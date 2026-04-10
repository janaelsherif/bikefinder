package eu.bikefinder.app.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record OfferImportRow(
        @NotBlank String sourceOfferId,
        @NotBlank String sourceUrl,
        String brand,
        String model,
        Integer modelYear,
        String bikeCategory,
        @NotBlank String bikeCondition,
        String motorBrand,
        String motorPosition,
        Integer batteryWh,
        Integer mileageKm,
        String warrantyType,
        Integer warrantyMonths,
        @NotNull BigDecimal totalPriceLocal,
        String currencyCode,
        String extractionMethod,
        String[] images,
        BigDecimal qualityScore) {}
