package eu.bikefinder.app.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.bikefinder.app.domain.BikeWishSubmission;
import eu.bikefinder.app.repo.BikeWishSubmissionRepository;
import eu.bikefinder.app.web.dto.BikeWishManualRequest;
import eu.bikefinder.app.web.dto.BikeWishSubmissionResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BikeWishSubmissionService {

    public static final String SOURCE_WEB_MANUAL = "WEB_MANUAL";
    public static final String SOURCE_XML_IMPORT = "XML_IMPORT";

    private final BikeWishSubmissionRepository repository;
    private final BikeWishXmlParseService xmlParseService;
    private final ObjectMapper objectMapper;

    public BikeWishSubmissionService(
            BikeWishSubmissionRepository repository,
            BikeWishXmlParseService xmlParseService,
            ObjectMapper objectMapper) {
        this.repository = repository;
        this.xmlParseService = xmlParseService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public BikeWishSubmissionResponse submitManual(BikeWishManualRequest request) {
        var entity = new BikeWishSubmission();
        entity.setSubmissionSource(SOURCE_WEB_MANUAL);
        entity.setContactEmail(request.contactEmail().trim());
        entity.setContactName(trimOrNull(request.contactName()));
        entity.setContactPhone(trimOrNull(request.contactPhone()));
        entity.setPayloadJson(request.payload());
        entity.setRawXmlImport(null);
        BikeWishSubmission saved = repository.save(entity);
        return new BikeWishSubmissionResponse(saved.getId(), saved.getCreatedAt());
    }

    @Transactional
    public BikeWishSubmissionResponse submitXml(String rawXml) throws java.io.IOException {
        JsonNode tree = xmlParseService.parseToTree(rawXml);
        String email = xmlParseService.extractContactEmail(tree);
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "XML must contain contact/email (under bikeWish/contact/email).");
        }
        String name = xmlParseService.extractContactName(tree);
        String phone = xmlParseService.extractContactPhone(tree);
        JsonNode payload =
                xmlParseService.mergeContactIntoPayload(tree, email, name, phone, objectMapper);

        var entity = new BikeWishSubmission();
        entity.setSubmissionSource(SOURCE_XML_IMPORT);
        entity.setContactEmail(email);
        entity.setContactName(trimOrNull(name));
        entity.setContactPhone(trimOrNull(phone));
        entity.setPayloadJson(payload);
        entity.setRawXmlImport(rawXml);
        BikeWishSubmission saved = repository.save(entity);
        return new BikeWishSubmissionResponse(saved.getId(), saved.getCreatedAt());
    }

    private static String trimOrNull(String s) {
        if (s == null) {
            return null;
        }
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
