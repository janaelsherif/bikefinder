package eu.bikefinder.app.repo;

import eu.bikefinder.app.domain.PriceAlertSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PriceAlertSubscriptionRepository
        extends JpaRepository<PriceAlertSubscription, UUID> {

    List<PriceAlertSubscription> findByActiveIsTrue();

    Optional<PriceAlertSubscription> findByUnsubscribeToken(UUID unsubscribeToken);
}
