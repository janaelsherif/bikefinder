package eu.bikefinder.app.web.dto;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PriceAlertSubscribeRequest(
        @NotBlank @Email String email,
        @NotNull JsonNode filter,
        String locale) {}
