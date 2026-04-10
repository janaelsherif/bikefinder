package eu.bikefinder.app.web.dto;

import java.math.BigDecimal;

/** One Swiss competitor shop result from an on-demand PriceSense live search. */
public record LiveProbeRowDto(
        String slug,
        String displayName,
        BigDecimal priceChf,
        String productUrl,
        String errorMessage) {}
