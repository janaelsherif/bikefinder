package eu.bikefinder.app.repo;

import eu.bikefinder.app.domain.FxRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface FxRateRepository extends JpaRepository<FxRate, UUID> {

    Optional<FxRate> findTopByCurrencyPairOrderByEffectiveDateDesc(String currencyPair);

    boolean existsByCurrencyPairAndEffectiveDate(String currencyPair, LocalDate effectiveDate);
}
