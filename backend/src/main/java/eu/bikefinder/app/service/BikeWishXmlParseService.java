package eu.bikefinder.app.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class BikeWishXmlParseService {

    private final XmlMapper xmlMapper = new XmlMapper();

    public JsonNode parseToTree(String xml) throws IOException {
        return xmlMapper.readTree(xml.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }

    /** Resolves contact email from Wunsch XML (v1) after Jackson tree mapping. */
    public String extractContactEmail(JsonNode root) {
        if (root == null || root.isNull()) {
            return null;
        }
        JsonNode bikeWish = root.has("bikeWish") ? root.get("bikeWish") : root;
        JsonNode contact = bikeWish.path("contact");
        String email = textOrNull(contact, "email");
        if (email != null) {
            return email.trim();
        }
        return null;
    }

    public String extractContactName(JsonNode root) {
        JsonNode bikeWish = root != null && root.has("bikeWish") ? root.get("bikeWish") : root;
        if (bikeWish == null) {
            return null;
        }
        return textOrNull(bikeWish.path("contact"), "fullName");
    }

    public String extractContactPhone(JsonNode root) {
        JsonNode bikeWish = root != null && root.has("bikeWish") ? root.get("bikeWish") : root;
        if (bikeWish == null) {
            return null;
        }
        return textOrNull(bikeWish.path("contact"), "phone");
    }

    /** Merges denormalised contact into a copy of the tree for storage (JSONB). */
    public JsonNode mergeContactIntoPayload(
            JsonNode root, String email, String name, String phone, ObjectMapper json) {
        if (root == null || root.isNull()) {
            return json.createObjectNode();
        }
        JsonNode copy = root.deepCopy();
        JsonNode bikeWish = copy.has("bikeWish") ? copy.get("bikeWish") : copy;
        if (!bikeWish.isObject()) {
            return copy;
        }
        var obj = (com.fasterxml.jackson.databind.node.ObjectNode) bikeWish;
        var contact = obj.has("contact") && obj.get("contact").isObject()
                ? (com.fasterxml.jackson.databind.node.ObjectNode) obj.get("contact")
                : json.createObjectNode();
        if (email != null && !email.isBlank()) {
            contact.put("email", email.trim());
        }
        if (name != null && !name.isBlank()) {
            contact.put("fullName", name.trim());
        }
        if (phone != null && !phone.isBlank()) {
            contact.put("phone", phone.trim());
        }
        obj.set("contact", contact);
        return copy;
    }

    private static String textOrNull(JsonNode parent, String field) {
        if (parent == null || parent.isMissingNode() || parent.isNull()) {
            return null;
        }
        JsonNode n = parent.get(field);
        if (n == null || n.isNull() || !n.isValueNode()) {
            return null;
        }
        String s = n.asText();
        return s == null || s.isBlank() ? null : s;
    }
}
