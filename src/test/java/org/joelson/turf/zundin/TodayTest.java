package org.joelson.mattias.turfgame.zundin;

import org.joelson.mattias.turfgame.util.URLReaderTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TodayTest {

    @Test
    public void testZonerXToday() throws Exception {
        Today today = getZonerXToday();
        assertNotNull(today);
        assertEquals("ZonerX", today.getUserName());
        assertEquals("2018-06-30", today.getDate());
        assertEquals(879, today.getZones().size());
        assertEquals(513, getActivityCount(today, "Takeover"));
        assertEquals(0, getActivityCount(today, "Assist"));
        assertEquals(1, getActivityCount(today, "Revisit"));
        assertEquals(365, getActivityCount(today, "Lost"));
    }

    private static Today getZonerXToday() throws Exception {
        return readProperties("todays_activity_zonerx_2018-06-30.html", "ZonerX", "2018-06-30");
    }

    @Test
    public void testOberoffToday() throws Exception {
        Today today = getOberoffToday();
        assertNotNull(today);
        assertEquals("Oberoff", today.getUserName());
        assertEquals("2019-11-11", today.getDate());
        assertEquals(254, today.getZones().size());
        assertEquals(87, getActivityCount(today, "Takeover"));
        assertEquals(1, getActivityCount(today, "Assist"));
        assertEquals(166, getActivityCount(today, "Lost"));
    }

    private static int getActivityCount(Today today, String activity) {
        int count = 0;
        for (TodayZone zone : today.getZones()) {
            if (zone.getActivity().equals(activity)) {
                count += 1;
            }
        }
        return count;
    }
    private static Today getOberoffToday() throws Exception {
        return readProperties("todays_activity_oberoff_2019-11-11.html", "Oberoff", "2019-11-11");
    }

    private static Today readProperties(String resource, String name, String date) throws Exception {
        return URLReaderTest.readProperties(resource, s -> Today.fromHTML(name, date, s));
    }
}
