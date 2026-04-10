package eu.bikefinder.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "ebf.pricing")
public class PricingProperties {

    /**
     * Minimum discount vs Swiss median (%) to mark {@code is_bargain}.
     */
    private BigDecimal bargainThresholdPercent = new BigDecimal("15");

    /**
     * Swiss import VAT estimate applies above this product value in CHF (spec / dev spec).
     */
    private BigDecimal importVatThresholdChf = new BigDecimal("150");

    private BigDecimal importVatRate = new BigDecimal("0.077");

    private BigDecimal customsHandlingChf = new BigDecimal("20");

    /**
     * Default shipping estimate (CHF) when not overridden per country.
     */
    private BigDecimal defaultShippingEstimateChf = new BigDecimal("40");

    /**
     * ISO country code → shipping estimate CHF (Germany refurbishers often ship; still conservative default).
     */
    private Map<String, BigDecimal> shippingEstimateChfByCountry = new HashMap<>(Map.of(
            "DE", new BigDecimal("40"),
            "FR", new BigDecimal("80"),
            "NL", new BigDecimal("80"),
            "IT", new BigDecimal("100"),
            "AT", new BigDecimal("60")
    ));

    public BigDecimal getBargainThresholdPercent() {
        return bargainThresholdPercent;
    }

    public void setBargainThresholdPercent(BigDecimal bargainThresholdPercent) {
        this.bargainThresholdPercent = bargainThresholdPercent;
    }

    public BigDecimal getImportVatThresholdChf() {
        return importVatThresholdChf;
    }

    public void setImportVatThresholdChf(BigDecimal importVatThresholdChf) {
        this.importVatThresholdChf = importVatThresholdChf;
    }

    public BigDecimal getImportVatRate() {
        return importVatRate;
    }

    public void setImportVatRate(BigDecimal importVatRate) {
        this.importVatRate = importVatRate;
    }

    public BigDecimal getCustomsHandlingChf() {
        return customsHandlingChf;
    }

    public void setCustomsHandlingChf(BigDecimal customsHandlingChf) {
        this.customsHandlingChf = customsHandlingChf;
    }

    public BigDecimal getDefaultShippingEstimateChf() {
        return defaultShippingEstimateChf;
    }

    public void setDefaultShippingEstimateChf(BigDecimal defaultShippingEstimateChf) {
        this.defaultShippingEstimateChf = defaultShippingEstimateChf;
    }

    public Map<String, BigDecimal> getShippingEstimateChfByCountry() {
        return shippingEstimateChfByCountry;
    }

    public void setShippingEstimateChfByCountry(Map<String, BigDecimal> shippingEstimateChfByCountry) {
        this.shippingEstimateChfByCountry = shippingEstimateChfByCountry;
    }
}
