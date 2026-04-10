package eu.bikefinder.app.service;

import com.fasterxml.jackson.databind.JsonNode;
import eu.bikefinder.app.web.dto.OfferSearchParams;

import java.math.BigDecimal;

/**
 * Maps persisted JSON (same keys as {@code GET /api/v1/offers} query params) to {@link OfferSearchParams}.
 */
public final class AlertFilterMapper {

    private AlertFilterMapper() {}

    public static OfferSearchParams toParams(JsonNode filter) {
        OfferSearchParams p = new OfferSearchParams();
        if (filter == null || !filter.isObject()) {
            return p;
        }
        p.setBrand(text(filter, "brand"));
        p.setModel(text(filter, "model"));
        p.setBikeCategory(text(filter, "bikeCategory"));
        p.setBikeCondition(text(filter, "bikeCondition"));
        p.setMotorBrand(text(filter, "motorBrand"));
        p.setMotorPosition(text(filter, "motorPosition"));
        p.setMinBatteryWh(intOrNull(filter, "minBatteryWh"));
        p.setMaxLandedPriceChf(decimalOrNull(filter, "maxLandedPriceChf"));
        p.setMinDiscountVsSwissPct(decimalOrNull(filter, "minDiscountVsSwissPct"));
        p.setMaxMileageKm(intOrNull(filter, "maxMileageKm"));
        p.setCountryCode(text(filter, "countryCode"));
        p.setWarrantyPresent(boolOrNull(filter, "warrantyPresent"));
        p.setBargainOnly(boolOrNull(filter, "bargainOnly"));
        return p;
    }

    private static String text(JsonNode root, String field) {
        JsonNode n = root.get(field);
        if (n == null || n.isNull() || !n.isTextual()) {
            return null;
        }
        String s = n.asText().trim();
        return s.isEmpty() ? null : s;
    }

    private static Integer intOrNull(JsonNode root, String field) {
        JsonNode n = root.get(field);
        if (n == null || n.isNull()) {
            return null;
        }
        if (n.isInt() || n.isLong()) {
            return n.intValue();
        }
        if (n.isTextual()) {
            String s = n.asText().trim();
            if (s.isEmpty()) {
                return null;
            }
            return Integer.parseInt(s);
        }
        return null;
    }

    private static BigDecimal decimalOrNull(JsonNode root, String field) {
        JsonNode n = root.get(field);
        if (n == null || n.isNull()) {
            return null;
        }
        if (n.isNumber()) {
            return n.decimalValue();
        }
        if (n.isTextual()) {
            String s = n.asText().trim();
            if (s.isEmpty()) {
                return null;
            }
            return new BigDecimal(s);
        }
        return null;
    }

    private static Boolean boolOrNull(JsonNode root, String field) {
        JsonNode n = root.get(field);
        if (n == null || n.isNull()) {
            return null;
        }
        if (n.isBoolean()) {
            return n.booleanValue();
        }
        if (n.isTextual()) {
            return "true".equalsIgnoreCase(n.asText().trim());
        }
        return null;
    }
}
