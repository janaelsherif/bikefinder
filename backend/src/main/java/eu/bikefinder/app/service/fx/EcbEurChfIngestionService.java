package eu.bikefinder.app.service.fx;

import eu.bikefinder.app.domain.FxRate;
import eu.bikefinder.app.repo.FxRateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Service
public class EcbEurChfIngestionService {

    private static final Logger log = LoggerFactory.getLogger(EcbEurChfIngestionService.class);
    public static final String PAIR_EUR_CHF = "EUR_CHF";

    private final RestClient ecbRestClient;
    private final FxRateRepository fxRateRepository;

    public EcbEurChfIngestionService(
            @Qualifier("ecbRestClient") RestClient ecbRestClient,
            FxRateRepository fxRateRepository) {
        this.ecbRestClient = ecbRestClient;
        this.fxRateRepository = fxRateRepository;
    }

    /**
     * Fetches ECB euro foreign exchange reference rates and stores EUR→CHF for the published day.
     *
     * @return {@code true} if a new row was inserted
     */
    @Transactional
    public boolean fetchAndStoreDailyRate() {
        byte[] xml = ecbRestClient.get()
                .uri("/stats/eurofxref/eurofxref-daily.xml")
                .retrieve()
                .body(byte[].class);
        if (xml == null || xml.length == 0) {
            log.warn("ECB returned empty body");
            return false;
        }
        ParsedEcb parsed;
        try {
            parsed = parseEcbXml(xml);
        } catch (Exception e) {
            log.error("Failed to parse ECB XML", e);
            return false;
        }
        if (parsed == null || parsed.eurToChf() == null || parsed.date() == null) {
            log.warn("ECB parse missing CHF rate or date");
            return false;
        }

        BigDecimal rate = parsed.eurToChf().setScale(8, RoundingMode.HALF_UP);
        if (fxRateRepository.existsByCurrencyPairAndEffectiveDate(PAIR_EUR_CHF, parsed.date())) {
            log.debug("FX {} for {} already stored", PAIR_EUR_CHF, parsed.date());
            return false;
        }

        fxRateRepository.save(new FxRate(PAIR_EUR_CHF, rate, parsed.date()));
        log.info("Stored ECB {} = {} effective {}", PAIR_EUR_CHF, rate, parsed.date());
        return true;
    }

    public record ParsedEcb(LocalDate date, BigDecimal eurToChf) {
    }

    public static ParsedEcb parseEcbXml(byte[] xmlBytes) throws Exception {
        var factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        var doc = factory.newDocumentBuilder().parse(new ByteArrayInputStream(xmlBytes));

        LocalDate lastDate = null;
        BigDecimal chf = null;
        var cubes = doc.getElementsByTagName("Cube");
        for (int i = 0; i < cubes.getLength(); i++) {
            var el = (org.w3c.dom.Element) cubes.item(i);
            if (el.hasAttribute("time")) {
                try {
                    lastDate = LocalDate.parse(el.getAttribute("time"));
                } catch (DateTimeParseException ignored) {
                    // ignore
                }
            }
            if (el.hasAttribute("currency") && "CHF".equalsIgnoreCase(el.getAttribute("currency"))) {
                chf = new BigDecimal(el.getAttribute("rate"));
            }
        }
        if (chf == null) {
            return null;
        }
        return new ParsedEcb(lastDate != null ? lastDate : LocalDate.now(), chf);
    }
}
