package eu.bikefinder.app.repo;

import eu.bikefinder.app.domain.SwissPriceReference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SwissPriceReferenceRepository extends JpaRepository<SwissPriceReference, UUID> {

    Optional<SwissPriceReference> findByBrandAndBikeCategoryAndSpecTier(
            String brand,
            String bikeCategory,
            String specTier);
}
