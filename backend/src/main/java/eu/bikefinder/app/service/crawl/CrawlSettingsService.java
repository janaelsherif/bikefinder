package eu.bikefinder.app.service.crawl;

import eu.bikefinder.app.domain.CrawlSettings;
import eu.bikefinder.app.repo.CrawlSettingsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

@Service
public class CrawlSettingsService {

    private static final DateTimeFormatter HH_MM = DateTimeFormatter.ofPattern("HH:mm", Locale.ROOT);
    private static final String DEFAULT_ZONE = "Europe/Zurich";

    private final CrawlSettingsRepository crawlSettingsRepository;

    public CrawlSettingsService(CrawlSettingsRepository crawlSettingsRepository) {
        this.crawlSettingsRepository = crawlSettingsRepository;
    }

    @Transactional(readOnly = true)
    public CrawlSettings getCurrent() {
        return crawlSettingsRepository.findById(CrawlSettings.SINGLETON_ID).orElseGet(this::createDefault);
    }

    @Transactional
    public CrawlSettings update(Boolean autoCrawlEnabled, String autoCrawlTime, String timezone) {
        CrawlSettings settings = crawlSettingsRepository.findById(CrawlSettings.SINGLETON_ID).orElseGet(this::createDefault);
        if (autoCrawlEnabled != null) {
            settings.setAutoCrawlEnabled(autoCrawlEnabled);
        }
        if (autoCrawlTime != null) {
            settings.setAutoCrawlTime(parseAutoCrawlTime(autoCrawlTime));
        }
        if (timezone != null) {
            settings.setTimezone(validateTimezone(timezone));
        }
        return crawlSettingsRepository.save(settings);
    }

    @Transactional
    public void markAutoRunAt(Instant when) {
        CrawlSettings settings = crawlSettingsRepository.findById(CrawlSettings.SINGLETON_ID).orElseGet(this::createDefault);
        settings.setLastAutoRunAt(when);
        crawlSettingsRepository.save(settings);
    }

    @Transactional(readOnly = true)
    public boolean shouldRunNow(ZonedDateTime now, CrawlSettings settings) {
        if (!settings.isAutoCrawlEnabled()) {
            return false;
        }
        ZoneId zone = ZoneId.of(settings.getTimezone());
        ZonedDateTime zonedNow = now.withZoneSameInstant(zone);
        LocalTime scheduled = settings.getAutoCrawlTime().withSecond(0).withNano(0);
        if (zonedNow.getHour() != scheduled.getHour() || zonedNow.getMinute() != scheduled.getMinute()) {
            return false;
        }
        Instant lastAutoRunAt = settings.getLastAutoRunAt();
        if (lastAutoRunAt == null) {
            return true;
        }
        LocalDate lastRunDate = lastAutoRunAt.atZone(zone).toLocalDate();
        return !lastRunDate.equals(zonedNow.toLocalDate());
    }

    public static String formatAutoCrawlTime(LocalTime value) {
        return value == null ? null : value.format(HH_MM);
    }

    public static LocalTime parseAutoCrawlTime(String value) {
        if (value == null) {
            throw new IllegalArgumentException("autoCrawlTime is required");
        }
        String trimmed = value.trim();
        try {
            return LocalTime.parse(trimmed, HH_MM).withSecond(0).withNano(0);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("autoCrawlTime must use HH:mm format");
        }
    }

    public static String validateTimezone(String value) {
        if (value == null || value.isBlank()) {
            return DEFAULT_ZONE;
        }
        String trimmed = value.trim();
        try {
            ZoneId.of(trimmed);
            return trimmed;
        } catch (Exception ex) {
            throw new IllegalArgumentException("timezone must be a valid IANA zone ID");
        }
    }

    private CrawlSettings createDefault() {
        CrawlSettings created = new CrawlSettings();
        created.setAutoCrawlEnabled(false);
        created.setAutoCrawlTime(LocalTime.of(3, 0));
        created.setTimezone(DEFAULT_ZONE);
        return crawlSettingsRepository.save(created);
    }
}
