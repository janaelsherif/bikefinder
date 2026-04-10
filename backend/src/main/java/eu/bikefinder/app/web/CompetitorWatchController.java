package eu.bikefinder.app.web;

import eu.bikefinder.app.service.competitorwatch.CompetitorWatchService;
import eu.bikefinder.app.web.dto.CompetitorWatchDashboardRow;
import eu.bikefinder.app.web.dto.CompetitorWatchSnapshotDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Hamza / Patrick “5 competitors” snapshot dashboard (HTTP fetch + heuristic listing count). Staff token
 * when {@code ebf.staff.api-token} is set — same as procurement APIs.
 */
@RestController
@RequestMapping("/api/v1/competitor-watch")
public class CompetitorWatchController {

    private final CompetitorWatchService competitorWatchService;

    public CompetitorWatchController(CompetitorWatchService competitorWatchService) {
        this.competitorWatchService = competitorWatchService;
    }

    @GetMapping("/dashboard")
    public List<CompetitorWatchDashboardRow> dashboard() {
        return competitorWatchService.dashboard();
    }

    @GetMapping("/history/{slug}")
    public List<CompetitorWatchSnapshotDto> history(@PathVariable String slug) {
        return competitorWatchService.history(slug);
    }
}
