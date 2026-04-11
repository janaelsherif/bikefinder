package eu.bikefinder.app.service.crawl.heuristic;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Parses prices like {@code CHF 1'500.-} from Velocorner-style HTML text. */
public final class SwissDisplayPriceParser {

    private static final Pattern PRIMARY =
            Pattern.compile("CHF\\s*([0-9][0-9''\\s]*)(?:\\.-|\\.([0-9]{2}))?", Pattern.CASE_INSENSITIVE);

    private SwissDisplayPriceParser() {}

    /** Returns the first CHF amount found, or null. */
    public static BigDecimal parseFirstChf(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        String normalized =
                text.replace("\u00a0", " ")
                        .replace("&#x27;", "'")
                        .replace("&#39;", "'")
                        .trim();
        Matcher m = PRIMARY.matcher(normalized);
        if (!m.find()) {
            return null;
        }
        String intPart = m.group(1).replace("'", "").replace("’", "").replaceAll("\\s+", "");
        String dec = m.group(2);
        try {
            if (dec != null) {
                return new BigDecimal(intPart + "." + dec);
            }
            return new BigDecimal(intPart);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
