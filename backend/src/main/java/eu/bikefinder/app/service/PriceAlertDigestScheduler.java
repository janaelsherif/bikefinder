package eu.bikefinder.app.service;

import eu.bikefinder.app.config.EbfMailProperties;
import eu.bikefinder.app.repo.PriceAlertSubscriptionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PriceAlertDigestScheduler {

    private static final Logger log = LoggerFactory.getLogger(PriceAlertDigestScheduler.class);

    private final EbfMailProperties mailProperties;
    private final PriceAlertSubscriptionRepository subscriptionRepository;
    private final PriceAlertDigestRunner digestRunner;

    public PriceAlertDigestScheduler(
            EbfMailProperties mailProperties,
            PriceAlertSubscriptionRepository subscriptionRepository,
            PriceAlertDigestRunner digestRunner) {
        this.mailProperties = mailProperties;
        this.subscriptionRepository = subscriptionRepository;
        this.digestRunner = digestRunner;
    }

    @Scheduled(cron = "${ebf.mail.digest-cron:0 0 7 * * *}", zone = "${ebf.mail.digest-zone:Europe/Zurich}")
    public void sendDigests() {
        if (!mailProperties.isEnabled()) {
            return;
        }
        for (var sub : subscriptionRepository.findByActiveIsTrue()) {
            try {
                digestRunner.processSubscription(sub.getId());
            } catch (Exception e) {
                log.error("Price alert digest failed for subscription {}", sub.getId(), e);
            }
        }
    }
}
