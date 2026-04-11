package eu.bikefinder.app.service.llm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.bikefinder.app.config.LlmProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Minimal Anthropic Messages API client (Claude).
 */
@Component
public class AnthropicMessagesClient {

    private static final String ANTHROPIC_VERSION = "2023-06-01";

    private final RestClient restClient;
    private final LlmProperties props;
    private final ObjectMapper objectMapper;

    public AnthropicMessagesClient(
            @Qualifier("anthropicRestClient") RestClient restClient,
            LlmProperties props,
            ObjectMapper objectMapper) {
        this.restClient = restClient;
        this.props = props;
        this.objectMapper = objectMapper;
    }

    /**
     * @param system optional system prompt (may be null)
     * @param user user message
     */
    public String complete(String system, String user) {
        ObjectNode body = objectMapper.createObjectNode();
        body.put("model", props.getAnthropicModel());
        body.put("max_tokens", 4096);
        if (system != null && !system.isBlank()) {
            body.put("system", system);
        }
        ArrayNode messages = body.putArray("messages");
        ObjectNode m = messages.addObject();
        m.put("role", "user");
        m.put("content", user);

        String json = body.toString();
        String raw =
                restClient
                        .post()
                        .uri("/v1/messages")
                        .header("x-api-key", props.getAnthropicApiKey().trim())
                        .header("anthropic-version", ANTHROPIC_VERSION)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(json)
                        .retrieve()
                        .onStatus(
                                HttpStatusCode::isError,
                                (req, res) -> {
                                    throw new IllegalStateException(
                                            "Anthropic HTTP "
                                                    + res.getStatusCode()
                                                    + ": "
                                                    + new String(res.getBody().readAllBytes()));
                                })
                        .body(String.class);

        try {
            JsonNode root = objectMapper.readTree(raw);
            JsonNode content = root.path("content");
            if (content.isArray() && content.size() > 0) {
                JsonNode block = content.get(0);
                String text = block.path("text").asText("");
                if (!text.isEmpty()) {
                    return text;
                }
            }
            throw new IllegalStateException("Unexpected Anthropic response shape: " + raw);
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse Anthropic response", e);
        }
    }
}
