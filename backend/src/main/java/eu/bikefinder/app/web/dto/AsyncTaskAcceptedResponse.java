package eu.bikefinder.app.web.dto;

public record AsyncTaskAcceptedResponse(String taskId, String taskType, String status) {}
