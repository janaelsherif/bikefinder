package eu.bikefinder.app.service;

import eu.bikefinder.app.config.EbfMailProperties;
import eu.bikefinder.app.domain.PriceAlertSubscription;
import eu.bikefinder.app.repo.PriceAlertSubscriptionRepository;
import eu.bikefinder.app.web.dto.OfferSearchParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class PriceAlertDigestRunner {

    private static final Logger log = LoggerFactory.getLogger(PriceAlertDigestRunner.class);

    private static final int BATCH_SIZE = 25;
    private static final int MAX_ROUNDS = 5;

    private final EbfMailProperties mailProperties;
    private final PriceAlertSubscriptionRepository subscriptionRepository;
    private final OfferQueryService offerQueryService;
    private final PriceAlertMailService mailService;

    public PriceAlertDigestRunner(
            EbfMailProperties mailProperties,
            PriceAlertSubscriptionRepository subscriptionRepository,
            OfferQueryService offerQueryService,
            PriceAlertMailService mailService) {
        this.mailProperties = mailProperties;
        this.subscriptionRepository = subscriptionRepository;
        this.offerQueryService = offerQueryService;
        this.mailService = mailService;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processSubscription(UUID subscriptionId) {
        if (!mailProperties.isEnabled()) {
            return;
        }
        PriceAlertSubscription sub =
                subscriptionRepository.findById(subscriptionId).orElse(null);
        if (sub == null || !sub.isActive()) {
            return;
        }
        OfferSearchParams params;
        try {
            params = AlertFilterMapper.toParams(sub.getFilterJson());
        } catch (Exception e) {
            log.warn("Invalid alert filter for subscription {}", subscriptionId, e);
            return;
        }
        Instant sinceExclusive =
                sub.getLastOfferWatermark() != null
                        ? sub.getLastOfferWatermark()
                        : sub.getCreatedAt().minusNanos(1);

        for (int round = 0; round < MAX_ROUNDS; round++) {
            List<OfferAlertBatchItem> batch;
            try {
                batch = offerQueryService.searchOffersForAlertSince(params, sinceExclusive, BATCH_SIZE);
            } catch (ResponseStatusException e) {
                if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                    log.warn("Rejected alert filter for subscription {}: {}", subscriptionId, e.getMessage());
                    return;
                }
                throw e;
            }
            if (batch.isEmpty()) {
                break;
            }
            mailService.sendDigest(sub, batch);
            Instant maxSeen =
                    batch.stream()
                            .map(OfferAlertBatchItem::firstSeenAt)
                            .max(Comparator.naturalOrder())
                            .orElseThrow();
            sub.setLastOfferWatermark(maxSeen);
            subscriptionRepository.save(sub);
            sinceExclusive = maxSeen;
        }
    }
}
