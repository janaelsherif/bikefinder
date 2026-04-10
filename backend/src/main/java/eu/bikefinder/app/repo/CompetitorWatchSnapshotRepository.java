package eu.bikefinder.app.repo;

import eu.bikefinder.app.domain.CompetitorWatchSnapshot;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CompetitorWatchSnapshotRepository extends JpaRepository<CompetitorWatchSnapshot, UUID> {

    @EntityGraph(attributePaths = {"target"})
    Optional<CompetitorWatchSnapshot> findFirstByTarget_IdOrderByCapturedAtDesc(UUID targetId);

    List<CompetitorWatchSnapshot> findByTarget_IdOrderByCapturedAtDesc(UUID targetId, Pageable pageable);

    @EntityGraph(attributePaths = {"target"})
    List<CompetitorWatchSnapshot> findTop50ByTarget_SlugOrderByCapturedAtDesc(String slug);
}
