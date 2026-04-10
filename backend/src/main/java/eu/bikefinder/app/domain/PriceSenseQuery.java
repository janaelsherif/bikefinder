package eu.bikefinder.app.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "price_sense_query")
public class PriceSenseQuery {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @CreationTimestamp
    @Column(name = "queried_at", nullable = false, updatable = false)
    private Instant queriedAt;

    @Column(nullable = false, length = 100)
    private String brand;

    @Column(nullable = false, length = 100)
    private String model;

    @Column(name = "model_year")
    private Integer modelYear;

    @Column(name = "condition_grade", nullable = false, length = 1)
    private String conditionGrade;

    @Column(name = "buyin_cost_chf", nullable = false, precision = 12, scale = 2)
    private BigDecimal buyinCostChf;

    @Column(name = "n_ch", nullable = false)
    private int nCh;

    @Column(name = "n_de", nullable = false)
    private int nDe;

    @Column(name = "p_median_chf", precision = 12, scale = 2)
    private BigDecimal pMedianChf;

    @Column(name = "p_p25_chf", precision = 12, scale = 2)
    private BigDecimal pP25Chf;

    @Column(name = "p_p75_chf", precision = 12, scale = 2)
    private BigDecimal pP75Chf;

    @Column(name = "p_target_raw_chf", precision = 12, scale = 2)
    private BigDecimal pTargetRawChf;

    @Column(name = "p_floor_chf", nullable = false, precision = 12, scale = 2)
    private BigDecimal pFloorChf;

    @Column(name = "p_recommend_chf", precision = 12, scale = 2)
    private BigDecimal pRecommendChf;

    @Column(name = "gross_margin_pct", precision = 6, scale = 2)
    private BigDecimal grossMarginPct;

    @Column(name = "fallback_used", nullable = false)
    private boolean fallbackUsed;

    @Column(name = "f_ch_applied", precision = 6, scale = 3)
    private BigDecimal fChApplied;

    @Column(name = "eur_chf_rate", precision = 10, scale = 6)
    private BigDecimal eurChfRate;

    @Column(length = 16)
    private String confidence;

    @Column(name = "margin_conflict", nullable = false)
    private boolean marginConflict;

    @Column(columnDefinition = "text")
    private String notes;

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setModelYear(Integer modelYear) {
        this.modelYear = modelYear;
    }

    public void setConditionGrade(String conditionGrade) {
        this.conditionGrade = conditionGrade;
    }

    public void setBuyinCostChf(BigDecimal buyinCostChf) {
        this.buyinCostChf = buyinCostChf;
    }

    public void setNCh(int nCh) {
        this.nCh = nCh;
    }

    public void setNDe(int nDe) {
        this.nDe = nDe;
    }

    public void setPMedianChf(BigDecimal pMedianChf) {
        this.pMedianChf = pMedianChf;
    }

    public void setPP25Chf(BigDecimal pP25Chf) {
        this.pP25Chf = pP25Chf;
    }

    public void setPP75Chf(BigDecimal pP75Chf) {
        this.pP75Chf = pP75Chf;
    }

    public void setPTargetRawChf(BigDecimal pTargetRawChf) {
        this.pTargetRawChf = pTargetRawChf;
    }

    public void setPFloorChf(BigDecimal pFloorChf) {
        this.pFloorChf = pFloorChf;
    }

    public void setPRecommendChf(BigDecimal pRecommendChf) {
        this.pRecommendChf = pRecommendChf;
    }

    public void setGrossMarginPct(BigDecimal grossMarginPct) {
        this.grossMarginPct = grossMarginPct;
    }

    public void setFallbackUsed(boolean fallbackUsed) {
        this.fallbackUsed = fallbackUsed;
    }

    public void setFChApplied(BigDecimal fChApplied) {
        this.fChApplied = fChApplied;
    }

    public void setEurChfRate(BigDecimal eurChfRate) {
        this.eurChfRate = eurChfRate;
    }

    public void setConfidence(String confidence) {
        this.confidence = confidence;
    }

    public void setMarginConflict(boolean marginConflict) {
        this.marginConflict = marginConflict;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
