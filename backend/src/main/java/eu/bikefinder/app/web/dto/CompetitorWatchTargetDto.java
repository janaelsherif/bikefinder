package eu.bikefinder.app.web.dto;

import java.util.UUID;

public record CompetitorWatchTargetDto(
        UUID id, String slug, String displayName, String watchUrl, boolean active) {}
