package org.joelson.turf.turfgame.apiv4;

import org.joelson.turf.util.URLReaderTest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RegionsTest {

    public static List<Region> getRegions() throws Exception {
        return URLReaderTest.readProperties("regions.json", Regions::fromJSON);
    }

    public static List<Region> getAllRegions() throws Exception {
        return URLReaderTest.readProperties("regions-all.json", Regions::fromJSON);
    }

    @Test
    public void parseRegions() throws Exception {
        List<Region> regions = getRegions();
        assertEquals(328, regions.size());
    }

    @Test
    public void parseAllRegions() throws Exception {
        List<Region> regions = getAllRegions();
        assertEquals(330, regions.size());
    }
}
