package eu.bikefinder.app.web;

import eu.bikefinder.app.domain.Source;
import eu.bikefinder.app.repo.SourceRepository;
import eu.bikefinder.app.web.dto.SourceSummaryDto;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Read-only catalogue of procurement sources (B2B directory seeds). Crawlers pick a {@code source_id} from here.
 */
@RestController
@RequestMapping("/api/v1/sources")
public class SourceController {

    private final SourceRepository sourceRepository;

    public SourceController(SourceRepository sourceRepository) {
        this.sourceRepository = sourceRepository;
    }

    @GetMapping
    public List<SourceSummaryDto> list() {
        return sourceRepository.findAll(Sort.by("countryCode", "name")).stream()
                .map(SourceController::toDto)
                .toList();
    }

    private static SourceSummaryDto toDto(Source s) {
        return new SourceSummaryDto(
                s.getId(),
                s.getName(),
                s.getCountryCode(),
                s.getType(),
                s.getBaseUrl(),
                s.isCrawlEnabled(),
                s.getRefreshIntervalMin());
    }
}
