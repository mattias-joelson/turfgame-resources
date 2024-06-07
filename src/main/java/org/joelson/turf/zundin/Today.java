package org.joelson.mattias.turfgame.zundin;

import org.joelson.mattias.turfgame.util.URLReader;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Today {

    private static final String TURF_LINK_TAG = "<a href='https://www.turfgame.com/zone/";

    private final String userName;
    private final String date;
    private final List<TodayZone> zones;

    private Today(String userName, String date, List<TodayZone> zones) {
        this.userName = userName;
        this.date = date;
        this.zones = new ArrayList<>();
        this.zones.addAll(zones);
    }

    public String getUserName() {
        return userName;
    }

    public String getDate() {
        return date;
    }

    public List<TodayZone> getZones() {
        return Collections.unmodifiableList(zones);
    }

    public static Today fromZundin(String userName, String date) throws IOException {
        String request = "https://frut.zundin.se/today.php?userid=" + userName + "&date=" + date;
        return fromHTML(userName, date, URLReader.getRequest(request));
    }

    public static Today fromHTML(String userName, String date, String html) {
        List<TodayZone> zones = new ArrayList<>();
        int pos = html.indexOf(TURF_LINK_TAG);
        if (pos == -1) {
            return new Today(userName, date, zones);
        }
        pos = html.substring(0, pos).lastIndexOf("<tr>");
        if (pos == -1) {
            return new Today(userName, date, zones);
        }
        while (pos < html.length()) {
            int end = html.indexOf("</tr>", pos);
            if (end == -1) {
                end = html.length();
            }
            String row = html.substring(pos, end);
            if (row.indexOf(TURF_LINK_TAG) > 0) {
                TodayZone zone = TodayZone.fromHTML(row);
                zones.add(zone);
            }
            pos = html.indexOf("<tr>", end);
            if (pos == -1) {
                pos = html.length();
            }
        }
        return new Today(userName, date, zones);
    }
}
