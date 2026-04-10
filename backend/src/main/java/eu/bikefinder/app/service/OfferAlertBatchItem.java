package eu.bikefinder.app.service;

import eu.bikefinder.app.web.dto.OfferSummaryDto;

import java.time.Instant;

public record OfferAlertBatchItem(OfferSummaryDto offer, Instant firstSeenAt) {}
