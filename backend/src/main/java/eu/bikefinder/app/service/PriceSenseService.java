package eu.bikefinder.app.service;

import eu.bikefinder.app.config.PriceSenseProperties;
import eu.bikefinder.app.domain.BikeOffer;
import eu.bikefinder.app.domain.PriceSenseQuery;
import eu.bikefinder.app.domain.Source;
import eu.bikefinder.app.repo.BikeOfferRepository;
import eu.bikefinder.app.repo.FxRateRepository;
import eu.bikefinder.app.repo.PriceSenseQueryRepository;
import eu.bikefinder.app.service.fx.EcbEurChfIngestionService;
import eu.bikefinder.app.service.pricesense.live.PriceSenseLiveProbeService;
import eu.bikefinder.app.web.dto.LiveProbeRowDto;
import eu.bikefinder.app.web.dto.PriceSenseRequest;
import eu.bikefinder.app.web.dto.PriceSenseResponse;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * PriceSense-aligned competitive sell pricing: optional on-demand live competitor checks, then {@code
 * bike_offer} comparables + ECB EUR/CHF.
 */
@Service
public class PriceSenseService {

    private static final MathContext MC = new MathContext(12, RoundingMode.HALF_UP);

    private final PriceSenseProperties props;
    private final BikeOfferRepository bikeOfferRepository;
    private final FxRateRepository fxRateRepository;
    private final PriceSenseQueryRepository auditRepository;
    private final PriceSenseLiveProbeService liveProbeService;

    public PriceSenseService(
            PriceSenseProperties props,
            BikeOfferRepository bikeOfferRepository,
            FxRateRepository fxRateRepository,
            PriceSenseQueryRepository auditRepository,
            PriceSenseLiveProbeService liveProbeService) {
        this.props = props;
        this.bikeOfferRepository = bikeOfferRepository;
        this.fxRateRepository = fxRateRepository;
        this.auditRepository = auditRepository;
        this.liveProbeService = liveProbeService;
    }

    @Transactional
    public PriceSenseResponse recommend(PriceSenseRequest request) {
        char grade = Character.toUpperCase(request.conditionGrade().charAt(0));
        BigDecimal gradeFactor = gradeFactor(grade);
        BigDecimal buyIn = request.buyInCostChf().setScale(2, RoundingMode.HALF_UP);
        BigDecimal pFloor = buyIn.multiply(props.getMarginFloorMultiplier(), MC).setScale(2, RoundingMode.HALF_UP);

        BigDecimal eurChf =
                fxRateRepository
                        .findTopByCurrencyPairOrderByEffectiveDateDesc(EcbEurChfIngestionService.PAIR_EUR_CHF)
                        .map(r -> r.getRate().setScale(6, RoundingMode.HALF_UP))
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.SERVICE_UNAVAILABLE,
                                                "EUR/CHF rate missing — run ECB ingest or POST /api/v1/system/fx/refresh"));

        List<LiveProbeRowDto> liveProbes = liveProbeService.probeAll(request, eurChf);
        List<BigDecimal> liveChfPrices = PriceSenseLiveProbeService.successfulChfPrices(liveProbes);
        boolean useLiveBenchmark =
                props.getLiveCompetitorSearch().isEnabled()
                        && liveChfPrices.size()
                                >= props.getLiveCompetitorSearch().getMinSuccessfulPrices();

        List<BikeOffer> comparables =
                bikeOfferRepository.findAll(
                        comparableSpec(
                                request.brand(),
                                request.model(),
                                request.modelYear(),
                                props.getYearTolerance(),
                                conditionTiersForGrade(grade)));

        List<BikeOffer> swiss =
                comparables.stream()
                        .filter(o -> "CH".equalsIgnoreCase(o.getSource().getCountryCode()))
                        .toList();
        List<BikeOffer> german =
                comparables.stream()
                        .filter(o -> "DE".equalsIgnoreCase(o.getSource().getCountryCode()))
                        .toList();
        int nChWithPrice =
                (int) swiss.stream().filter(o -> o.getLandedPriceChf() != null).count();
        int nDeWithPrice =
                (int)
                        german.stream()
                                .filter(
                                        o ->
                                                o.getLandedPriceChf() != null
                                                        || (o.getListPriceLocal() != null
                                                                && "EUR".equalsIgnoreCase(
                                                                        o.getCurrencyCode())))
                                .count();

        boolean fallbackUsed;
        BigDecimal benchmarkMedian;
        List<BigDecimal> distributionForPercentiles;

        if (useLiveBenchmark) {
            distributionForPercentiles = new ArrayList<>(liveChfPrices);
            benchmarkMedian = median(distributionForPercentiles);
            fallbackUsed = false;
        } else {
            fallbackUsed = nChWithPrice < props.getSwissMinListings();
            if (!fallbackUsed) {
                distributionForPercentiles =
                        swiss.stream()
                                .map(BikeOffer::getLandedPriceChf)
                                .filter(Objects::nonNull)
                                .sorted()
                                .toList();
                if (distributionForPercentiles.isEmpty()) {
                    benchmarkMedian = null;
                } else {
                    benchmarkMedian = median(distributionForPercentiles);
                }
            } else {
                distributionForPercentiles = germanDeChfEstimates(german, eurChf);
                if (distributionForPercentiles.isEmpty()) {
                    distributionForPercentiles =
                            comparables.stream()
                                    .map(BikeOffer::getLandedPriceChf)
                                    .filter(Objects::nonNull)
                                    .sorted()
                                    .toList();
                    fallbackUsed = true;
                }
                benchmarkMedian =
                        distributionForPercentiles.isEmpty() ? null : median(distributionForPercentiles);
            }
        }

        if (benchmarkMedian == null) {
            String baseMsg =
                    "No benchmark price: ";
            String tail =
                    props.getLiveCompetitorSearch().isEnabled() && !liveChfPrices.isEmpty()
                            ? "live checks returned too few usable CHF prices for the configured minimum; "
                            : props.getLiveCompetitorSearch().isEnabled()
                                    ? "live competitor checks found no prices; "
                                    : "";
            PriceSenseResponse resp =
                    new PriceSenseResponse(
                            null,
                            null,
                            null,
                            null,
                            null,
                            pFloor,
                            null,
                            null,
                            null,
                            false,
                            null,
                            nChWithPrice,
                            nDeWithPrice,
                            fallbackUsed,
                            "LOW",
                            eurChf,
                            props.getSwissPremiumFactor(),
                            props.getImportAllowanceChf(),
                            true,
                            baseMsg
                                    + tail
                                    + "no comparable landed prices in the database for this brand/model/year window. "
                                    + "Import more listings (e.g. Rebike crawl), enable live search, or widen search.",
                            List.of(),
                            liveProbes,
                            false);
            saveAudit(request, grade, buyIn, pFloor, resp, eurChf);
            return resp;
        }

        BigDecimal pP25 = percentile(distributionForPercentiles, 25);
        BigDecimal pP75 = percentile(distributionForPercentiles, 75);
        BigDecimal adjustedBenchmark = benchmarkMedian.multiply(gradeFactor, MC).setScale(2, RoundingMode.HALF_UP);
        BigDecimal pTargetRaw =
                adjustedBenchmark
                        .multiply(props.getCompetitorDiscountMultiplier(), MC)
                        .setScale(2, RoundingMode.HALF_UP);
        boolean marginConflict = pTargetRaw.compareTo(pFloor) < 0;
        BigDecimal pRecommend = pTargetRaw.max(pFloor);
        BigDecimal maxBuyIn =
                pRecommend.divide(props.getMarginFloorMultiplier(), 2, RoundingMode.HALF_UP);
        BigDecimal marginPct =
                buyIn.signum() > 0
                        ? pRecommend
                                .subtract(buyIn)
                                .divide(buyIn, 4, RoundingMode.HALF_UP)
                                .multiply(BigDecimal.valueOf(100), MC)
                                .setScale(2, RoundingMode.HALF_UP)
                        : null;

        String confidence =
                useLiveBenchmark
                        ? confidenceLabelLive(liveChfPrices.size())
                        : confidenceLabel(
                                distributionForPercentiles.size(), nChWithPrice, fallbackUsed);
        String marginMsg =
                marginConflict
                        ? "10% unter dem Referenzwert ("
                                + pTargetRaw
                                + " CHF) liegt unter Ihrer 30%-Marge auf dem Einkauf ("
                                + pFloor
                                + " CHF). Empfehlung: Mindestpreis "
                                + pFloor
                                + " CHF — oder Einkauf prüfen."
                        : null;

        String explanation =
                useLiveBenchmark
                        ? ("Referenz: Median aus "
                                + liveChfPrices.size()
                                + " Live-Preis(checks) auf konfigurierten CH-Konkurrenz-Shops (JSON-LD), "
                                + "Zustandsstufe "
                                + grade
                                + " Faktor "
                                + gradeFactor
                                + ", dann "
                                + props.getCompetitorDiscountMultiplier()
                                        .multiply(BigDecimal.valueOf(100))
                                        .stripTrailingZeros()
                                + "% vom Referenzwert. ")
                        : (fallbackUsed
                                        ? "CH-Stichprobe < "
                                                + props.getSwissMinListings()
                                                + ": Referenz aus DE-Angeboten (EUR×EUR/CHF×F_CH+A pro Listing, Median). "
                                        : "Referenz aus CH-Angeboten (Landpreis CHF), Median. ")
                                + (props.getLiveCompetitorSearch().isEnabled() && !useLiveBenchmark
                                        ? "Live-Konkurrenz-Checks lieferten nicht genug Preise — Benchmark aus Datenbank. "
                                        : "")
                                + "Benchmark nach Zustandsstufe "
                                + grade
                                + " mit Faktor "
                                + gradeFactor
                                + ", dann "
                                + props.getCompetitorDiscountMultiplier()
                                        .multiply(BigDecimal.valueOf(100))
                                        .stripTrailingZeros()
                                + "% vom Referenzwert.";

        List<PriceSenseResponse.ComparableListingDto> samples =
                comparables.stream()
                        .limit(5)
                        .map(
                                o ->
                                        new PriceSenseResponse.ComparableListingDto(
                                                o.getSource().getName(),
                                                o.getSource().getCountryCode(),
                                                o.getBrand(),
                                                o.getModel(),
                                                o.getModelYear(),
                                                o.getBikeCondition(),
                                                o.getLandedPriceChf(),
                                                o.getSourceUrl()))
                        .toList();

        PriceSenseResponse resp =
                new PriceSenseResponse(
                        benchmarkMedian.setScale(2, RoundingMode.HALF_UP),
                        pP25,
                        pP75,
                        adjustedBenchmark,
                        pTargetRaw,
                        pFloor,
                        pRecommend,
                        maxBuyIn,
                        marginPct,
                        marginConflict,
                        marginMsg,
                        nChWithPrice,
                        nDeWithPrice,
                        fallbackUsed,
                        confidence,
                        eurChf,
                        props.getSwissPremiumFactor(),
                        props.getImportAllowanceChf(),
                        false,
                        explanation,
                        samples,
                        liveProbes,
                        useLiveBenchmark);

        saveAudit(request, grade, buyIn, pFloor, resp, eurChf);
        return resp;
    }

    private void saveAudit(
            PriceSenseRequest request,
            char grade,
            BigDecimal buyIn,
            BigDecimal pFloor,
            PriceSenseResponse resp,
            BigDecimal eurChf) {
        var row = new PriceSenseQuery();
        row.setBrand(request.brand().trim());
        row.setModel(request.model().trim());
        row.setModelYear(request.modelYear());
        row.setConditionGrade(String.valueOf(grade));
        row.setBuyinCostChf(buyIn);
        row.setNCh(resp.nSwissListings());
        row.setNDe(resp.nGermanListings());
        row.setPMedianChf(resp.pMedianChf());
        row.setPP25Chf(resp.pP25Chf());
        row.setPP75Chf(resp.pP75Chf());
        row.setPTargetRawChf(resp.pTargetRawChf());
        row.setPFloorChf(pFloor);
        row.setPRecommendChf(resp.pRecommendChf());
        row.setGrossMarginPct(resp.grossMarginPct());
        row.setFallbackUsed(resp.fallbackUsed());
        row.setFChApplied(resp.swissPremiumFactorApplied());
        row.setEurChfRate(eurChf);
        row.setConfidence(resp.confidence());
        row.setMarginConflict(resp.marginConflict());
        row.setNotes(resp.insufficientData() ? resp.explanation() : null);
        auditRepository.save(row);
    }

    private static String confidenceLabelLive(int liveN) {
        if (liveN >= 5) {
            return "HIGH";
        }
        if (liveN >= 2) {
            return "MEDIUM";
        }
        return "LOW";
    }

    private static String confidenceLabel(int benchmarkSampleSize, int nCh, boolean fallback) {
        int n = benchmarkSampleSize;
        if (fallback) {
            return n >= 5 ? "MEDIUM" : "LOW";
        }
        if (nCh >= 10 && n >= 10) {
            return "HIGH";
        }
        if (n >= 5) {
            return "MEDIUM";
        }
        return "LOW";
    }

    private List<BigDecimal> germanDeChfEstimates(List<BikeOffer> german, BigDecimal eurChf) {
        BigDecimal fCh = props.getSwissPremiumFactor();
        BigDecimal a = props.getImportAllowanceChf();
        List<BigDecimal> out = new ArrayList<>();
        for (BikeOffer o : german) {
            if (o.getListPriceLocal() != null && "EUR".equalsIgnoreCase(o.getCurrencyCode())) {
                BigDecimal pch =
                        o.getListPriceLocal()
                                .multiply(eurChf, MC)
                                .multiply(fCh, MC)
                                .add(a, MC)
                                .setScale(2, RoundingMode.HALF_UP);
                out.add(pch);
            } else if (o.getLandedPriceChf() != null) {
                out.add(o.getLandedPriceChf());
            }
        }
        out.sort(Comparator.naturalOrder());
        return out;
    }

    private static BigDecimal gradeFactor(char grade) {
        return switch (grade) {
            case 'A' -> new BigDecimal("1.12");
            case 'B' -> BigDecimal.ONE;
            case 'C' -> new BigDecimal("0.85");
            case 'D' -> new BigDecimal("0.65");
            default -> BigDecimal.ONE;
        };
    }

    /** PriceSense A–D → {@code bike_condition} values for SQL IN. */
    private static List<String> conditionTiersForGrade(char grade) {
        return switch (grade) {
            case 'A' -> List.of("new", "like_new");
            case 'B' -> List.of("new", "like_new", "refurbished", "used");
            case 'C' -> List.of("used", "refurbished");
            case 'D' -> List.of("used");
            default -> List.of("used", "like_new", "refurbished", "new");
        };
    }

    private Specification<BikeOffer> comparableSpec(
            String brand, String model, Integer year, int yearTol, List<String> conditions) {
        return (root, query, cb) -> {
            Join<BikeOffer, Source> src = root.join("source", JoinType.INNER);
            List<Predicate> parts = new ArrayList<>();
            parts.add(cb.equal(root.get("status"), "active"));
            parts.add(cb.isFalse(root.get("demo")));
            String b = brand.trim().toLowerCase(Locale.ROOT);
            String m = model.trim().toLowerCase(Locale.ROOT);
            parts.add(cb.like(cb.lower(root.get("brand")), "%" + b + "%"));
            parts.add(cb.like(cb.lower(root.get("model")), "%" + m + "%"));
            if (year != null) {
                int lo = year - yearTol;
                int hi = year + yearTol;
                parts.add(cb.between(root.get("modelYear"), lo, hi));
            }
            parts.add(root.get("bikeCondition").in(conditions));
            parts.add(
                    cb.or(
                            cb.isNotNull(root.get("landedPriceChf")),
                            cb.and(
                                    cb.equal(cb.upper(root.get("currencyCode")), "EUR"),
                                    cb.isNotNull(root.get("listPriceLocal")))));
            query.distinct(true);
            return cb.and(parts.toArray(Predicate[]::new));
        };
    }

    private static BigDecimal median(List<BigDecimal> sorted) {
        if (sorted.isEmpty()) {
            return null;
        }
        List<BigDecimal> s = sorted.stream().sorted().collect(Collectors.toList());
        int n = s.size();
        if (n % 2 == 1) {
            return s.get(n / 2).setScale(2, RoundingMode.HALF_UP);
        }
        return s.get(n / 2 - 1)
                .add(s.get(n / 2))
                .divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
    }

    private static BigDecimal percentile(List<BigDecimal> sortedAsc, int p) {
        if (sortedAsc.isEmpty()) {
            return null;
        }
        int n = sortedAsc.size();
        if (n == 1) {
            return sortedAsc.getFirst().setScale(2, RoundingMode.HALF_UP);
        }
        double rank = (n - 1) * (p / 100.0);
        int lo = (int) Math.floor(rank);
        int hi = (int) Math.ceil(rank);
        if (lo == hi) {
            return sortedAsc.get(lo).setScale(2, RoundingMode.HALF_UP);
        }
        BigDecimal w = BigDecimal.valueOf(rank - lo);
        return sortedAsc
                .get(lo)
                .multiply(BigDecimal.ONE.subtract(w), MC)
                .add(sortedAsc.get(hi).multiply(w, MC), MC)
                .setScale(2, RoundingMode.HALF_UP);
    }
}
