package eu.bikefinder.app.web.dto;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Manual Wunsch submission (same logical payload as XML import). Top-level contact fields are
 * denormalised for indexing; full form lives in {@code payload}.
 */
public record BikeWishManualRequest(
        @NotBlank @Email String contactEmail,
        String contactName,
        String contactPhone,
        @NotNull JsonNode payload) {}
