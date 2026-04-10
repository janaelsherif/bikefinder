package eu.bikefinder.app.web.dto;

import java.util.UUID;

public record SourceSummaryDto(
        UUID id,
        String name,
        String countryCode,
        String type,
        String baseUrl,
        boolean crawlEnabled,
        int refreshIntervalMin) {}
