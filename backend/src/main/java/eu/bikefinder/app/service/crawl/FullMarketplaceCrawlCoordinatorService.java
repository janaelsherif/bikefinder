package eu.bikefinder.app.service.crawl;

import eu.bikefinder.app.service.crawl.heuristic.HeuristicCrawlCoordinatorService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/** Shopify crawls + heuristic / HTML marketplace crawls + classified placeholders. */
@Service
public class FullMarketplaceCrawlCoordinatorService {

    private final ShopifyCrawlCoordinatorService shopifyCrawlCoordinatorService;
    private final HeuristicCrawlCoordinatorService heuristicCrawlCoordinatorService;

    public FullMarketplaceCrawlCoordinatorService(
            ShopifyCrawlCoordinatorService shopifyCrawlCoordinatorService,
            HeuristicCrawlCoordinatorService heuristicCrawlCoordinatorService) {
        this.shopifyCrawlCoordinatorService = shopifyCrawlCoordinatorService;
        this.heuristicCrawlCoordinatorService = heuristicCrawlCoordinatorService;
    }

    public List<ShopifyCrawlCoordinatorService.NamedCrawlRun> runEverything(boolean requireMasterSwitch) {
        List<ShopifyCrawlCoordinatorService.NamedCrawlRun> all = new ArrayList<>();
        all.addAll(shopifyCrawlCoordinatorService.runAllConfiguredShopify(requireMasterSwitch));
        all.addAll(heuristicCrawlCoordinatorService.runAllHeuristic(requireMasterSwitch));
        return all;
    }
}
