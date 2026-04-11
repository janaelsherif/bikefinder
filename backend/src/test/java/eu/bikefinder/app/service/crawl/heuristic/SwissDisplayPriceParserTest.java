package eu.bikefinder.app.service.crawl.heuristic;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SwissDisplayPriceParserTest {

    @Test
    void parsesApostropheThousands() {
        assertEquals(0, new BigDecimal("1500").compareTo(SwissDisplayPriceParser.parseFirstChf("CHF 1'500.-")));
    }

    @Test
    void parsesWithDecimals() {
        assertEquals(new BigDecimal("3599.10"), SwissDisplayPriceParser.parseFirstChf("CHF 3'599.10"));
    }

    @Test
    void nullForEmpty() {
        assertNull(SwissDisplayPriceParser.parseFirstChf(""));
    }
}
