package org.joelson.turf.turfgame.apiv5;

import org.joelson.turf.util.URLReaderTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ZonesTest {

    public static List<Zone> getAllZones() throws IOException {
        return URLReaderTest.readProperties("zones-all.v5.json", Zones::fromJSON);
    }

    @Test
    public void parseAllZones() throws Exception {
        List<Zone> zones = getAllZones();
        assertEquals(131468, zones.size());
    }

    @Test
    public void generateStockholmTakeMap() throws Exception {
        List<Zone> zones = getAllZones();
        List<Zone> stockholmZones = zones.stream()
                .filter(zone -> zone.getRegion() != null && zone.getRegion().getId() == 141).toList();
        Set<String> municipalities = new HashSet<>();
        stockholmZones.stream().map(Zone::getRegion).map(Region::getArea).map(Area::getName)
                .forEach(municipalities::add);
        assertEquals(26, municipalities.size());
    }
}
