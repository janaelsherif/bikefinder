package eu.bikefinder.app.domain;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "price_alert_subscription")
public class PriceAlertSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 320)
    private String email;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "filter_json", nullable = false, columnDefinition = "jsonb")
    private JsonNode filterJson;

    @Column(nullable = false, length = 16)
    private String locale = "de-CH";

    @Column(nullable = false)
    private boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /** Max {@code bike_offer.first_seen_at} included in a digest (exclusive lower bound for the next run). */
    @Column(name = "last_offer_watermark")
    private Instant lastOfferWatermark;

    @Column(name = "unsubscribe_token", nullable = false, updatable = false)
    private UUID unsubscribeToken;

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public JsonNode getFilterJson() {
        return filterJson;
    }

    public String getLocale() {
        return locale;
    }

    public boolean isActive() {
        return active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getLastOfferWatermark() {
        return lastOfferWatermark;
    }

    public void setLastOfferWatermark(Instant lastOfferWatermark) {
        this.lastOfferWatermark = lastOfferWatermark;
    }

    public UUID getUnsubscribeToken() {
        return unsubscribeToken;
    }

    @PrePersist
    void assignUnsubscribeToken() {
        if (unsubscribeToken == null) {
            unsubscribeToken = UUID.randomUUID();
        }
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFilterJson(JsonNode filterJson) {
        this.filterJson = filterJson;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
