package eu.bikefinder.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Optional Claude (Anthropic) and Perplexity APIs for on-demand intelligence briefs (no Telegram).
 * Keys via {@code ANTHROPIC_API_KEY} / {@code PERPLEXITY_API_KEY}.
 */
@ConfigurationProperties(prefix = "ebf.llm")
public class LlmProperties {

    private String anthropicApiKey = "";
    /** Anthropic Messages API model id (override if your account uses a different default). */
    private String anthropicModel = "claude-3-5-sonnet-20241022";

    private String perplexityApiKey = "";
    /** Perplexity chat/completions model (e.g. sonar, sonar-pro). */
    private String perplexityModel = "sonar";

    public String getAnthropicApiKey() {
        return anthropicApiKey;
    }

    public void setAnthropicApiKey(String anthropicApiKey) {
        this.anthropicApiKey = anthropicApiKey;
    }

    public String getAnthropicModel() {
        return anthropicModel;
    }

    public void setAnthropicModel(String anthropicModel) {
        this.anthropicModel = anthropicModel;
    }

    public String getPerplexityApiKey() {
        return perplexityApiKey;
    }

    public void setPerplexityApiKey(String perplexityApiKey) {
        this.perplexityApiKey = perplexityApiKey;
    }

    public String getPerplexityModel() {
        return perplexityModel;
    }

    public void setPerplexityModel(String perplexityModel) {
        this.perplexityModel = perplexityModel;
    }

    public boolean hasAnthropic() {
        return anthropicApiKey != null && !anthropicApiKey.isBlank();
    }

    public boolean hasPerplexity() {
        return perplexityApiKey != null && !perplexityApiKey.isBlank();
    }
}
