package eu.bikefinder.app.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record OfferImportBatchRequest(
        @NotNull UUID sourceId, @NotEmpty @Valid List<OfferImportRow> offers) {}
