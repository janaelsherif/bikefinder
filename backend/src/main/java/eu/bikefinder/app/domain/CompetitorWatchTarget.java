package eu.bikefinder.app.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "competitor_watch_target")
public class CompetitorWatchTarget {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 64)
    private String slug;

    @Column(name = "display_name", nullable = false, length = 200)
    private String displayName;

    @Column(name = "watch_url", nullable = false)
    private String watchUrl;

    @Column(nullable = false)
    private boolean active = true;

    /** When true, PriceSense may run a live search on this shop (see {@code ebf.pricesense.live-competitor-search}). */
    @Column(name = "live_price_probe_enabled", nullable = false)
    private boolean livePriceProbeEnabled = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public UUID getId() {
        return id;
    }

    public String getSlug() {
        return slug;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getWatchUrl() {
        return watchUrl;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isLivePriceProbeEnabled() {
        return livePriceProbeEnabled;
    }
}
