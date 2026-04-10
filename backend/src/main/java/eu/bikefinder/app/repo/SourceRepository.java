package eu.bikefinder.app.repo;

import eu.bikefinder.app.domain.Source;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SourceRepository extends JpaRepository<Source, UUID> {}
