package org.joelson.turf.statistics;

import org.joelson.turf.turfgame.apiv4.RegionsTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StatisticsTest {

    @Test
    public void testImportRegions() throws Exception {
        Statistics statistics = new Statistics();
        statistics.importRegions(RegionsTest.getRegions());
        assertEquals(164, statistics.getCountries().size());
        assertEquals(328, statistics.getRegions().size());
    }
}
