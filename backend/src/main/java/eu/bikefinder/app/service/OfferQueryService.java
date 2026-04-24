package eu.bikefinder.app.service;

import eu.bikefinder.app.DiscoveryRegion;
import eu.bikefinder.app.config.SearchProperties;
import eu.bikefinder.app.domain.BikeOffer;
import eu.bikefinder.app.repo.BikeOfferRepository;
import eu.bikefinder.app.repo.spec.BikeOfferSpecs;
import eu.bikefinder.app.web.dto.OfferSearchParams;
import eu.bikefinder.app.web.dto.OfferSummaryDto;
import eu.bikefinder.app.web.dto.WishSearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class OfferQueryService {

    private static final BigDecimal TOP_DEAL_MIN_PCT = new BigDecimal("25");
    private static final BigDecimal TOP_DEAL_MAX_PCT = new BigDecimal("40");

    private static final Set<String> BIKE_CATEGORIES =
            Set.of("city", "trekking", "cargo", "mtb", "road", "gravel", "kids");
    private static final Set<String> BIKE_CONDITIONS =
            Set.of("new", "like_new", "refurbished", "used");
    private static final Set<String> MOTOR_POSITIONS = Set.of("mid", "rear", "front");

    private final BikeOfferRepository bikeOfferRepository;
    private final SearchProperties searchProperties;

    public OfferQueryService(BikeOfferRepository bikeOfferRepository, SearchProperties searchProperties) {
        this.bikeOfferRepository = bikeOfferRepository;
        this.searchProperties = searchProperties;
    }

    @Transactional(readOnly = true)
    public Page<OfferSummaryDto> search(OfferSearchParams params, Pageable pageable) {
        validate(params);
        Specification<BikeOffer> spec = buildSpecification(params, searchProperties.isIncludeDemoListings());
        Pageable sorted = buildPageable(params, pageable);
        return bikeOfferRepository.findAll(spec, sorted).map(this::toDto);
    }

    /**
     * Hamza wish search: strict filters; if no rows and {@link SearchProperties#isNearMatchFallback()}, runs a relaxed
     * query (nearest alternatives).
     */
    /**
     * Alert digest: active offers matching {@code params} with {@code first_seen_at} strictly after {@code sinceExclusive}.
     * Ordered by first seen ascending (then id) so a batch watermark advances predictably.
     */
    @Transactional(readOnly = true)
    public List<OfferAlertBatchItem> searchOffersForAlertSince(
            OfferSearchParams params, Instant sinceExclusive, int limit) {
        validate(params);
        Specification<BikeOffer> spec = buildSpecification(params, searchProperties.isIncludeDemoListings());
        spec = spec.and(BikeOfferSpecs.firstSeenAfter(sinceExclusive));
        Pageable pageable =
                PageRequest.of(
                        0,
                        limit,
                        Sort.by(Sort.Order.asc("firstSeenAt"), Sort.Order.asc("id")));
        return bikeOfferRepository.findAll(spec, pageable).getContent().stream()
                .map(b -> new OfferAlertBatchItem(toDto(b), b.getFirstSeenAt()))
                .sorted(Comparator.comparing(OfferAlertBatchItem::firstSeenAt)
                        .thenComparing(a -> a.offer().id()))
                .toList();
    }

    /**
     * Non-vector “similar” listings: same brand/category/motor family, landed CHF within ~±35% when
     * available, model year ±2 when available. Populates semantic search until embeddings are wired.
     */
    @Transactional(readOnly = true)
    public Page<OfferSummaryDto> searchSimilarOffers(UUID offerId, Pageable pageable) {
        BikeOffer ref =
                bikeOfferRepository
                        .findById(offerId)
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "Offer not found"));
        boolean includeDemo = searchProperties.isIncludeDemoListings();
        Specification<BikeOffer> spec = Specification.where(BikeOfferSpecs.statusActive());
        spec = and(spec, BikeOfferSpecs.demoOffers(includeDemo));
        spec = and(spec, BikeOfferSpecs.idNotEqual(offerId));
        spec = and(spec, BikeOfferSpecs.brandContainsIgnoreCase(ref.getBrand()));
        spec = and(spec, BikeOfferSpecs.modelContainsIgnoreCase(ref.getModel()));
        if (ref.getBikeCategory() != null && !ref.getBikeCategory().isBlank()) {
            spec = spec.and(BikeOfferSpecs.bikeCategoryEquals(ref.getBikeCategory()));
        }
        spec = and(spec, BikeOfferSpecs.motorBrandContainsIgnoreCase(ref.getMotorBrand()));
        if (ref.getLandedPriceChf() != null) {
            BigDecimal r = ref.getLandedPriceChf();
            BigDecimal min = r.multiply(new BigDecimal("0.65")).setScale(2, RoundingMode.HALF_UP);
            BigDecimal max = r.multiply(new BigDecimal("1.35")).setScale(2, RoundingMode.HALF_UP);
            spec = and(spec, BikeOfferSpecs.landedPriceChfBetween(min, max));
        }
        if (ref.getModelYear() != null) {
            int y = ref.getModelYear();
            spec = and(spec, BikeOfferSpecs.modelYearBetweenInclusive(y - 2, y + 2));
        }
        Pageable sorted = similarListingsPageable(pageable);
        return bikeOfferRepository.findAll(spec, sorted).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public WishSearchResponse searchWish(OfferSearchParams params, Pageable pageable) {
        validate(params);
        boolean includeDemo = searchProperties.isIncludeDemoListings();
        Specification<BikeOffer> strictSpec = buildSpecification(params, includeDemo);
        Pageable sorted = buildPageable(params, pageable);
        Page<BikeOffer> exact = bikeOfferRepository.findAll(strictSpec, sorted);
        if (exact.getTotalElements() > 0) {
            return new WishSearchResponse(
                    "EXACT",
                    exact.map(b -> toDto(b, "exact")),
                    Page.empty(sorted));
        }
        if (!searchProperties.isNearMatchFallback()) {
            return new WishSearchResponse("NONE", Page.empty(sorted), Page.empty(sorted));
        }
        OfferSearchParams relaxed = OfferSearchRelax.nearMatch(params);
        validate(relaxed);
        Specification<BikeOffer> nearSpec = buildSpecification(relaxed, includeDemo);
        Page<BikeOffer> near = bikeOfferRepository.findAll(nearSpec, sorted);
        String tier = near.getTotalElements() > 0 ? "NEAR" : "NONE";
        return new WishSearchResponse(tier, Page.empty(sorted), near.map(b -> toDto(b, "near")));
    }

    /**
     * Uses {@link OfferSearchParams#getOfferSort()} ({@code newest}, {@code price_asc}, …) so nested JPA paths avoid
     * Spring’s {@code sort=a,b} comma ambiguity.
     */
    private static Pageable buildPageable(OfferSearchParams params, Pageable pageable) {
        String mode = params.normalizedOfferSort();
        if (mode != null) {
            Sort s = sortFromOfferMode(mode);
            if (s != null) {
                return PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        s.and(Sort.by(Sort.Order.asc("id"))));
            }
        }
        return defaultNewestPageable(pageable);
    }

    private static Sort sortFromOfferMode(String mode) {
        return switch (mode) {
            case "newest" -> Sort.by(Sort.Order.desc("firstSeenAt"));
            case "price_asc" -> Sort.by(Sort.Order.asc("landedPriceChf"));
            case "price_desc" -> Sort.by(Sort.Order.desc("landedPriceChf"));
            case "country_asc" -> Sort.by(Sort.Order.asc("source.countryCode"));
            case "country_desc" -> Sort.by(Sort.Order.desc("source.countryCode"));
            default -> null;
        };
    }

    private static Pageable defaultNewestPageable(Pageable pageable) {
        return PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Order.desc("firstSeenAt"), Sort.Order.asc("id")));
    }

    private static Pageable similarListingsPageable(Pageable pageable) {
        return defaultNewestPageable(pageable);
    }

    private void validate(OfferSearchParams p) {
        if (p.getBikeCategory() != null && !p.getBikeCategory().isBlank()) {
            String c = p.getBikeCategory().trim().toLowerCase();
            if (!BIKE_CATEGORIES.contains(c)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "bikeCategory must be one of: " + String.join(", ", BIKE_CATEGORIES));
            }
        }
        if (p.getBikeCondition() != null && !p.getBikeCondition().isBlank()) {
            String c = p.getBikeCondition().trim().toLowerCase();
            if (!BIKE_CONDITIONS.contains(c)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "bikeCondition must be one of: " + String.join(", ", BIKE_CONDITIONS));
            }
        }
        if (p.getMotorPosition() != null && !p.getMotorPosition().isBlank()) {
            String m = p.getMotorPosition().trim().toLowerCase();
            if (!MOTOR_POSITIONS.contains(m)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "motorPosition must be one of: " + String.join(", ", MOTOR_POSITIONS));
            }
        }
        if (p.getCountryCode() != null && !p.getCountryCode().isBlank()) {
            if (p.getCountryCode().trim().length() != 2) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "countryCode must be ISO-2");
            }
        }
    }

    private Specification<BikeOffer> buildSpecification(OfferSearchParams p, boolean includeDemoListings) {
        Specification<BikeOffer> spec = Specification.where(BikeOfferSpecs.statusActive());
        spec = and(spec, BikeOfferSpecs.demoOffers(includeDemoListings));
        spec = and(spec, BikeOfferSpecs.brandContainsIgnoreCase(p.getBrand()));
        spec = and(spec, BikeOfferSpecs.modelContainsIgnoreCase(p.getModel()));
        if (p.getBikeCategory() != null && !p.getBikeCategory().isBlank()) {
            spec = spec.and(BikeOfferSpecs.bikeCategoryEquals(p.getBikeCategory().trim().toLowerCase()));
        }
        if (p.getBikeCondition() != null && !p.getBikeCondition().isBlank()) {
            spec = spec.and(BikeOfferSpecs.bikeConditionEquals(p.getBikeCondition().trim().toLowerCase()));
        }
        spec = and(spec, BikeOfferSpecs.motorBrandContainsIgnoreCase(p.getMotorBrand()));
        if (p.getMotorPosition() != null && !p.getMotorPosition().isBlank()) {
            spec = spec.and(BikeOfferSpecs.motorPositionEquals(p.getMotorPosition().trim().toLowerCase()));
        }
        spec = and(spec, BikeOfferSpecs.batteryWhAtLeast(p.getMinBatteryWh()));
        spec = and(spec, BikeOfferSpecs.landedPriceChfAtMost(p.getMaxLandedPriceChf()));
        spec = and(spec, BikeOfferSpecs.discountVsSwissAtLeast(p.getMinDiscountVsSwissPct()));
        spec = and(spec, BikeOfferSpecs.mileageKmAtMost(p.getMaxMileageKm()));
        if (Boolean.TRUE.equals(p.getNearbyMarkets())) {
            spec = spec.and(BikeOfferSpecs.sourceCountryCodeIn(DiscoveryRegion.NEAR_SWITZERLAND_ISO2));
        } else {
            spec = and(spec, BikeOfferSpecs.sourceCountryCode(p.getCountryCode()));
        }
        if (Boolean.TRUE.equals(p.getWarrantyPresent())) {
            spec = spec.and(BikeOfferSpecs.warrantyPresent());
        }
        if (Boolean.TRUE.equals(p.getBargainOnly())) {
            spec = spec.and(BikeOfferSpecs.bargainOnly());
        }
        return spec;
    }

    private static Specification<BikeOffer> and(
            Specification<BikeOffer> base, Specification<BikeOffer> part) {
        if (part == null) {
            return base;
        }
        return base.and(part);
    }

    private OfferSummaryDto toDto(BikeOffer b) {
        return toDto(b, null);
    }

    private OfferSummaryDto toDto(BikeOffer b, String matchTier) {
        var src = b.getSource();
        BigDecimal disc = b.getDiscountVsSwissPct();
        boolean topDeal = isTopDeal(b, disc);
        String imageUrl = null;
        if (b.getImages() != null && b.getImages().length > 0) {
            imageUrl = b.getImages()[0];
        }
        return new OfferSummaryDto(
                b.getId(),
                src.getName(),
                src.getCountryCode(),
                src.getType(),
                b.getBrand(),
                b.getModel(),
                b.getModelYear(),
                b.getBikeCategory(),
                b.getBikeCondition(),
                b.getMotorBrand(),
                b.getBatteryWh(),
                b.getMileageKm(),
                b.getWarrantyType(),
                b.getWarrantyMonths(),
                b.getLandedPriceChf(),
                disc,
                b.isBargain(),
                topDeal,
                b.getQualityScore(),
                imageUrl,
                b.getSourceUrl(),
                matchTier);
    }

    private boolean isTopDeal(BikeOffer b, BigDecimal discountPct) {
        if (discountPct == null) {
            return false;
        }
        if (discountPct.compareTo(TOP_DEAL_MIN_PCT) < 0 || discountPct.compareTo(TOP_DEAL_MAX_PCT) > 0) {
            return false;
        }
        String wt = b.getWarrantyType();
        if (wt == null || "none".equalsIgnoreCase(wt)) {
            return false;
        }
        Integer km = b.getMileageKm();
        return km != null && km < 3000;
    }
}
