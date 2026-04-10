package eu.bikefinder.app.repo;

import eu.bikefinder.app.domain.BikeWishSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BikeWishSubmissionRepository extends JpaRepository<BikeWishSubmission, UUID> {}
