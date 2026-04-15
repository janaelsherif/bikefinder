package eu.bikefinder.app.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalTime;

@Entity
@Table(name = "crawl_settings")
public class CrawlSettings {

    public static final short SINGLETON_ID = 1;

    @Id
    private Short id = SINGLETON_ID;

    @Column(name = "auto_crawl_enabled", nullable = false)
    private boolean autoCrawlEnabled = false;

    @Column(name = "auto_crawl_time", nullable = false)
    private LocalTime autoCrawlTime = LocalTime.of(3, 0);

    @Column(name = "timezone", nullable = false, length = 64)
    private String timezone = "Europe/Zurich";

    @Column(name = "last_auto_run_at")
    private Instant lastAutoRunAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void prePersist() {
        if (id == null) {
            id = SINGLETON_ID;
        }
        if (timezone == null || timezone.isBlank()) {
            timezone = "Europe/Zurich";
        }
        if (autoCrawlTime == null) {
            autoCrawlTime = LocalTime.of(3, 0);
        }
    }

    public Short getId() {
        return id;
    }

    public boolean isAutoCrawlEnabled() {
        return autoCrawlEnabled;
    }

    public void setAutoCrawlEnabled(boolean autoCrawlEnabled) {
        this.autoCrawlEnabled = autoCrawlEnabled;
    }

    public LocalTime getAutoCrawlTime() {
        return autoCrawlTime;
    }

    public void setAutoCrawlTime(LocalTime autoCrawlTime) {
        this.autoCrawlTime = autoCrawlTime;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public Instant getLastAutoRunAt() {
        return lastAutoRunAt;
    }

    public void setLastAutoRunAt(Instant lastAutoRunAt) {
        this.lastAutoRunAt = lastAutoRunAt;
    }
}
