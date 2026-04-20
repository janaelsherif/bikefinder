package eu.bikefinder.app.repo;

import eu.bikefinder.app.domain.CrawlSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CrawlSettingsRepository extends JpaRepository<CrawlSettings, Short> {}
