package eu.bikefinder.app.web.dto;

import eu.bikefinder.app.service.crawl.CrawlRunResult;

import java.util.List;

/** Response for {@code POST /api/v1/system/crawl/shopify-all}. */
public record ShopifyCrawlBatchResponse(List<ShopifyCrawlBatchItem> runs) {

    public record ShopifyCrawlBatchItem(String target, CrawlRunResult result) {}
}
