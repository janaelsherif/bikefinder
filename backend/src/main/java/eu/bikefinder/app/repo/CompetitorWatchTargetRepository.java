package eu.bikefinder.app.repo;

import eu.bikefinder.app.domain.CompetitorWatchTarget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CompetitorWatchTargetRepository extends JpaRepository<CompetitorWatchTarget, UUID> {

    List<CompetitorWatchTarget> findByActiveIsTrueOrderBySlugAsc();

    List<CompetitorWatchTarget> findByActiveIsTrueAndLivePriceProbeEnabledIsTrueOrderBySlugAsc();
}
