package eu.bikefinder.app.web;

import eu.bikefinder.app.repo.FxRateRepository;
import eu.bikefinder.app.service.fx.EcbEurChfIngestionService;
import eu.bikefinder.app.service.pricing.PricingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/system")
public class FxStatusController {

    private final FxRateRepository fxRateRepository;
    private final EcbEurChfIngestionService ecbEurChfIngestionService;
    private final PricingService pricingService;

    public FxStatusController(
            FxRateRepository fxRateRepository,
            EcbEurChfIngestionService ecbEurChfIngestionService,
            PricingService pricingService) {
        this.fxRateRepository = fxRateRepository;
        this.ecbEurChfIngestionService = ecbEurChfIngestionService;
        this.pricingService = pricingService;
    }

    @GetMapping("/fx/eur-chf")
    public Map<String, Object> latestEurChf() {
        return fxRateRepository.findTopByCurrencyPairOrderByEffectiveDateDesc(EcbEurChfIngestionService.PAIR_EUR_CHF)
                .map(fx -> Map.<String, Object>of(
                        "currencyPair", fx.getCurrencyPair(),
                        "rate", fx.getRate(),
                        "effectiveDate", fx.getEffectiveDate().toString()))
                .orElse(Map.of("message", "no rate stored yet"));
    }

    /** Dev convenience: pull ECB now and reprice offers. */
    @PostMapping("/fx/refresh")
    public Map<String, Object> refreshNow() {
        boolean inserted = ecbEurChfIngestionService.fetchAndStoreDailyRate();
        pricingService.repriceAllActive();
        return Map.of("ecbRowInserted", inserted, "ok", true);
    }
}
