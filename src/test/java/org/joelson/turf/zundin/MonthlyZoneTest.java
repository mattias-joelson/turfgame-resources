package org.joelson.turf.zundin;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MonthlyZoneTest {

    private static final String HTML_ROW = "<tr>\n"
            + "                    <td align='right'>224</td>\n"
            + "                    <td><a href='https://www.turfgame.com/zone/Southball'><img height='12' src='images/turf.ico'/></a> <a href='zone.php?zoneid=257&roundid=113'>Southball</a></td>\n"
            + "                    <td>Stockholms kommun</td>\n"
            + "                    <td>Stockholm</td>\n"
            + "                    <td align='right'>80</td>\n"
            + "                    <td align='right'> +8</td>\n"
            + "                    <td align='right'>240</td>\n"
            + "                    <td align='right'>05:00:56</td>\n"
            + "                    <td align='right'>2</td>\n"
            + "                    <td align='right'>2</td>\n"
            + "                    <td align='right'>0</td>\n"
            + "                    <td align='right'>0</td>\n"
            + "                </tr>";

    @Test
    public void parseRowTest() {
        MonthlyZone zone = MonthlyZone.fromHTML(HTML_ROW);
        assertNotNull(zone);
        assertEquals("Southball", zone.getName());
        assertEquals(80, zone.getTP());
        assertEquals(8, zone.getPPH());
        assertEquals("Stockholms kommun", zone.getMunicipality());
        assertEquals(2, zone.getTakes());
        assertEquals(0, zone.getRevisits());
        assertEquals(0, zone.getAssists());
    }
}
