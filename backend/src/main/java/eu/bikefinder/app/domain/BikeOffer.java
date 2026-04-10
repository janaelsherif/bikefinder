package eu.bikefinder.app.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@DynamicInsert
@Table(name = "bike_offer")
public class BikeOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "source_id", nullable = false)
    private Source source;

    @Column(name = "source_offer_id", nullable = false, length = 200)
    private String sourceOfferId;

    @Column(name = "source_url", nullable = false)
    private String sourceUrl;

    @Column(nullable = false, length = 16)
    private String status = "active";

    @Column(length = 100)
    private String brand;

    @Column(length = 200)
    private String model;

    @Column(name = "model_line", length = 100)
    private String modelLine;

    @Column(name = "bike_category", length = 32)
    private String bikeCategory;

    @Column(name = "frame_type", length = 16)
    private String frameType;

    @Column(name = "frame_size", length = 20)
    private String frameSize;

    @Column(name = "wheel_size_inch", precision = 4, scale = 1)
    private BigDecimal wheelSizeInch;

    @Column(name = "drivetrain_type", length = 16)
    private String drivetrainType;

    @Column(name = "gears_count")
    private Integer gearsCount;

    @Column(length = 100)
    private String groupset;

    @Column(name = "motor_brand", length = 100)
    private String motorBrand;

    @Column(name = "motor_model", length = 100)
    private String motorModel;

    @Column(name = "motor_position", length = 16)
    private String motorPosition;

    @Column(name = "motor_power_w")
    private Integer motorPowerW;

    @Column(name = "battery_wh")
    private Integer batteryWh;

    @Column(name = "battery_cycles")
    private Integer batteryCycles;

    @Column(name = "range_estimate_km")
    private Integer rangeEstimateKm;

    @Column(name = "model_year")
    private Integer modelYear;

    @Column(name = "mileage_km")
    private Integer mileageKm;

    @Column(name = "bike_condition", nullable = false, length = 32)
    private String bikeCondition;

    @Column(name = "refurbisher_name", length = 100)
    private String refurbisherName;

    @Column(name = "warranty_type", nullable = false, length = 32)
    private String warrantyType = "none";

    @Column(name = "warranty_months")
    private Integer warrantyMonths;

    @Column(name = "list_price_local", precision = 10, scale = 2)
    private BigDecimal listPriceLocal;

    @Column(name = "currency_code", nullable = false, length = 3)
    private String currencyCode = "EUR";

    @Column(name = "fees_local", precision = 10, scale = 2)
    private BigDecimal feesLocal;

    @Column(name = "shipping_cost_local", precision = 10, scale = 2)
    private BigDecimal shippingCostLocal;

    @Column(name = "total_price_local", precision = 10, scale = 2)
    private BigDecimal totalPriceLocal;

    @Column(name = "price_chf", precision = 10, scale = 2)
    private BigDecimal priceChf;

    @Column(name = "shipping_estimate_chf", precision = 10, scale = 2)
    private BigDecimal shippingEstimateChf;

    @Column(name = "import_surcharge_chf", precision = 10, scale = 2)
    private BigDecimal importSurchargeChf;

    @Column(name = "landed_price_chf", precision = 10, scale = 2)
    private BigDecimal landedPriceChf;

    @Column(name = "swiss_median_price_chf", precision = 10, scale = 2)
    private BigDecimal swissMedianPriceChf;

    @Column(name = "discount_vs_swiss_pct", precision = 5, scale = 2)
    private BigDecimal discountVsSwissPct;

    @Column(name = "is_bargain", nullable = false)
    private boolean bargain;

    @Column(name = "quality_score", precision = 3, scale = 1)
    private BigDecimal qualityScore;

    @Column(name = "description_raw")
    private String descriptionRaw;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[]")
    private String[] images;

    @Column(name = "first_seen_at")
    private Instant firstSeenAt;

    @Column(name = "last_seen_at")
    private Instant lastSeenAt;

    @Column(name = "extraction_method", nullable = false, length = 16)
    private String extractionMethod = "heuristic";

    /** Flyway demo / placeholder rows — hidden from staff wish search when {@code ebf.search.include-demo-listings=false}. */
    @Column(name = "is_demo", nullable = false)
    private boolean demo;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public UUID getId() {
        return id;
    }

    public Source getSource() {
        return source;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public Integer getModelYear() {
        return modelYear;
    }

    public String getBikeCategory() {
        return bikeCategory;
    }

    public String getBikeCondition() {
        return bikeCondition;
    }

    public String getMotorBrand() {
        return motorBrand;
    }

    public Integer getBatteryWh() {
        return batteryWh;
    }

    public Integer getMileageKm() {
        return mileageKm;
    }

    public String getWarrantyType() {
        return warrantyType;
    }

    public Integer getWarrantyMonths() {
        return warrantyMonths;
    }

    public BigDecimal getLandedPriceChf() {
        return landedPriceChf;
    }

    public BigDecimal getDiscountVsSwissPct() {
        return discountVsSwissPct;
    }

    public boolean isBargain() {
        return bargain;
    }

    public BigDecimal getQualityScore() {
        return qualityScore;
    }

    public boolean isDemo() {
        return demo;
    }

    public void setDemo(boolean demo) {
        this.demo = demo;
    }

    public String[] getImages() {
        return images;
    }

    public Instant getFirstSeenAt() {
        return firstSeenAt;
    }

    public BigDecimal getTotalPriceLocal() {
        return totalPriceLocal;
    }

    public BigDecimal getListPriceLocal() {
        return listPriceLocal;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    /** Writes computed CHF pricing and Swiss comparison (PricingService). */
    public void applyComputedPricing(
            BigDecimal priceChf,
            BigDecimal shippingEstimateChf,
            BigDecimal importSurchargeChf,
            BigDecimal landedPriceChf,
            BigDecimal swissMedianPriceChf,
            BigDecimal discountVsSwissPct,
            boolean bargain) {
        this.priceChf = priceChf;
        this.shippingEstimateChf = shippingEstimateChf;
        this.importSurchargeChf = importSurchargeChf;
        this.landedPriceChf = landedPriceChf;
        this.swissMedianPriceChf = swissMedianPriceChf;
        this.discountVsSwissPct = discountVsSwissPct;
        this.bargain = bargain;
    }

    /** Manual / dev import row (e.g. JSON ingest). After persist, run PricingService.repriceOffer. */
    public static BikeOffer createImported(
            Source source,
            String sourceOfferId,
            String sourceUrl,
            String brand,
            String model,
            Integer modelYear,
            String bikeCategory,
            String bikeCondition,
            String motorBrand,
            String motorPosition,
            Integer batteryWh,
            Integer mileageKm,
            String warrantyType,
            Integer warrantyMonths,
            BigDecimal totalPriceLocal,
            String currencyCode,
            String extractionMethod,
            String[] images,
            BigDecimal qualityScore) {
        BikeOffer o = new BikeOffer();
        o.source = Objects.requireNonNull(source);
        o.sourceOfferId = Objects.requireNonNull(sourceOfferId);
        o.sourceUrl = Objects.requireNonNull(sourceUrl);
        o.status = "active";
        o.brand = brand;
        o.model = model;
        o.modelYear = modelYear;
        o.bikeCategory = bikeCategory;
        o.bikeCondition = Objects.requireNonNull(bikeCondition);
        o.motorBrand = motorBrand;
        o.motorPosition = motorPosition;
        o.batteryWh = batteryWh;
        o.mileageKm = mileageKm;
        o.warrantyType = warrantyType != null ? warrantyType : "none";
        o.warrantyMonths = warrantyMonths;
        o.listPriceLocal = totalPriceLocal;
        o.totalPriceLocal = totalPriceLocal;
        o.currencyCode = currencyCode != null ? currencyCode : "EUR";
        o.extractionMethod = extractionMethod != null ? extractionMethod : "manual";
        o.images = images;
        o.qualityScore = qualityScore;
        Instant now = Instant.now();
        o.firstSeenAt = now;
        o.lastSeenAt = now;
        o.bargain = false;
        o.demo = false;
        return o;
    }
}
