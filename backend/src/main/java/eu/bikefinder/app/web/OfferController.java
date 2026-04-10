package eu.bikefinder.app.web;

import eu.bikefinder.app.service.OfferQueryService;
import eu.bikefinder.app.web.dto.OfferSearchParams;
import eu.bikefinder.app.web.dto.OfferSummaryDto;
import eu.bikefinder.app.web.dto.WishSearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * {@code GET /api/v1/offers} — optional query params (see {@link OfferSearchParams}) filter active
 * offers; same DTO as unfiltered list. Used by the staff “Kundenwunsch” search UI.
 */
@RestController
@RequestMapping("/api/v1/offers")
public class OfferController {

    private final OfferQueryService offerQueryService;

    public OfferController(OfferQueryService offerQueryService) {
        this.offerQueryService = offerQueryService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(
                BigDecimal.class,
                new PropertyEditorSupport() {
                    @Override
                    public void setAsText(String text) {
                        if (text == null || text.isBlank()) {
                            setValue(null);
                        } else {
                            setValue(new BigDecimal(text.trim()));
                        }
                    }
                });
        binder.registerCustomEditor(
                Integer.class,
                new PropertyEditorSupport() {
                    @Override
                    public void setAsText(String text) {
                        if (text == null || text.isBlank()) {
                            setValue(null);
                        } else {
                            setValue(Integer.valueOf(text.trim()));
                        }
                    }
                });
    }

    @GetMapping
    public Page<OfferSummaryDto> list(
            @ModelAttribute OfferSearchParams searchParams,
            @PageableDefault(size = 20, sort = "firstSeenAt", direction = Sort.Direction.DESC)
                    Pageable pageable) {
        return offerQueryService.search(searchParams, pageable);
    }

    /** Staff wish search with near-match fallback when strict filters return nothing. */
    @GetMapping("/wish")
    public WishSearchResponse wish(
            @ModelAttribute OfferSearchParams searchParams,
            @PageableDefault(size = 24, sort = "firstSeenAt", direction = Sort.Direction.DESC)
                    Pageable pageable) {
        return offerQueryService.searchWish(searchParams, pageable);
    }

    /**
     * Comparable listings without embeddings: brand/model/category/motor, landed CHF band, year ±2.
     * When {@code EBF_STAFF_API_TOKEN} is set, same token rule as {@code GET /offers}.
     */
    @GetMapping("/{id}/similar")
    public Page<OfferSummaryDto> similar(
            @PathVariable UUID id,
            @PageableDefault(size = 12, sort = "firstSeenAt", direction = Sort.Direction.DESC)
                    Pageable pageable) {
        return offerQueryService.searchSimilarOffers(id, pageable);
    }
}
