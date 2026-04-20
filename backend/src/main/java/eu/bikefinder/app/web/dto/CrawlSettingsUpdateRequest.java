package eu.bikefinder.app.web.dto;

public record CrawlSettingsUpdateRequest(Boolean autoCrawlEnabled, String autoCrawlTime, String timezone) {}
