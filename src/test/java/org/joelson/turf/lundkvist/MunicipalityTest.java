package org.joelson.turf.lundkvist;

import org.joelson.turf.util.URLReaderTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MunicipalityTest {

    public static Map<String, Boolean> getDanderydZones() throws IOException {
        return URLReaderTest.readProperties("lundkvist_141_danderyd.html", Municipality::fromHTML);
    }

    public static Map<String, Boolean> getHuddingeZones() throws Exception {
        return URLReaderTest.readProperties("lundkvist_141_huddinge.html", Municipality::fromHTML);
    }

    public static Map<String, Boolean> getJarfallaZones() throws Exception {
        return URLReaderTest.readProperties("lundkvist_141_jarfalla.html", Municipality::fromHTML);
    }

    public static Map<String, Boolean> getSolnaZones() throws IOException {
        return URLReaderTest.readProperties("lundkvist_141_solna.html", Municipality::fromHTML);
    }

    public static Map<String, Boolean> getSundbybergZones() throws IOException {
        return URLReaderTest.readProperties("lundkvist_141_sundbyberg.html", Municipality::fromHTML);
    }

    public static Map<String, Boolean> getVallentunaZones() throws Exception {
        return URLReaderTest.readProperties("lundkvist_141_vallentuna.html", Municipality::fromHTML);
    }

    private static int countTakenZones(Map<String, Boolean> zones) {
        int taken = 0;
        for (Boolean takenZone : zones.values()) {
            if (takenZone) {
                taken += 1;
            }
        }
        return taken;
    }

    @Test
    public void readDanderyd() throws Exception {
        Map<String, Boolean> zones = getDanderydZones();
        assertEquals(170, zones.size());
        int taken = countTakenZones(zones);
        assertEquals(170, taken);
    }

    @Test
    public void readSolna() throws Exception {
        Map<String, Boolean> zones = getSolnaZones();
        assertEquals(228, zones.size());
        int taken = countTakenZones(zones);
        assertEquals(228, taken);
    }

    @Test
    public void readSundbyberg() throws Exception {
        Map<String, Boolean> zones = getSundbybergZones();
        assertEquals(117, zones.size());
        int taken = countTakenZones(zones);
        assertEquals(117, taken);
    }
}
