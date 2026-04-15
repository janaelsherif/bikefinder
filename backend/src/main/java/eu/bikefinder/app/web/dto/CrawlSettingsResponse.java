package eu.bikefinder.app.web.dto;

import java.time.Instant;

public record CrawlSettingsResponse(
        boolean autoCrawlEnabled,
        String autoCrawlTime,
        String timezone,
        Instant lastAutoRunAt) {}
