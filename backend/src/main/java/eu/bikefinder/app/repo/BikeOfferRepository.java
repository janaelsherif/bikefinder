package eu.bikefinder.app.repo;

import eu.bikefinder.app.domain.BikeOffer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface BikeOfferRepository
        extends JpaRepository<BikeOffer, UUID>, JpaSpecificationExecutor<BikeOffer> {

    boolean existsBySource_IdAndSourceOfferId(UUID sourceId, String sourceOfferId);

    @EntityGraph(attributePaths = "source")
    Page<BikeOffer> findByStatusOrderByFirstSeenAtDesc(String status, Pageable pageable);

    @EntityGraph(attributePaths = "source")
    Page<BikeOffer> findAll(Specification<BikeOffer> spec, Pageable pageable);

    @Query("SELECT b FROM BikeOffer b JOIN FETCH b.source WHERE b.status = :status")
    List<BikeOffer> findAllByStatusWithSource(String status);
}
