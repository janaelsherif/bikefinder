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
 * Perplexity OpenAI-compatible chat/completions client (web-grounded).
 */
@Component
public class PerplexityChatClient {

    private final RestClient restClient;
    private final LlmProperties props;
    private final ObjectMapper objectMapper;

    public PerplexityChatClient(
            @Qualifier("perplexityRestClient") RestClient restClient,
            LlmProperties props,
            ObjectMapper objectMapper) {
        this.restClient = restClient;
        this.props = props;
        this.objectMapper = objectMapper;
    }

    public String completeUserMessage(String userMessage) {
        ObjectNode body = objectMapper.createObjectNode();
        body.put("model", props.getPerplexityModel());
        body.put("max_tokens", 1200);
        body.put("temperature", 0.2);
        ArrayNode messages = body.putArray("messages");
        ObjectNode u = messages.addObject();
        u.put("role", "user");
        u.put("content", userMessage);

        String json = body.toString();
        String raw =
                restClient
                        .post()
                        .uri("/chat/completions")
                        .header("Authorization", "Bearer " + props.getPerplexityApiKey().trim())
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(json)
                        .retrieve()
                        .onStatus(
                                HttpStatusCode::isError,
                                (req, res) -> {
                                    throw new IllegalStateException(
                                            "Perplexity HTTP "
                                                    + res.getStatusCode()
                                                    + ": "
                                                    + new String(res.getBody().readAllBytes()));
                                })
                        .body(String.class);

        try {
            JsonNode root = objectMapper.readTree(raw);
            String text = root.path("choices").path(0).path("message").path("content").asText("");
            if (!text.isEmpty()) {
                return text;
            }
            throw new IllegalStateException("Unexpected Perplexity response shape: " + raw);
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse Perplexity response", e);
        }
    }
}
