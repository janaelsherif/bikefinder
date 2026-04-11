package eu.bikefinder.app.service.competitorwatch;

import eu.bikefinder.app.config.LlmProperties;
import eu.bikefinder.app.service.llm.AnthropicMessagesClient;
import eu.bikefinder.app.service.llm.PerplexityChatClient;
import eu.bikefinder.app.web.dto.CompetitorWatchBriefResponse;
import eu.bikefinder.app.web.dto.CompetitorWatchDashboardRow;
import eu.bikefinder.app.web.dto.CompetitorWatchSnapshotDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * VeloIntel-style Module 5 narrative: Claude synthesizes competitor snapshot data; optional Perplexity adds
 * recent Swiss market context. Invoked from the web UI / API only (no Telegram).
 */
@Service
public class CompetitorWatchBriefService {

    private static final Logger log = LoggerFactory.getLogger(CompetitorWatchBriefService.class);

    private static final String SYSTEM =
            """
            You are a concise market analyst helping PatrickBike (Basel & Zürich, certified pre-owned bikes).
            You receive automated HTTP snapshot estimates (listing counts and deltas) for Swiss competitor shop fronts.
            PatrickBike's live inventory is NOT attached — do not invent stock overlap; speak in terms of signals and what to verify.
            Output Markdown: short title, bullet signals (max 6), then "Watch next" with 2–3 actions. Total under 350 words.
            Be factual; if data is thin, say so. Language: English unless the user focus asks otherwise.
            """;

    private final LlmProperties llmProperties;
    private final CompetitorWatchService competitorWatchService;
    private final AnthropicMessagesClient anthropicMessagesClient;
    private final PerplexityChatClient perplexityChatClient;

    public CompetitorWatchBriefService(
            LlmProperties llmProperties,
            CompetitorWatchService competitorWatchService,
            AnthropicMessagesClient anthropicMessagesClient,
            PerplexityChatClient perplexityChatClient) {
        this.llmProperties = llmProperties;
        this.competitorWatchService = competitorWatchService;
        this.anthropicMessagesClient = anthropicMessagesClient;
        this.perplexityChatClient = perplexityChatClient;
    }

    public CompetitorWatchBriefResponse generate(String focus) {
        if (!llmProperties.hasAnthropic()) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "LLM not configured: set ANTHROPIC_API_KEY (see ebf.llm in application.yml).");
        }

        List<CompetitorWatchDashboardRow> rows = competitorWatchService.dashboard();
        String snapshotBlock = formatSnapshots(rows);

        boolean usedPerplexity = false;
        String perplexityNote = "";
        if (llmProperties.hasPerplexity()) {
            try {
                perplexityNote =
                        perplexityChatClient.completeUserMessage(
                                """
                                In at most 6 short bullet points, summarize notable Swiss e-bike or certified \
                                pre-owned retail/market news from roughly the last 7 days relevant to dealers \
                                watching: Veloplus, Upway Switzerland, Rebike Switzerland, BibiBike, Velocorner. \
                                If nothing material, say "No major public signals this week." Be factual; avoid speculation.
                                """);
                usedPerplexity = !perplexityNote.isBlank();
            } catch (Exception e) {
                log.warn("Perplexity call failed; continuing with Claude only", e);
            }
        }

        StringBuilder user = new StringBuilder();
        user.append("## Snapshot data (automated estimates)\n\n");
        user.append(snapshotBlock);
        user.append("\n\n## Optional focus from user\n\n");
        if (focus != null && !focus.isBlank()) {
            user.append(focus.trim());
        } else {
            user.append("(none)");
        }
        user.append("\n\n## Web-grounded context (Perplexity)\n\n");
        if (!perplexityNote.isBlank()) {
            user.append(perplexityNote);
        } else {
            user.append("(not available or skipped)");
        }

        String markdown = anthropicMessagesClient.complete(SYSTEM, user.toString());
        return new CompetitorWatchBriefResponse(
                markdown,
                usedPerplexity,
                llmProperties.getAnthropicModel(),
                llmProperties.getPerplexityModel());
    }

    private static String formatSnapshots(List<CompetitorWatchDashboardRow> rows) {
        if (rows.isEmpty()) {
            return "No active competitor watch targets.";
        }
        StringBuilder sb = new StringBuilder();
        for (CompetitorWatchDashboardRow row : rows) {
            sb.append("- **")
                    .append(row.target().displayName())
                    .append("** (")
                    .append(row.target().slug())
                    .append(")\n");
            sb.append("  - URL: ").append(row.target().watchUrl()).append("\n");
            CompetitorWatchSnapshotDto s = row.latestSnapshot();
            if (s == null) {
                sb.append("  - Latest snapshot: none\n");
                continue;
            }
            sb.append("  - Last capture: ").append(s.capturedAt()).append("\n");
            sb.append("  - HTTP: ").append(s.httpStatus()).append("\n");
            sb.append("  - Listing count estimate: ").append(s.listingCountEstimate()).append("\n");
            sb.append("  - Delta vs previous: ").append(s.deltaVsPrevious()).append("\n");
            if (s.errorMessage() != null && !s.errorMessage().isBlank()) {
                sb.append("  - Error: ").append(s.errorMessage()).append("\n");
            }
        }
        return sb.toString();
    }
}
