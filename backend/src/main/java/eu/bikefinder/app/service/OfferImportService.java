package eu.bikefinder.app.service;

import eu.bikefinder.app.domain.BikeOffer;
import eu.bikefinder.app.domain.Source;
import eu.bikefinder.app.repo.BikeOfferRepository;
import eu.bikefinder.app.repo.SourceRepository;
import eu.bikefinder.app.service.pricing.PricingService;
import eu.bikefinder.app.web.dto.OfferImportBatchRequest;
import eu.bikefinder.app.web.dto.OfferImportResultDto;
import eu.bikefinder.app.web.dto.OfferImportRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OfferImportService {

    private static final Logger log = LoggerFactory.getLogger(OfferImportService.class);

    private final SourceRepository sourceRepository;
    private final BikeOfferRepository bikeOfferRepository;
    private final PricingService pricingService;

    public OfferImportService(
            SourceRepository sourceRepository,
            BikeOfferRepository bikeOfferRepository,
            PricingService pricingService) {
        this.sourceRepository = sourceRepository;
        this.bikeOfferRepository = bikeOfferRepository;
        this.pricingService = pricingService;
    }

    @Transactional
    public OfferImportResultDto importBatch(OfferImportBatchRequest batch) {
        Source source =
                sourceRepository.findById(batch.sourceId()).orElseThrow();
        int imported = 0;
        int skipped = 0;
        for (OfferImportRow row : batch.offers()) {
            if (bikeOfferRepository.existsBySource_IdAndSourceOfferId(source.getId(), row.sourceOfferId())) {
                skipped++;
                log.debug("Skip duplicate source_offer_id={}", row.sourceOfferId());
                continue;
            }
            BikeOffer offer =
                    BikeOffer.createImported(
                            source,
                            row.sourceOfferId(),
                            row.sourceUrl(),
                            row.brand(),
                            row.model(),
                            row.modelYear(),
                            row.bikeCategory(),
                            row.bikeCondition(),
                            row.motorBrand(),
                            row.motorPosition(),
                            row.batteryWh(),
                            row.mileageKm(),
                            row.warrantyType(),
                            row.warrantyMonths(),
                            row.totalPriceLocal(),
                            row.currencyCode(),
                            row.extractionMethod(),
                            row.images(),
                            row.qualityScore());
            bikeOfferRepository.save(offer);
            pricingService.repriceOffer(offer);
            imported++;
        }
        log.info("Import finished: {} imported, {} skipped", imported, skipped);
        return new OfferImportResultDto(imported, skipped);
    }
}
