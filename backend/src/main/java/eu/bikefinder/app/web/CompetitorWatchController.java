package eu.bikefinder.app.web;

import eu.bikefinder.app.service.competitorwatch.CompetitorWatchBriefService;
import eu.bikefinder.app.service.competitorwatch.CompetitorWatchService;
import eu.bikefinder.app.web.dto.CompetitorWatchBriefRequest;
import eu.bikefinder.app.web.dto.CompetitorWatchBriefResponse;
import eu.bikefinder.app.web.dto.CompetitorWatchDashboardRow;
import eu.bikefinder.app.web.dto.CompetitorWatchSnapshotDto;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Hamza / Patrick “5 competitors” snapshot dashboard (HTTP fetch + heuristic listing count). Staff token
 * when {@code ebf.staff.api-token} is set — same as procurement APIs. Optional POST {@code /brief} calls Claude
 * (+ optional Perplexity); no Telegram.
 */
@RestController
@RequestMapping("/api/v1/competitor-watch")
public class CompetitorWatchController {

    private final CompetitorWatchService competitorWatchService;
    private final CompetitorWatchBriefService competitorWatchBriefService;

    public CompetitorWatchController(
            CompetitorWatchService competitorWatchService,
            CompetitorWatchBriefService competitorWatchBriefService) {
        this.competitorWatchService = competitorWatchService;
        this.competitorWatchBriefService = competitorWatchBriefService;
    }

    @GetMapping("/dashboard")
    public List<CompetitorWatchDashboardRow> dashboard() {
        return competitorWatchService.dashboard();
    }

    @GetMapping("/history/{slug}")
    public List<CompetitorWatchSnapshotDto> history(@PathVariable String slug) {
        return competitorWatchService.history(slug);
    }

    /** On-demand Markdown brief: Claude + optional Perplexity (requires {@code ANTHROPIC_API_KEY}). */
    @PostMapping(value = "/brief", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public CompetitorWatchBriefResponse brief(@Valid @RequestBody(required = false) CompetitorWatchBriefRequest body) {
        String focus = body != null ? body.getFocus() : null;
        return competitorWatchBriefService.generate(focus);
    }
}
