package eu.bikefinder.app.domain;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "competitor_watch_snapshot")
public class CompetitorWatchSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "target_id", nullable = false)
    private CompetitorWatchTarget target;

    @Column(name = "captured_at", nullable = false)
    private Instant capturedAt;

    @Column(name = "http_status")
    private Integer httpStatus;

    @Column(name = "listing_count_estimate")
    private Integer listingCountEstimate;

    @Column(name = "delta_vs_previous")
    private Integer deltaVsPrevious;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "summary_json", columnDefinition = "jsonb")
    private JsonNode summaryJson;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "duration_ms")
    private Integer durationMs;

    @PrePersist
    void prePersist() {
        if (capturedAt == null) {
            capturedAt = Instant.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public CompetitorWatchTarget getTarget() {
        return target;
    }

    public void setTarget(CompetitorWatchTarget target) {
        this.target = target;
    }

    public Instant getCapturedAt() {
        return capturedAt;
    }

    public Integer getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(Integer httpStatus) {
        this.httpStatus = httpStatus;
    }

    public Integer getListingCountEstimate() {
        return listingCountEstimate;
    }

    public void setListingCountEstimate(Integer listingCountEstimate) {
        this.listingCountEstimate = listingCountEstimate;
    }

    public Integer getDeltaVsPrevious() {
        return deltaVsPrevious;
    }

    public void setDeltaVsPrevious(Integer deltaVsPrevious) {
        this.deltaVsPrevious = deltaVsPrevious;
    }

    public JsonNode getSummaryJson() {
        return summaryJson;
    }

    public void setSummaryJson(JsonNode summaryJson) {
        this.summaryJson = summaryJson;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Integer getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(Integer durationMs) {
        this.durationMs = durationMs;
    }
}
