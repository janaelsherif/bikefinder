package eu.bikefinder.app.repo.spec;

import eu.bikefinder.app.domain.BikeOffer;
import eu.bikefinder.app.domain.Source;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public final class BikeOfferSpecs {

    private BikeOfferSpecs() {}

    public static Specification<BikeOffer> statusActive() {
        return (root, query, cb) -> cb.equal(root.get("status"), "active");
    }

    public static Specification<BikeOffer> brandContainsIgnoreCase(String brand) {
        if (brand == null || brand.isBlank()) {
            return null;
        }
        String pattern = "%" + brand.trim().toLowerCase() + "%";
        return (root, query, cb) -> cb.like(cb.lower(root.get("brand")), pattern);
    }

    public static Specification<BikeOffer> modelContainsIgnoreCase(String model) {
        if (model == null || model.isBlank()) {
            return null;
        }
        String pattern = "%" + model.trim().toLowerCase() + "%";
        return (root, query, cb) -> cb.like(cb.lower(root.get("model")), pattern);
    }

    public static Specification<BikeOffer> bikeCategoryEquals(String category) {
        if (category == null || category.isBlank()) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("bikeCategory"), category.trim().toLowerCase());
    }

    public static Specification<BikeOffer> bikeConditionEquals(String condition) {
        if (condition == null || condition.isBlank()) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("bikeCondition"), condition.trim().toLowerCase());
    }

    public static Specification<BikeOffer> motorBrandContainsIgnoreCase(String motorBrand) {
        if (motorBrand == null || motorBrand.isBlank()) {
            return null;
        }
        String pattern = "%" + motorBrand.trim().toLowerCase() + "%";
        return (root, query, cb) -> cb.like(cb.lower(root.get("motorBrand")), pattern);
    }

    public static Specification<BikeOffer> motorPositionEquals(String position) {
        if (position == null || position.isBlank()) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("motorPosition"), position.trim().toLowerCase());
    }

    public static Specification<BikeOffer> batteryWhAtLeast(Integer minWh) {
        if (minWh == null || minWh <= 0) {
            return null;
        }
        return (root, query, cb) ->
                cb.and(
                        cb.isNotNull(root.get("batteryWh")),
                        cb.greaterThanOrEqualTo(root.get("batteryWh"), minWh));
    }

    public static Specification<BikeOffer> landedPriceChfAtMost(BigDecimal maxChf) {
        if (maxChf == null || maxChf.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }
        return (root, query, cb) ->
                cb.and(
                        cb.isNotNull(root.get("landedPriceChf")),
                        cb.lessThanOrEqualTo(root.get("landedPriceChf"), maxChf));
    }

    public static Specification<BikeOffer> discountVsSwissAtLeast(BigDecimal minPct) {
        if (minPct == null) {
            return null;
        }
        return (root, query, cb) ->
                cb.and(
                        cb.isNotNull(root.get("discountVsSwissPct")),
                        cb.greaterThanOrEqualTo(root.get("discountVsSwissPct"), minPct));
    }

    public static Specification<BikeOffer> mileageKmAtMost(Integer maxKm) {
        if (maxKm == null || maxKm < 0) {
            return null;
        }
        return (root, query, cb) ->
                cb.and(
                        cb.isNotNull(root.get("mileageKm")),
                        cb.lessThanOrEqualTo(root.get("mileageKm"), maxKm));
    }

    public static Specification<BikeOffer> sourceCountryCode(String iso2) {
        if (iso2 == null || iso2.length() != 2) {
            return null;
        }
        String cc = iso2.trim().toUpperCase();
        return (root, query, cb) -> {
            Join<BikeOffer, Source> src = root.join("source", JoinType.INNER);
            return cb.equal(src.get("countryCode"), cc);
        };
    }

    public static Specification<BikeOffer> warrantyPresent() {
        return (root, query, cb) -> cb.notEqual(root.get("warrantyType"), "none");
    }

    public static Specification<BikeOffer> bargainOnly() {
        return (root, query, cb) -> cb.isTrue(root.get("bargain"));
    }

    /** When {@code includeDemo} is false, exclude Flyway demo / placeholder offers. */
    public static Specification<BikeOffer> demoOffers(boolean includeDemo) {
        if (includeDemo) {
            return null;
        }
        return (root, query, cb) -> cb.isFalse(root.get("demo"));
    }

    public static Specification<BikeOffer> idNotEqual(UUID excludeId) {
        if (excludeId == null) {
            return null;
        }
        return (root, query, cb) -> cb.notEqual(root.get("id"), excludeId);
    }

    /** Landed CHF in [min, max] (both inclusive); requires non-null landed price on row. */
    public static Specification<BikeOffer> landedPriceChfBetween(BigDecimal min, BigDecimal max) {
        if (min == null || max == null || min.compareTo(max) > 0) {
            return null;
        }
        return (root, query, cb) ->
                cb.and(
                        cb.isNotNull(root.get("landedPriceChf")),
                        cb.between(root.get("landedPriceChf"), min, max));
    }

    public static Specification<BikeOffer> modelYearBetweenInclusive(Integer lo, Integer hi) {
        if (lo == null || hi == null || lo > hi) {
            return null;
        }
        return (root, query, cb) ->
                cb.and(
                        cb.isNotNull(root.get("modelYear")),
                        cb.between(root.get("modelYear"), lo, hi));
    }

    /** Strictly after the subscription watermark (digest cursor). */
    public static Specification<BikeOffer> firstSeenAfter(Instant sinceExclusive) {
        if (sinceExclusive == null) {
            return null;
        }
        return (root, query, cb) -> cb.greaterThan(root.get("firstSeenAt"), sinceExclusive);
    }
}
