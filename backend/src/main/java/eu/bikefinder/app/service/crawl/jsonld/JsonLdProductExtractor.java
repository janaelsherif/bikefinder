package eu.bikefinder.app.service.crawl.jsonld;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Reads schema.org {@code Product} from {@code script type=application/ld+json} blocks (Shopify storefront).
 */
public final class JsonLdProductExtractor {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final Pattern YEAR_IN_NAME = Pattern.compile("\\((19|20)(\\d{2})\\)");
    private static final Pattern BATTERY_WH = Pattern.compile("Battery:\\s*(\\d+)\\s*Wh", Pattern.CASE_INSENSITIVE);
    private static final Pattern ODOMETER_KM = Pattern.compile("Odometer:\\s*(\\d+)\\s*km", Pattern.CASE_INSENSITIVE);
    private static final Pattern MOTOR_LINE = Pattern.compile("Motor:\\s*([^\\n]+)", Pattern.CASE_INSENSITIVE);

    private JsonLdProductExtractor() {}

    public static Optional<ParsedProduct> extract(String html) {
        Document doc = Jsoup.parse(html);
        String ogTitle = doc.select("meta[property=og:title]").attr("content");
        if (ogTitle != null
                && (ogTitle.contains("404") || ogTitle.toLowerCase(Locale.ROOT).contains("nicht gefunden"))) {
            return Optional.empty();
        }
        String title = doc.title();
        if (title != null && (title.contains("404") || title.toLowerCase(Locale.ROOT).contains("nicht gefunden"))) {
            return Optional.empty();
        }

        List<JsonNode> products = new ArrayList<>();
        for (Element script : doc.select("script[type=application/ld+json]")) {
            String raw = script.data();
            if (raw == null || raw.isBlank()) {
                continue;
            }
            String trimmed = raw.trim();
            if (!trimmed.startsWith("{") && !trimmed.startsWith("[")) {
                continue;
            }
            try {
                JsonNode tree = MAPPER.readTree(trimmed);
                findProductNodes(tree, products);
            } catch (Exception ignored) {
                // Shopify may inject non-JSON placeholders in some themes
            }
        }
        if (products.isEmpty()) {
            return Optional.empty();
        }
        JsonNode p = products.get(0);
        return Optional.of(map(p));
    }

    private static void findProductNodes(JsonNode node, List<JsonNode> out) {
        if (node == null || node.isNull()) {
            return;
        }
        if (node.isArray()) {
            for (JsonNode c : node) {
                findProductNodes(c, out);
            }
            return;
        }
        if (!node.isObject()) {
            return;
        }
        if (isProduct(node)) {
            out.add(node);
        }
        var fields = node.fields();
        while (fields.hasNext()) {
            var e = fields.next();
            findProductNodes(e.getValue(), out);
        }
    }

    private static boolean isProduct(JsonNode o) {
        JsonNode t = o.get("@type");
        if (t == null || t.isNull()) {
            return false;
        }
        if (t.isTextual()) {
            return "Product".equalsIgnoreCase(t.asText());
        }
        if (t.isArray()) {
            for (JsonNode x : t) {
                if (x.isTextual() && "Product".equalsIgnoreCase(x.asText())) {
                    return true;
                }
            }
        }
        return false;
    }

    private static ParsedProduct map(JsonNode product) {
        String name = text(product, "name");
        String brand = brandName(product);
        String sku = text(product, "sku");
        String mpn = text(product, "mpn");
        String description = text(product, "description");
        String category = text(product, "category");
        String image = firstImage(product);
        JsonNode offer = firstOffer(product);
        String priceStr = offer != null ? text(offer, "price") : null;
        String currency = offer != null ? text(offer, "priceCurrency") : null;
        BigDecimal price = null;
        if (priceStr != null && !priceStr.isBlank()) {
            try {
                price = new BigDecimal(priceStr.trim());
            } catch (NumberFormatException ignored) {
                // leave null
            }
        }
        Integer batteryWh = parseIntGroup(description, BATTERY_WH);
        if (batteryWh == null) {
            batteryWh = parseIntGroup(name, BATTERY_WH);
        }
        Integer mileageKm = parseIntGroup(description, ODOMETER_KM);
        String motorBrand = motorBrandFromDescription(description);
        Integer modelYear = extractYear(name);
        String bikeCategory = mapBikeCategory(category, name);
        String model = deriveModel(name, brand);
        return new ParsedProduct(
                name,
                brand,
                model,
                modelYear,
                mpn != null ? mpn.trim() : null,
                sku != null ? sku.trim() : null,
                bikeCategory,
                motorBrand,
                batteryWh,
                mileageKm,
                price,
                currency != null ? currency : "EUR",
                image,
                description);
    }

    private static String deriveModel(String name, String brand) {
        if (name == null || name.isBlank()) {
            return "Unknown";
        }
        String n = name.trim();
        if (brand != null && !brand.isBlank() && n.toLowerCase(Locale.ROOT).startsWith(brand.toLowerCase(Locale.ROOT) + " ")) {
            return n.substring(brand.length()).trim();
        }
        return n;
    }

    private static Integer extractYear(String name) {
        if (name == null) {
            return null;
        }
        Matcher m = YEAR_IN_NAME.matcher(name);
        if (m.find()) {
            try {
                return Integer.parseInt(m.group(1) + m.group(2));
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    private static String motorBrandFromDescription(String description) {
        if (description == null) {
            return null;
        }
        Matcher m = MOTOR_LINE.matcher(description);
        if (!m.find()) {
            return null;
        }
        String line = m.group(1).trim();
        // "Bosch (250 W)" -> Bosch
        int paren = line.indexOf('(');
        if (paren > 0) {
            line = line.substring(0, paren).trim();
        }
        int colon = line.indexOf(':');
        if (colon > 0) {
            line = line.substring(0, colon).trim();
        }
        return line.isEmpty() ? null : line;
    }

    private static Integer parseIntGroup(String text, Pattern p) {
        if (text == null) {
            return null;
        }
        Matcher m = p.matcher(text);
        if (!m.find()) {
            return null;
        }
        try {
            return Integer.parseInt(m.group(1));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    static String mapBikeCategory(String schemaCategory, String name) {
        String blob =
                ((schemaCategory != null ? schemaCategory : "") + " " + (name != null ? name : ""))
                        .toLowerCase(Locale.ROOT);
        if (blob.contains("cargo")) {
            return "cargo";
        }
        if (blob.contains("city") || blob.contains("urban")) {
            return "city";
        }
        if (blob.contains("fully") || blob.contains("full suspension") || blob.contains("enduro")) {
            return "mtb";
        }
        if (blob.contains("mountain") || blob.contains("mtb") || blob.contains("trail")) {
            return "mtb";
        }
        if (blob.contains("trekking") || blob.contains("trek") || blob.contains("allroad")) {
            return "trekking";
        }
        if (blob.contains("gravel") || blob.contains("road") || blob.contains("rennrad")) {
            return "road";
        }
        return "trekking";
    }

    private static String text(JsonNode node, String field) {
        JsonNode v = node.get(field);
        return v != null && v.isTextual() ? v.asText() : null;
    }

    private static String brandName(JsonNode product) {
        JsonNode b = product.get("brand");
        if (b == null || b.isNull()) {
            return null;
        }
        if (b.isTextual()) {
            return b.asText();
        }
        if (b.isObject()) {
            JsonNode n = b.get("name");
            return n != null && n.isTextual() ? n.asText() : null;
        }
        return null;
    }

    private static String firstImage(JsonNode product) {
        JsonNode img = product.get("image");
        if (img == null || img.isNull()) {
            return null;
        }
        if (img.isTextual()) {
            return img.asText();
        }
        if (img.isArray() && !img.isEmpty()) {
            JsonNode first = img.get(0);
            if (first.isTextual()) {
                return first.asText();
            }
            if (first.isObject()) {
                JsonNode u = first.get("url");
                return u != null && u.isTextual() ? u.asText() : null;
            }
        }
        return null;
    }

    private static JsonNode firstOffer(JsonNode product) {
        JsonNode o = product.get("offers");
        if (o == null || o.isNull()) {
            return null;
        }
        if (o.isObject()) {
            return o;
        }
        if (o.isArray() && o.size() > 0) {
            return o.get(0);
        }
        return null;
    }

    public record ParsedProduct(
            String name,
            String brand,
            String model,
            Integer modelYear,
            String mpn,
            String sku,
            String bikeCategory,
            String motorBrand,
            Integer batteryWh,
            Integer mileageKm,
            BigDecimal priceEur,
            String currencyCode,
            String imageUrl,
            String descriptionRaw) {}
}
