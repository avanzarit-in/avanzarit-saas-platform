package com.avanzarit.platform.saas.aws.util.region;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;

/**
 * Utility class providing some methods for working or determining the AWS Region the application
 * is running in.
 */
public class RegionUtils {
    /**
     * Attempts to get the current region by consulting multiple sources.
     * <ol>
     * <li>AWS_REGION environment variable</li>
     * <li>REGION environment variable</li>
     * <li>AWS_DEFAULT_REGION environment variable</li>
     * <li>Using {@link Regions#getCurrentRegion()} from the AWS SDK</li>
     * </ol>
     */
    public static Region getCurrentRegion() {
        String regionName = System.getenv("AWS_REGION");

        if (regionName == null) {
            regionName = System.getenv("REGION");
        }

        if (regionName == null) {
            regionName = System.getenv("AWS_DEFAULT_REGION");
        }

        Region region;

        if (regionName == null) {
            region = Regions.getCurrentRegion();
        } else {
            region = Region.getRegion(Regions.fromName(regionName));
        }

        return region;
    }
}
