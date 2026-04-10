package eu.bikefinder.app.service;

import eu.bikefinder.app.config.EbfMailProperties;
import eu.bikefinder.app.domain.PriceAlertSubscription;
import eu.bikefinder.app.web.dto.OfferSummaryDto;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

@Service
public class PriceAlertMailService {

    private static final Logger log = LoggerFactory.getLogger(PriceAlertMailService.class);

    private final EbfMailProperties mailProperties;
    private final Optional<JavaMailSender> mailSender;

    public PriceAlertMailService(
            EbfMailProperties mailProperties, @Autowired(required = false) JavaMailSender mailSender) {
        this.mailProperties = mailProperties;
        this.mailSender = Optional.ofNullable(mailSender);
    }

    public void sendWelcome(PriceAlertSubscription sub) {
        if (!mailProperties.isEnabled() || mailSender.isEmpty()) {
            log.debug("Welcome mail skipped (mail disabled or no JavaMailSender)");
            return;
        }
        boolean de = sub.getLocale() != null && sub.getLocale().toLowerCase(Locale.ROOT).startsWith("de");
        String subject = de ? "Benachrichtigung gespeichert — EuropeBikeFinder" : "Alert saved — EuropeBikeFinder";
        String body =
                de
                        ? htmlWelcomeDe(HtmlUtils.htmlEscape(sub.getEmail()))
                        : htmlWelcomeEn(HtmlUtils.htmlEscape(sub.getEmail()));
        send(sub.getEmail(), subject, body);
    }

    public void sendDigest(PriceAlertSubscription sub, List<OfferAlertBatchItem> batch) {
        if (!mailProperties.isEnabled() || mailSender.isEmpty()) {
            return;
        }
        if (batch.isEmpty()) {
            return;
        }
        boolean de = sub.getLocale() != null && sub.getLocale().toLowerCase(Locale.ROOT).startsWith("de");
        String subject =
                de
                        ? "Neue Treffer für Ihre Suche — EuropeBikeFinder"
                        : "New matches for your search — EuropeBikeFinder";
        String unsub = unsubscribeUrl(sub.getUnsubscribeToken());
        String body =
                de
                        ? htmlDigestDe(batch, unsub, mailProperties.getApiBaseUrl())
                        : htmlDigestEn(batch, unsub, mailProperties.getApiBaseUrl());
        send(sub.getEmail(), subject, body);
    }

    private String unsubscribeUrl(java.util.UUID token) {
        String base = mailProperties.getApiBaseUrl().replaceAll("/+$", "");
        return base + "/api/v1/alert-subscriptions/unsubscribe?token=" + token;
    }

    private void send(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.get().createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_NO, StandardCharsets.UTF_8.name());
            helper.setFrom(mailProperties.getFrom());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.get().send(message);
            log.info("Sent mail to={} subject={}", to, subject);
        } catch (Exception e) {
            log.error("Failed to send mail to={}", to, e);
            throw new IllegalStateException("Mail send failed", e);
        }
    }

    private static String htmlWelcomeDe(String emailEsc) {
        return "<!DOCTYPE html><html><body style=\"font-family:system-ui,sans-serif;font-size:15px;color:#111;\">"
                + "<p>Hallo,</p>"
                + "<p>wir haben Ihre Benachrichtigung für die gespeicherten Suchfilter unter <strong>"
                + emailEsc
                + "</strong> registriert.</p>"
                + "<p>Sobald passende neue Inserate erscheinen, erhalten Sie eine kurze E-Mail mit den Treffern.</p>"
                + "<p style=\"color:#666;font-size:13px;\">EuropeBikeFinder</p>"
                + "</body></html>";
    }

    private static String htmlWelcomeEn(String emailEsc) {
        return "<!DOCTYPE html><html><body style=\"font-family:system-ui,sans-serif;font-size:15px;color:#111;\">"
                + "<p>Hello,</p>"
                + "<p>We saved your alert for the current search filters for <strong>"
                + emailEsc
                + "</strong>.</p>"
                + "<p>When new matching listings appear, we will email you a short digest.</p>"
                + "<p style=\"color:#666;font-size:13px;\">EuropeBikeFinder</p>"
                + "</body></html>";
    }

    private static String htmlDigestDe(List<OfferAlertBatchItem> batch, String unsubscribeUrl, String siteNote) {
        StringBuilder rows = new StringBuilder();
        DecimalFormat chf = chfFormat(Locale.GERMAN);
        for (OfferAlertBatchItem item : batch) {
            OfferSummaryDto o = item.offer();
            String title = HtmlUtils.htmlEscape(Objects.toString(o.brand(), "") + " " + Objects.toString(o.model(), ""));
            String price = formatChf(o.landedPriceChf(), chf);
            String url = HtmlUtils.htmlEscape(o.sourceUrl());
            rows.append("<tr><td style=\"padding:8px;border-bottom:1px solid #eee;\">")
                    .append(title)
                    .append("</td><td style=\"padding:8px;border-bottom:1px solid #eee;\">")
                    .append(price)
                    .append("</td><td style=\"padding:8px;border-bottom:1px solid #eee;\">")
                    .append("<a href=\"")
                    .append(url)
                    .append("\">Anzeige</a>")
                    .append("</td></tr>");
        }
        return "<!DOCTYPE html><html><body style=\"font-family:system-ui,sans-serif;font-size:15px;color:#111;\">"
                + "<p>Neue Treffer:</p>"
                + "<table style=\"border-collapse:collapse;width:100%;max-width:640px;\">"
                + "<thead><tr><th align=\"left\">Inserat</th><th align=\"left\">CHF</th><th align=\"left\">Link</th></tr></thead>"
                + "<tbody>"
                + rows
                + "</tbody></table>"
                + "<p style=\"margin-top:24px;font-size:13px;color:#555;\"><a href=\""
                + HtmlUtils.htmlEscape(unsubscribeUrl)
                + "\">Benachrichtigung abbestellen</a></p>"
                + "<p style=\"font-size:12px;color:#999;\">"
                + HtmlUtils.htmlEscape(siteNote)
                + "</p>"
                + "</body></html>";
    }

    private static String htmlDigestEn(List<OfferAlertBatchItem> batch, String unsubscribeUrl, String siteNote) {
        StringBuilder rows = new StringBuilder();
        DecimalFormat chf = chfFormat(Locale.US);
        for (OfferAlertBatchItem item : batch) {
            OfferSummaryDto o = item.offer();
            String title = HtmlUtils.htmlEscape(Objects.toString(o.brand(), "") + " " + Objects.toString(o.model(), ""));
            String price = formatChf(o.landedPriceChf(), chf);
            String url = HtmlUtils.htmlEscape(o.sourceUrl());
            rows.append("<tr><td style=\"padding:8px;border-bottom:1px solid #eee;\">")
                    .append(title)
                    .append("</td><td style=\"padding:8px;border-bottom:1px solid #eee;\">")
                    .append(price)
                    .append("</td><td style=\"padding:8px;border-bottom:1px solid #eee;\">")
                    .append("<a href=\"")
                    .append(url)
                    .append("\">Listing</a>")
                    .append("</td></tr>");
        }
        return "<!DOCTYPE html><html><body style=\"font-family:system-ui,sans-serif;font-size:15px;color:#111;\">"
                + "<p>New matches:</p>"
                + "<table style=\"border-collapse:collapse;width:100%;max-width:640px;\">"
                + "<thead><tr><th align=\"left\">Listing</th><th align=\"left\">CHF</th><th align=\"left\">Link</th></tr></thead>"
                + "<tbody>"
                + rows
                + "</tbody></table>"
                + "<p style=\"margin-top:24px;font-size:13px;color:#555;\"><a href=\""
                + HtmlUtils.htmlEscape(unsubscribeUrl)
                + "\">Unsubscribe</a></p>"
                + "<p style=\"font-size:12px;color:#999;\">"
                + HtmlUtils.htmlEscape(siteNote)
                + "</p>"
                + "</body></html>";
    }

    private static DecimalFormat chfFormat(Locale locale) {
        DecimalFormatSymbols sym = DecimalFormatSymbols.getInstance(locale);
        DecimalFormat df = new DecimalFormat("#,##0.00", sym);
        return df;
    }

    private static String formatChf(BigDecimal v, DecimalFormat df) {
        if (v == null) {
            return "—";
        }
        return df.format(v) + " CHF";
    }
}
