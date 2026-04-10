package eu.bikefinder.app.web.dto;

import java.time.Instant;
import java.util.UUID;

public record BikeWishSubmissionResponse(UUID id, Instant createdAt) {}
