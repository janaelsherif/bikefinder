package eu.bikefinder.app.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "source")
public class Source {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "country_code", nullable = false, length = 2)
    private String countryCode;

    @Column(nullable = false, length = 32)
    private String type;

    @Column(name = "base_url", nullable = false)
    private String baseUrl;

    @Column(name = "crawl_enabled", nullable = false)
    private boolean crawlEnabled = true;

    @Column(name = "refresh_interval_min", nullable = false)
    private int refreshIntervalMin = 240;

    @Column(name = "robots_compliant", nullable = false)
    private boolean robotsCompliant = true;

    @Column(name = "last_crawl_at")
    private Instant lastCrawlAt;

    @Column(name = "last_crawl_status", length = 16)
    private String lastCrawlStatus;

    @Column(name = "avg_offers_per_crawl")
    private Integer avgOffersPerCrawl;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getType() {
        return type;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public boolean isCrawlEnabled() {
        return crawlEnabled;
    }

    public boolean isRobotsCompliant() {
        return robotsCompliant;
    }

    public int getRefreshIntervalMin() {
        return refreshIntervalMin;
    }

    public void markCrawlFinished(String status, Integer offersThisRun) {
        this.lastCrawlAt = Instant.now();
        this.lastCrawlStatus = status;
        this.avgOffersPerCrawl = offersThisRun;
    }
}
