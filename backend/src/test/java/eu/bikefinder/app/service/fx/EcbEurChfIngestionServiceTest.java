package eu.bikefinder.app.service.fx;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class EcbEurChfIngestionServiceTest {

    @Test
    void parseEcbXml_extractsChfAndDate() throws Exception {
        var xml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <gesmes:Envelope xmlns:gesmes="http://www.gesmes.org/xml/2002-08-01" xmlns="http://www.ecb.int/vocabulary/2002-08-01/eurofxref">
                <Cube><Cube time='2024-06-01'>
                <Cube currency='USD' rate='1.08'/>
                <Cube currency='CHF' rate='0.9654'/>
                </Cube></Cube></gesmes:Envelope>
                """.getBytes(StandardCharsets.UTF_8);

        EcbEurChfIngestionService.ParsedEcb parsed = EcbEurChfIngestionService.parseEcbXml(xml);

        assertThat(parsed).isNotNull();
        assertThat(parsed.date()).isEqualTo(LocalDate.of(2024, 6, 1));
        assertThat(parsed.eurToChf()).isEqualByComparingTo(new BigDecimal("0.9654"));
    }
}
