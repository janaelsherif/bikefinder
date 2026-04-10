package eu.bikefinder.app.service.pricing;

import eu.bikefinder.app.service.fx.EcbEurChfIngestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DailyFxAndPricingJob {

    private static final Logger log = LoggerFactory.getLogger(DailyFxAndPricingJob.class);

    private final EcbEurChfIngestionService ecbEurChfIngestionService;
    private final PricingService pricingService;

    @Value("${ebf.fx.enabled:true}")
    private boolean fxEnabled;

    public DailyFxAndPricingJob(EcbEurChfIngestionService ecbEurChfIngestionService, PricingService pricingService) {
        this.ecbEurChfIngestionService = ecbEurChfIngestionService;
        this.pricingService = pricingService;
    }

    /** Dev spec G2: daily FX update (07:00 Zurich). */
    @Scheduled(cron = "${ebf.fx.cron:0 0 7 * * *}", zone = "${ebf.fx.zone:Europe/Zurich}")
    public void run() {
        if (!fxEnabled) {
            return;
        }
        try {
            ecbEurChfIngestionService.fetchAndStoreDailyRate();
            pricingService.repriceAllActive();
        } catch (Exception e) {
            log.warn("Scheduled ECB / repricing failed: {}", e.getMessage());
        }
    }
}
