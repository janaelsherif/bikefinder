package eu.bikefinder.app.web.dto;

import java.time.Instant;
import java.util.UUID;

public record CompetitorWatchSnapshotDto(
        UUID id,
        String targetSlug,
        Instant capturedAt,
        Integer httpStatus,
        Integer listingCountEstimate,
        Integer deltaVsPrevious,
        String errorMessage,
        Integer durationMs) {}
