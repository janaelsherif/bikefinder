package eu.bikefinder.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ebf.mail")
public class EbfMailProperties {

    /**
     * When true, {@link org.springframework.mail.javamail.JavaMailSender} is configured and alert e-mails are sent.
     */
    private boolean enabled = false;

    /** RFC 5322 From header (name + address). */
    private String from = "EuropeBikeFinder <noreply@localhost>";

    /** Public API base URL for unsubscribe links (no trailing slash), e.g. https://api.example.com */
    private String apiBaseUrl = "http://localhost:8080";

    /** Daily digest schedule (Spring {@code @Scheduled} 6-field cron, seconds first). */
    private String digestCron = "0 0 7 * * *";

    private String digestZone = "Europe/Zurich";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getApiBaseUrl() {
        return apiBaseUrl;
    }

    public void setApiBaseUrl(String apiBaseUrl) {
        this.apiBaseUrl = apiBaseUrl;
    }

    public String getDigestCron() {
        return digestCron;
    }

    public void setDigestCron(String digestCron) {
        this.digestCron = digestCron;
    }

    public String getDigestZone() {
        return digestZone;
    }

    public void setDigestZone(String digestZone) {
        this.digestZone = digestZone;
    }
}
