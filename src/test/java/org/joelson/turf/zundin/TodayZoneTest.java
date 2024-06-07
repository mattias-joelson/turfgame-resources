package org.joelson.mattias.turfgame.zundin;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TodayZoneTest {

    private static final String HTML_ROW = "          <tr>\n"
            + "            <td><script language='javascript' type='text/javascript'>document.write(getLocalDate('2019-11-11 19:52:05'));</script></td>\n"
            + "            <td>15:28</td>\n"
            + "\t\t    <td><a href='https://www.turfgame.com/zone/CircleGate'><img height='12' src='images/turf.ico'/></a> <a href='zone.php?zoneid=194306&roundid=113'>CircleGate</a></td>\n"
            + "\t \t    <td align='left'>Solna kommun</td>\n"
            + "\t \t    <td align='right'>95</td>\n"
            + "\t\t    <td style='padding-left:10px' align='left'></td>\n"
            + "\t\t    <td bgcolor='orange'>Assist</td>\n"
            + "\t\t    <td><a href='https://www.turfgame.com/user/RiddervanMyyl'><img height='12' src='images/turf.ico'/></a> <a href='today.php?userid=RiddervanMyyl&date=2019-11-11'>RiddervanMyyl</a></td>\n"
            + "\t\t   <td>46</td>\n"
            + "         </tr>\n";

    @Test
    public void parseRowTest() {
        TodayZone zone = TodayZone.fromHTML(HTML_ROW);
        assertNotNull(zone);
        assertEquals("2019-11-11 19:52:05", zone.getDate());
        assertEquals("CircleGate", zone.getZoneName());
        assertEquals("Solna kommun", zone.getAreaName());
        assertEquals(95, zone.getTP());
        assertEquals(-1, zone.getPPH());
        assertEquals("Assist", zone.getActivity());
        assertEquals("RiddervanMyyl", zone.getUserId());
        assertEquals(46, zone.getTake());
    }
}
