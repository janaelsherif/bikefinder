package eu.bikefinder.app.web.dto;

import java.time.Instant;

public record AsyncTaskStatusResponse(
        String taskId,
        String taskType,
        String status,
        Instant queuedAt,
        Instant startedAt,
        Instant finishedAt,
        String errorMessage) {}
