package eu.bikefinder.app.web.dto;

import org.springframework.data.domain.Page;

/**
 * Staff wish search: strict filters first; if empty and near-match is enabled, a relaxed query is used.
 */
public record WishSearchResponse(String matchTier, Page<OfferSummaryDto> exact, Page<OfferSummaryDto> near) {}
