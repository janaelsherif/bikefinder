package eu.bikefinder.app.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class BikeWishXmlParseServiceTest {

    private final BikeWishXmlParseService parseService = new BikeWishXmlParseService();
    private final ObjectMapper json = new ObjectMapper();

    @Test
    void parsesExampleXmlAndExtractsEmail() throws IOException {
        String xml =
                """
                <?xml version="1.0" encoding="UTF-8"?>
                <bikeWish xmlns="https://patrickbike.ch/ns/wunschvelo/1" version="1">
                  <contact>
                    <email>kunde@example.ch</email>
                    <fullName>Max Muster</fullName>
                    <phone>+41 79 000 00 00</phone>
                    <preferredChannel>email</preferredChannel>
                  </contact>
                  <useCase>ebike</useCase>
                </bikeWish>
                """;
        JsonNode tree = parseService.parseToTree(xml);
        assertThat(parseService.extractContactEmail(tree)).isEqualTo("kunde@example.ch");
        assertThat(parseService.extractContactName(tree)).isEqualTo("Max Muster");
        JsonNode merged = parseService.mergeContactIntoPayload(
                tree, "kunde@example.ch", "Max Muster", "+41 79 000 00 00", json);
        assertThat(merged.toString()).contains("kunde@example.ch");
    }
}
