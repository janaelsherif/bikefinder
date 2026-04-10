package eu.bikefinder.app.web;

import eu.bikefinder.app.service.BikeWishSubmissionService;
import eu.bikefinder.app.web.dto.BikeWishManualRequest;
import eu.bikefinder.app.web.dto.BikeWishSubmissionResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

/**
 * Wunsch-Velo submissions: manual JSON (primary) and XML import (secondary).
 *
 * <p>XML contract: see {@code docs/examples/wunschvelo-example.xml} and
 * {@code docs/PATRICK_WUNSCH_AND_SOURCES.md}.
 */
@RestController
@RequestMapping("/api/v1/bike-wishes")
public class BikeWishController {

    private final BikeWishSubmissionService submissionService;

    public BikeWishController(BikeWishSubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BikeWishSubmissionResponse> submitManual(@Valid @RequestBody BikeWishManualRequest body) {
        BikeWishSubmissionResponse res = submissionService.submitManual(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @PostMapping(
            value = "/xml",
            consumes = {MediaType.APPLICATION_XML_VALUE, "application/xml;charset=UTF-8"},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BikeWishSubmissionResponse> submitXml(@RequestBody String rawXml) {
        try {
            BikeWishSubmissionResponse res = submissionService.submitXml(rawXml);
            return ResponseEntity.status(HttpStatus.CREATED).body(res);
        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Invalid XML: " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }
}
