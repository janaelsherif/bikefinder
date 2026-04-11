package eu.bikefinder.app.web.dto;

/** LLM-generated competitor briefing (Markdown). Telegram intentionally not used. */
public class CompetitorWatchBriefResponse {

    private final String markdown;
    private final boolean usedPerplexity;
    private final String anthropicModel;
    private final String perplexityModel;

    public CompetitorWatchBriefResponse(
            String markdown,
            boolean usedPerplexity,
            String anthropicModel,
            String perplexityModel) {
        this.markdown = markdown;
        this.usedPerplexity = usedPerplexity;
        this.anthropicModel = anthropicModel;
        this.perplexityModel = perplexityModel;
    }

    public String getMarkdown() {
        return markdown;
    }

    public boolean isUsedPerplexity() {
        return usedPerplexity;
    }

    public String getAnthropicModel() {
        return anthropicModel;
    }

    public String getPerplexityModel() {
        return perplexityModel;
    }
}
