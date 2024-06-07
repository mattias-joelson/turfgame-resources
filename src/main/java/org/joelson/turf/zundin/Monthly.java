package org.joelson.mattias.turfgame.zundin;

import org.joelson.mattias.turfgame.statistics.Municipality;
import org.joelson.mattias.turfgame.statistics.Round;
import org.joelson.mattias.turfgame.statistics.Statistics;
import org.joelson.mattias.turfgame.statistics.User;
import org.joelson.mattias.turfgame.statistics.Visits;
import org.joelson.mattias.turfgame.statistics.Zone;
import org.joelson.mattias.turfgame.util.URLReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Monthly {


    private static final String TURF_LINK_TAG = "<a href='https://www.turfgame.com/zone/";

    private final String userName;
    private final int round;
    private final List<MonthlyZone> zones;

    private Monthly(String userName, int round, List<MonthlyZone> zones) {
        this.userName = userName;
        this.round = round;
        this.zones = new ArrayList<>();
        this.zones.addAll(zones);
    }

    public String getUserName() {
        return userName;
    }

    public int getRound() {
        return round;
    }

    public List<MonthlyZone> getZones() {
        return Collections.unmodifiableList(zones);
    }

    public static void addToStatistics(Monthly monthly, Statistics statistics) {
        User user = statistics.getUser(monthly.getUserName());
        Round round = statistics.getRound(monthly.getRound());
        for (MonthlyZone monthlyZone : monthly.getZones()) {
            Zone zone = statistics.getZone(monthlyZone.getName());
            if (zone == null) {
                Municipality municipality = statistics.getMunicipality(monthlyZone.getMunicipality());
                zone = new Zone(-1, monthlyZone.getName(), municipality, 0.0f, 0.0f);
                statistics.addZone(zone);
            }
            Visits visits = new Visits(zone, user, round, monthlyZone.getTP(), monthlyZone.getPPH(),
                    monthlyZone.getTakes(), monthlyZone.getAssists(), monthlyZone.getRevisits());
            if (!statistics.addVisits(visits)) {
                System.err.println("Visits already exists! " + visits);
            }
        }
    }

    public static Monthly fromZundin(String userName, int round) throws IOException {
        String request = "http://frut.zundin.se/monthly.php?userid=" + userName;
        if (round > 0) {
            request += "&roundid=" + round;
        }
        return fromHTML(userName, round, URLReader.getRequest(request));
    }

    public static Monthly fromHTML(String userName, int round, String html) {
        List<MonthlyZone> zones = new ArrayList<>();
        int pos = html.indexOf(TURF_LINK_TAG);
        if (pos == -1) {
            return new Monthly(userName, round, zones);
        }
        while (pos < html.length()) {
            int end = html.indexOf(TURF_LINK_TAG, pos + TURF_LINK_TAG.length());
            if (end == -1) {
                end = html.length();
            }
            MonthlyZone zone = MonthlyZone.fromHTML(html.substring(pos, end));
            zones.add(zone);
            pos = end;
        }
        return new Monthly(userName, round, zones);
    }

}
