package eu.bikefinder.app.web.dto;

public record CompetitorWatchDashboardRow(
        CompetitorWatchTargetDto target, CompetitorWatchSnapshotDto latestSnapshot) {}
