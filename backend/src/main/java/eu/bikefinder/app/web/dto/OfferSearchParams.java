package eu.bikefinder.app.web.dto;

import java.math.BigDecimal;
import java.util.Locale;

/**
 * Query parameters for {@code GET /api/v1/offers}. All optional; combined with AND.
 * Aligned with PatrickBike Wunsch-Velo criteria that map to {@code bike_offer} columns.
 */
@SuppressWarnings("unused") // Spring MVC binds GET query string to bean properties
public class OfferSearchParams {

    private String brand;
    private String model;
    /** One of: city, trekking, cargo, mtb, road, gravel, kids */
    private String bikeCategory;
    /** One of: new, like_new, refurbished, used */
    private String bikeCondition;
    private String motorBrand;
    /** One of: mid, rear, front */
    private String motorPosition;
    private Integer minBatteryWh;
    private BigDecimal maxLandedPriceChf;
    private BigDecimal minDiscountVsSwissPct;
    private Integer maxMileageKm;
    /** Source country (ISO-2), e.g. DE */
    private String countryCode;
    /** If true, only offers with warranty_type != none */
    private Boolean warrantyPresent;
    /** If true, only is_bargain = true */
    private Boolean bargainOnly;

    /**
     * Listing order: {@code newest}, {@code price_asc}, {@code price_desc}, {@code country_asc},
     * {@code country_desc}. When absent, defaults to newest first.
     */
    private String offerSort;

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getBikeCategory() {
        return bikeCategory;
    }

    public void setBikeCategory(String bikeCategory) {
        this.bikeCategory = bikeCategory;
    }

    public String getBikeCondition() {
        return bikeCondition;
    }

    public void setBikeCondition(String bikeCondition) {
        this.bikeCondition = bikeCondition;
    }

    public String getMotorBrand() {
        return motorBrand;
    }

    public void setMotorBrand(String motorBrand) {
        this.motorBrand = motorBrand;
    }

    public String getMotorPosition() {
        return motorPosition;
    }

    public void setMotorPosition(String motorPosition) {
        this.motorPosition = motorPosition;
    }

    public Integer getMinBatteryWh() {
        return minBatteryWh;
    }

    public void setMinBatteryWh(Integer minBatteryWh) {
        this.minBatteryWh = minBatteryWh;
    }

    public BigDecimal getMaxLandedPriceChf() {
        return maxLandedPriceChf;
    }

    public void setMaxLandedPriceChf(BigDecimal maxLandedPriceChf) {
        this.maxLandedPriceChf = maxLandedPriceChf;
    }

    public BigDecimal getMinDiscountVsSwissPct() {
        return minDiscountVsSwissPct;
    }

    public void setMinDiscountVsSwissPct(BigDecimal minDiscountVsSwissPct) {
        this.minDiscountVsSwissPct = minDiscountVsSwissPct;
    }

    public Integer getMaxMileageKm() {
        return maxMileageKm;
    }

    public void setMaxMileageKm(Integer maxMileageKm) {
        this.maxMileageKm = maxMileageKm;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Boolean getWarrantyPresent() {
        return warrantyPresent;
    }

    public void setWarrantyPresent(Boolean warrantyPresent) {
        this.warrantyPresent = warrantyPresent;
    }

    public Boolean getBargainOnly() {
        return bargainOnly;
    }

    public void setBargainOnly(Boolean bargainOnly) {
        this.bargainOnly = bargainOnly;
    }

    public String getOfferSort() {
        return offerSort;
    }

    public void setOfferSort(String offerSort) {
        this.offerSort = offerSort;
    }

    /** Lowercase sort token, or null when unset. */
    public String normalizedOfferSort() {
        if (offerSort == null || offerSort.isBlank()) {
            return null;
        }
        return offerSort.trim().toLowerCase(Locale.ROOT);
    }
}
