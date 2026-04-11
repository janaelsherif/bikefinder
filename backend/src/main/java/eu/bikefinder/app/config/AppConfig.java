package eu.bikefinder.app.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties({
    PricingProperties.class,
    AdminProperties.class,
    CrawlProperties.class,
    SearchProperties.class,
    StaffProperties.class,
    EbfMailProperties.class,
    PriceSenseProperties.class,
    CompetitorWatchProperties.class,
    CorsProperties.class,
    LlmProperties.class
})
public class AppConfig {

    @Bean
    public RestClient ecbRestClient() {
        return RestClient.builder()
                .baseUrl("https://www.ecb.europa.eu")
                .build();
    }

    /** Anthropic Messages API (Claude). */
    @Bean("anthropicRestClient")
    public RestClient anthropicRestClient() {
        return RestClient.builder().baseUrl("https://api.anthropic.com").build();
    }

    /** Perplexity OpenAI-compatible API. */
    @Bean("perplexityRestClient")
    public RestClient perplexityRestClient() {
        return RestClient.builder().baseUrl("https://api.perplexity.ai").build();
    }
}
