package eu.bikefinder.app.service.crawl;

import eu.bikefinder.app.domain.BikeOffer;
import eu.bikefinder.app.domain.Source;
import eu.bikefinder.app.repo.BikeOfferRepository;
import eu.bikefinder.app.repo.SourceRepository;
import eu.bikefinder.app.service.pricing.PricingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class CrawlOfferPersistence {

    private final BikeOfferRepository bikeOfferRepository;
    private final SourceRepository sourceRepository;
    private final PricingService pricingService;

    public CrawlOfferPersistence(
            BikeOfferRepository bikeOfferRepository,
            SourceRepository sourceRepository,
            PricingService pricingService) {
        this.bikeOfferRepository = bikeOfferRepository;
        this.sourceRepository = sourceRepository;
        this.pricingService = pricingService;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean importIfAbsent(
            UUID sourceId,
            String sourceOfferId,
            String sourceUrl,
            String brand,
            String model,
            Integer modelYear,
            String bikeCategory,
            String bikeCondition,
            String motorBrand,
            Integer batteryWh,
            Integer mileageKm,
            BigDecimal totalPriceLocal,
            String currencyCode,
            String[] images) {
        if (bikeOfferRepository.existsBySource_IdAndSourceOfferId(sourceId, sourceOfferId)) {
            return false;
        }
        Source source = sourceRepository.findById(sourceId).orElseThrow();
        BikeOffer offer =
                BikeOffer.createImported(
                        source,
                        sourceOfferId,
                        sourceUrl,
                        brand,
                        model,
                        modelYear,
                        bikeCategory,
                        bikeCondition,
                        motorBrand,
                        "mid",
                        batteryWh,
                        mileageKm,
                        "certified_refurb",
                        24,
                        totalPriceLocal,
                        currencyCode,
                        "heuristic",
                        images,
                        new BigDecimal("8.0"));
        bikeOfferRepository.save(offer);
        pricingService.repriceOffer(offer);
        return true;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordSourceCrawl(UUID sourceId, String status, Integer offersImported) {
        Source source = sourceRepository.findById(sourceId).orElseThrow();
        source.markCrawlFinished(status, offersImported);
    }
}
