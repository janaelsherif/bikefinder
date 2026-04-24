package eu.bikefinder.app;

import java.util.Set;

/**
 * Markets treated as “nearby” to Switzerland for discovery / Velo-news style feeds (ISO-3166-1 alpha-2).
 */
public final class DiscoveryRegion {

    /** CH + adjacent / high-volume EU sourcing corridors used in procurement research. */
    public static final Set<String> NEAR_SWITZERLAND_ISO2 =
            Set.of("CH", "DE", "AT", "FR", "IT", "NL");

    private DiscoveryRegion() {}
}
