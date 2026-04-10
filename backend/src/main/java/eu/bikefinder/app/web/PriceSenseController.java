package eu.bikefinder.app.web;

import eu.bikefinder.app.service.PriceSenseService;
import eu.bikefinder.app.web.dto.PriceSenseRequest;
import eu.bikefinder.app.web.dto.PriceSenseResponse;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * PatrickBike competitive pricing (PriceSense-aligned): median market CHF, −10% target, 30% margin floor on buy-in.
 * When {@code ebf.pricesense.live-competitor-search.enabled=true}, runs on-demand live checks on competitor shop
 * fronts (see {@code competitor_watch_target}), then falls back to {@code bike_offer} if needed. Optional staff
 * token same as procurement APIs.
 */
@RestController
@RequestMapping(path = "/api/v1/price-sense", produces = MediaType.APPLICATION_JSON_VALUE)
public class PriceSenseController {

    private final PriceSenseService priceSenseService;

    public PriceSenseController(PriceSenseService priceSenseService) {
        this.priceSenseService = priceSenseService;
    }

    @PostMapping(value = "/recommend", consumes = MediaType.APPLICATION_JSON_VALUE)
    public PriceSenseResponse recommend(@Valid @RequestBody PriceSenseRequest body) {
        return priceSenseService.recommend(body);
    }
}
