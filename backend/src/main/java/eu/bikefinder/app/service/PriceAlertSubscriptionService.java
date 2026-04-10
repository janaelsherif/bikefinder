package eu.bikefinder.app.service;

import com.fasterxml.jackson.databind.JsonNode;
import eu.bikefinder.app.domain.PriceAlertSubscription;
import eu.bikefinder.app.repo.PriceAlertSubscriptionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class PriceAlertSubscriptionService {

    private static final Logger log = LoggerFactory.getLogger(PriceAlertSubscriptionService.class);

    private final PriceAlertSubscriptionRepository repository;
    private final PriceAlertMailService mailService;

    public PriceAlertSubscriptionService(
            PriceAlertSubscriptionRepository repository, PriceAlertMailService mailService) {
        this.repository = repository;
        this.mailService = mailService;
    }

    @Transactional
    public void subscribe(String email, JsonNode filter, String locale) {
        var row = new PriceAlertSubscription();
        row.setEmail(email.trim().toLowerCase());
        row.setFilterJson(filter);
        row.setLocale(locale != null && !locale.isBlank() ? locale : "de-CH");
        PriceAlertSubscription saved = repository.save(row);
        log.info("Price alert subscription id={}", saved.getId());
        try {
            mailService.sendWelcome(saved);
        } catch (Exception e) {
            log.warn("Welcome mail failed for subscription {}", saved.getId(), e);
        }
    }

    @Transactional
    public boolean unsubscribe(UUID token) {
        Optional<PriceAlertSubscription> row = repository.findByUnsubscribeToken(token);
        if (row.isEmpty()) {
            return false;
        }
        PriceAlertSubscription s = row.get();
        s.setActive(false);
        repository.save(s);
        log.info("Price alert unsubscribed id={}", s.getId());
        return true;
    }
}
