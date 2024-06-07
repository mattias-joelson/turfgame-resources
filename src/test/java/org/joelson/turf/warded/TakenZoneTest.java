package org.joelson.mattias.turfgame.warded;

import org.joelson.mattias.turfgame.util.URLReaderTest;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TakenZoneTest {
    
    @Test
    public void takenZonesTest() throws Exception {
        Map<String, Integer> takenZones = readTakenZones();
        assertTrue(takenZones.size() >= 4258);
        assertTrue(takenZones.containsKey("Bockholmen"));
        assertEquals(1, (long) takenZones.get("Lambastranden"));
    }
    
    public static Map<String, Integer> readTakenZones() throws Exception {
        return URLReaderTest.readProperties("warded.unique.php.html", TakenZones::fromHTML);
    }
}
