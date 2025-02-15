package org.joelson.turf.zundin;

import org.joelson.turf.util.URLReader;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Twoday {

    private static final String TURF_LINK_TAG = "<a href='https://www.turfgame.com/zone/";

    private final String userName;
    private final String date;
    private final List<TwodayZone> zones;

    private Twoday(String userName, String date, List<TwodayZone> zones) {
        this.userName = userName;
        this.date = date;
        this.zones = new ArrayList<>();
        this.zones.addAll(zones);
    }

    public static Twoday fromZundin(String userName, String date) throws IOException {
        String request = "https://frut.zundin.se/2day.php?userid=" + userName + "&date=" + date;
        URLReader.Response response = URLReader.getRequest(request);
        if (response.statusCode() != HttpURLConnection.HTTP_OK) {
            System.err.printf("Response statusCode: %d, request: %s", response.statusCode(), request);
        }
        return fromHTML(userName, date, response.content());
    }

    public static Twoday fromHTML(String userName, String date, String html) {
        List<TwodayZone> zones = new ArrayList<>();
        int pos = html.indexOf(TURF_LINK_TAG);
        if (pos == -1) {
            return new Twoday(userName, date, zones);
        }
        pos = html.substring(0, pos).lastIndexOf("<tr>");
        if (pos == -1) {
            return new Twoday(userName, date, zones);
        }
        while (pos < html.length()) {
            int end = html.indexOf("</tr>", pos);
            if (end == -1) {
                end = html.length();
            }
            String row = html.substring(pos, end);
            if (row.indexOf(TURF_LINK_TAG) > 0) {
                TwodayZone zone = TwodayZone.fromHTML(row);
                zones.add(zone);
            }
            pos = html.indexOf("<tr>", end);
            if (pos == -1) {
                pos = html.length();
            }
        }
        return new Twoday(userName, date, zones);
    }

    public String getUserName() {
        return userName;
    }

    public String getDate() {
        return date;
    }

    public List<TwodayZone> getZones() {
        return Collections.unmodifiableList(zones);
    }
}
