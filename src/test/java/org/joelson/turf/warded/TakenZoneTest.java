package org.joelson.turf.warded;

import org.joelson.turf.util.URLReaderTest;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TakenZoneTest {

    public static Map<String, Integer> readTakenZones() throws Exception {
        return URLReaderTest.readProperties("warded.unique.php.html", TakenZones::fromHTML);
    }

    @Test
    public void takenZonesTest() throws Exception {
        Map<String, Integer> takenZones = readTakenZones();
        assertEquals(4642, takenZones.size());
        assertTrue(takenZones.containsKey("Bockholmen"));
        assertEquals(1, (long) takenZones.get("Lambastranden"));
    }
}
