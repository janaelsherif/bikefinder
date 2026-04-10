package eu.bikefinder.app.service.pricing;

import eu.bikefinder.app.config.PricingProperties;
import eu.bikefinder.app.domain.BikeOffer;
import eu.bikefinder.app.repo.BikeOfferRepository;
import eu.bikefinder.app.repo.FxRateRepository;
import eu.bikefinder.app.repo.SwissPriceReferenceRepository;
import eu.bikefinder.app.service.fx.EcbEurChfIngestionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Service
public class PricingService {

    private final FxRateRepository fxRateRepository;
    private final SwissPriceReferenceRepository swissPriceReferenceRepository;
    private final BikeOfferRepository bikeOfferRepository;
    private final PricingProperties pricingProperties;
    private final EcbEurChfIngestionService ecbEurChfIngestionService;

    public PricingService(
            FxRateRepository fxRateRepository,
            SwissPriceReferenceRepository swissPriceReferenceRepository,
            BikeOfferRepository bikeOfferRepository,
            PricingProperties pricingProperties,
            EcbEurChfIngestionService ecbEurChfIngestionService) {
        this.fxRateRepository = fxRateRepository;
        this.swissPriceReferenceRepository = swissPriceReferenceRepository;
        this.bikeOfferRepository = bikeOfferRepository;
        this.pricingProperties = pricingProperties;
        this.ecbEurChfIngestionService = ecbEurChfIngestionService;
    }

    /**
     * Dev spec §6.2: landed CHF = EUR product in CHF + shipping estimate + import surcharge estimate.
     */
    @Transactional
    public void repriceOffer(BikeOffer offer) {
        var eurChf = fxRateRepository.findTopByCurrencyPairOrderByEffectiveDateDesc(EcbEurChfIngestionService.PAIR_EUR_CHF)
                .orElse(null);
        if (eurChf == null) {
            return;
        }
        BigDecimal eurAmount = offer.getTotalPriceLocal();
        if (eurAmount == null || eurAmount.signum() <= 0) {
            return;
        }
        if (!"EUR".equalsIgnoreCase(offer.getCurrencyCode())) {
            return;
        }

        BigDecimal rate = eurChf.getRate();
        BigDecimal priceChf = eurAmount.multiply(rate).setScale(2, RoundingMode.HALF_UP);

        String country = offer.getSource().getCountryCode();
        BigDecimal shipping = resolveShipping(country);

        BigDecimal threshold = pricingProperties.getImportVatThresholdChf();
        BigDecimal over = priceChf.subtract(threshold);
        BigDecimal vatPart = over.signum() > 0
                ? over.multiply(pricingProperties.getImportVatRate()).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        BigDecimal importSurcharge = vatPart.add(pricingProperties.getCustomsHandlingChf());

        BigDecimal landed = priceChf.add(shipping).add(importSurcharge).setScale(2, RoundingMode.HALF_UP);

        String tier = inferSpecTier(offer.getBatteryWh());
        var swissOpt = swissPriceReferenceRepository.findByBrandAndBikeCategoryAndSpecTier(
                offer.getBrand(),
                offer.getBikeCategory(),
                tier);
        if (swissOpt.isEmpty() && offer.getBrand() != null) {
            swissOpt = swissPriceReferenceRepository.findByBrandAndBikeCategoryAndSpecTier(
                    offer.getBrand(),
                    offer.getBikeCategory(),
                    "mid");
        }

        BigDecimal median = swissOpt.map(s -> s.getMedianChf()).orElse(null);
        BigDecimal discountPct = null;
        boolean bargain = false;
        if (median != null && median.signum() > 0) {
            discountPct = median.subtract(landed)
                    .divide(median, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
            bargain = discountPct.compareTo(pricingProperties.getBargainThresholdPercent()) >= 0;
        }

        offer.applyComputedPricing(
                priceChf,
                shipping,
                importSurcharge,
                landed,
                median,
                discountPct,
                bargain);
    }

    private BigDecimal resolveShipping(String countryCode) {
        if (countryCode == null) {
            return pricingProperties.getDefaultShippingEstimateChf();
        }
        Map<String, BigDecimal> map = pricingProperties.getShippingEstimateChfByCountry();
        return map.getOrDefault(countryCode.toUpperCase(), pricingProperties.getDefaultShippingEstimateChf());
    }

    static String inferSpecTier(Integer batteryWh) {
        if (batteryWh == null) {
            return "mid";
        }
        if (batteryWh >= 750) {
            return "premium";
        }
        if (batteryWh >= 400) {
            return "mid";
        }
        return "entry";
    }

    @Transactional
    public void repriceAllActive() {
        for (BikeOffer offer : bikeOfferRepository.findAllByStatusWithSource("active")) {
            repriceOffer(offer);
        }
    }

    /** Ensures ECB rate exists locally, then reprices all offers (used on startup). */
    @Transactional
    public void ensureFxAndReprice() {
        if (fxRateRepository.findTopByCurrencyPairOrderByEffectiveDateDesc(EcbEurChfIngestionService.PAIR_EUR_CHF).isEmpty()) {
            ecbEurChfIngestionService.fetchAndStoreDailyRate();
        }
        repriceAllActive();
    }
}
