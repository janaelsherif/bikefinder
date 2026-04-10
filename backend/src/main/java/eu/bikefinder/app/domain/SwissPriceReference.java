package eu.bikefinder.app.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "swiss_price_reference")
public class SwissPriceReference {

    @Id
    private UUID id;

    protected SwissPriceReference() {
    }

    @Column(nullable = false, length = 100)
    private String brand;

    @Column(name = "bike_category", nullable = false, length = 32)
    private String bikeCategory;

    @Column(name = "spec_tier", nullable = false, length = 16)
    private String specTier;

    @Column(name = "median_chf", nullable = false, precision = 12, scale = 2)
    private BigDecimal medianChf;

    @Column(name = "p25_chf", precision = 12, scale = 2)
    private BigDecimal p25Chf;

    @Column(name = "p75_chf", precision = 12, scale = 2)
    private BigDecimal p75Chf;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public BigDecimal getMedianChf() {
        return medianChf;
    }
}
