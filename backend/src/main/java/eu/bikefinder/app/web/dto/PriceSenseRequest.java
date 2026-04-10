package eu.bikefinder.app.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * PatrickBike input: bike identity + total buy-in (purchase + refurb + parts) in CHF.
 */
public record PriceSenseRequest(
        @NotBlank @Size(max = 100) String brand,
        @NotBlank @Size(max = 100) String model,
        Integer modelYear,
        @NotNull @Pattern(regexp = "[AaBbCcDd]") String conditionGrade,
        @NotNull @PositiveOrZero BigDecimal buyInCostChf) {}
