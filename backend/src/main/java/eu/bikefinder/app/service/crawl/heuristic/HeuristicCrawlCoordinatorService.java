package eu.bikefinder.app.service.crawl.heuristic;

import eu.bikefinder.app.config.CrawlProperties;
import eu.bikefinder.app.service.crawl.CrawlRunResult;
import eu.bikefinder.app.service.crawl.ShopifyCrawlCoordinatorService.NamedCrawlRun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** BibiBike / Veloplus (JSON-LD + link regex), Velocorner HTML, and explicit skips for blocked classifieds. */
@Service
public class HeuristicCrawlCoordinatorService {

    private static final Logger log = LoggerFactory.getLogger(HeuristicCrawlCoordinatorService.class);

    private final CrawlProperties crawlProperties;
    private final HeuristicJsonLdLinkCrawlService heuristicJsonLdLinkCrawlService;
    private final VelocornerMarketplaceCrawlService velocornerMarketplaceCrawlService;
    private final ClassifiedsBlockedCrawlService classifiedsBlockedCrawlService;

    public HeuristicCrawlCoordinatorService(
            CrawlProperties crawlProperties,
            HeuristicJsonLdLinkCrawlService heuristicJsonLdLinkCrawlService,
            VelocornerMarketplaceCrawlService velocornerMarketplaceCrawlService,
            ClassifiedsBlockedCrawlService classifiedsBlockedCrawlService) {
        this.crawlProperties = crawlProperties;
        this.heuristicJsonLdLinkCrawlService = heuristicJsonLdLinkCrawlService;
        this.velocornerMarketplaceCrawlService = velocornerMarketplaceCrawlService;
        this.classifiedsBlockedCrawlService = classifiedsBlockedCrawlService;
    }

    public List<NamedCrawlRun> runAllHeuristic(boolean requireMasterSwitch) {
        List<NamedCrawlRun> out = new ArrayList<>();
        for (CrawlProperties.JsonLdLinkTarget t : crawlProperties.getJsonLdLinkTargets()) {
            if (!t.isEnabled()
                    || t.getSourceId() == null
                    || t.getSourceId().isBlank()
                    || t.getSeedUrls() == null
                    || t.getSeedUrls().isEmpty()
                    || t.getLinkRegex() == null
                    || t.getLinkRegex().isBlank()) {
                continue;
            }
            UUID sourceId;
            try {
                sourceId = UUID.fromString(t.getSourceId().trim());
            } catch (IllegalArgumentException e) {
                log.warn("Skipping json-ld-link target with invalid source-id: {}", t.getLabel());
                continue;
            }
            String label = t.getLabel() != null && !t.getLabel().isBlank() ? t.getLabel() : t.getSourceId();
            CrawlRunResult r =
                    heuristicJsonLdLinkCrawlService.crawl(
                            sourceId,
                            label,
                            t.getSeedUrls(),
                            t.getLinkRegex(),
                            t.getMaxProductsPerRun(),
                            t.allowedCurrencySet(),
                            requireMasterSwitch);
            out.add(new NamedCrawlRun(label, r));
        }

        CrawlProperties.VelocornerMarketplace vc = crawlProperties.getVelocorner();
        if (vc.isEnabled() && vc.getSourceId() != null && !vc.getSourceId().isBlank()) {
            try {
                UUID vid = UUID.fromString(vc.getSourceId().trim());
                String vlabel = vc.getLabel() != null && !vc.getLabel().isBlank() ? vc.getLabel() : "Velocorner CH";
                CrawlRunResult vr =
                        velocornerMarketplaceCrawlService.crawl(
                                vid, vlabel, vc.getSeedUrls(), vc.getMaxProductsPerRun(), requireMasterSwitch);
                out.add(new NamedCrawlRun(vlabel, vr));
            } catch (IllegalArgumentException e) {
                log.warn("Skipping Velocorner: invalid source-id");
            }
        }

        out.add(new NamedCrawlRun("Ricardo CH", classifiedsBlockedCrawlService.skipRicardo(requireMasterSwitch)));
        out.add(new NamedCrawlRun("Tutti CH", classifiedsBlockedCrawlService.skipTutti(requireMasterSwitch)));
        out.add(
                new NamedCrawlRun(
                        "Kleinanzeigen DE", classifiedsBlockedCrawlService.skipKleinanzeigen(requireMasterSwitch)));
        return out;
    }
}
