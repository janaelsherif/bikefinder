package eu.bikefinder.app.repo;

import eu.bikefinder.app.domain.PriceSenseQuery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PriceSenseQueryRepository extends JpaRepository<PriceSenseQuery, UUID> {}
