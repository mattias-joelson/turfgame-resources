package org.joelson.turf.turfgame.apiv4;

import org.joelson.turf.util.URLReaderTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ZonesTest {
    public static List<Zone> getAllZones() throws IOException {
        return URLReaderTest.readProperties("zones-all.v4.json", Zones::fromJSON);
    }

    @Test
    public void parseAllZones() throws Exception {
        List<Zone> zones = getAllZones();
        assertEquals(131468, zones.size());
    }

    @Test
    public void testName() throws Exception {
        List<Zone> zones = getAllZones();
        Set<String> zoneNames = new HashSet<>(zones.size());
        zones.forEach(zone -> zoneNames.add(zone.getName()));
        assertEquals(131468, zones.size());
        assertEquals(131467, zoneNames.size());
        assertEquals(2, zones.stream().map(Zone::getName)
                .filter(s -> s.contains("FäbodaSwim") || s.contains("CarlislePlaza")).count());
    }
}
