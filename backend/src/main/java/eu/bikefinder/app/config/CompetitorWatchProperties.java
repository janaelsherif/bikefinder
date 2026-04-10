package eu.bikefinder.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Scheduled HTTP snapshot of Swiss competitor shop fronts (Hamza / VeloIntel “competitor watch”).
 */
@ConfigurationProperties(prefix = "ebf.competitor-watch")
public class CompetitorWatchProperties {

    private boolean enabled = false;

    /** Log at WARN when absolute delta vs previous snapshot exceeds this (inventory signal). */
    private int alertDeltaLogThreshold = 5;

    /** Monday 08:00 Zurich — align with VeloIntel-style weekly digest; override via env. */
    private String cron = "0 0 8 * * MON";

    private String zone = "Europe/Zurich";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getAlertDeltaLogThreshold() {
        return alertDeltaLogThreshold;
    }

    public void setAlertDeltaLogThreshold(int alertDeltaLogThreshold) {
        this.alertDeltaLogThreshold = alertDeltaLogThreshold;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }
}
