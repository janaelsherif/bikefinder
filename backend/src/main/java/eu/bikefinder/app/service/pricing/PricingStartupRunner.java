package eu.bikefinder.app.service.pricing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class PricingStartupRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(PricingStartupRunner.class);

    private final PricingService pricingService;

    public PricingStartupRunner(PricingService pricingService) {
        this.pricingService = pricingService;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            pricingService.ensureFxAndReprice();
        } catch (Exception e) {
            log.warn("Initial ECB fetch / repricing skipped (offline or ECB unreachable): {}", e.getMessage());
        }
    }
}
