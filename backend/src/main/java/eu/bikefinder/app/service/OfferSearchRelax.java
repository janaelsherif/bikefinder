package eu.bikefinder.app.service;

import eu.bikefinder.app.web.dto.OfferSearchParams;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Relaxes wish-search constraints when no strict matches exist (nearest-alternative behaviour).
 */
public final class OfferSearchRelax {

    private OfferSearchRelax() {}

    public static OfferSearchParams nearMatch(OfferSearchParams strict) {
        OfferSearchParams r = new OfferSearchParams();
        r.setBrand(strict.getBrand());
        r.setModel(strict.getModel());
        r.setBikeCategory(null);
        r.setBikeCondition(null);
        r.setMotorBrand(strict.getMotorBrand());
        r.setMotorPosition(null);
        Integer minWh = strict.getMinBatteryWh();
        if (minWh != null && minWh > 0) {
            r.setMinBatteryWh(Math.max(0, minWh - 150));
        } else {
            r.setMinBatteryWh(null);
        }
        BigDecimal maxChf = strict.getMaxLandedPriceChf();
        if (maxChf != null && maxChf.signum() > 0) {
            r.setMaxLandedPriceChf(
                    maxChf.multiply(new BigDecimal("1.18")).setScale(2, RoundingMode.HALF_UP));
        } else {
            r.setMaxLandedPriceChf(null);
        }
        r.setMinDiscountVsSwissPct(null);
        r.setMaxMileageKm(relaxMileage(strict.getMaxMileageKm()));
        r.setCountryCode(strict.getCountryCode());
        r.setWarrantyPresent(false);
        r.setBargainOnly(false);
        r.setOfferSort(strict.getOfferSort());
        return r;
    }

    private static Integer relaxMileage(Integer maxKm) {
        if (maxKm == null || maxKm < 0) {
            return null;
        }
        return (int) Math.round(maxKm * 1.5) + 500;
    }
}
