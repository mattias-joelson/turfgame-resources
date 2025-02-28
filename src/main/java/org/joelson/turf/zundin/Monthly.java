package org.joelson.turf.zundin;

import org.joelson.turf.statistics.Municipality;
import org.joelson.turf.statistics.Round;
import org.joelson.turf.statistics.Statistics;
import org.joelson.turf.statistics.User;
import org.joelson.turf.statistics.Visits;
import org.joelson.turf.statistics.Zone;
import org.joelson.turf.util.URLReader;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
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

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            printUsageAndExit();
        }
        String userName = null;
        int round = -1;
        for (String arg : args) {
            if (arg.startsWith("user=")) {
                userName = arg.substring(5);
            } else if (arg.startsWith("round=")) {
                round = Integer.parseInt(arg.substring(6));
            }
        }
        if (userName == null || round <= 0) {
            System.out.printf("Provided arguments: user=%s, round=%d", userName, round);
            printUsageAndExit();
        }
        String html = getMonthlyHTML(userName, round);
        String filename = String.format("monthly_%s_round%d.html", userName, round);
        try (PrintWriter writer = new PrintWriter(filename, StandardCharsets.UTF_8)) {
            writer.println(html);
        }
    }

    private static void printUsageAndExit() {
        System.out.printf("Usage:\n\t%s user=nick round=176%n", Monthly.class.getName());
        System.exit(-1);
    }

    private static String getMonthlyHTML(String userName, int round) throws IOException {
        String request = "https://frut.zundin.se/monthly.php?userid=" + userName;
        if (round > 0) {
            request += "&roundid=" + round;
        }
        URLReader.Response response = URLReader.getRequest(request);
        if (response.statusCode() != HttpURLConnection.HTTP_OK) {
            System.err.printf("Response statusCode: %d, request: %s%n", response.statusCode(), request);
        }
        return response.content();
    }

    public static Monthly fromZundin(String userName, int round) throws IOException {
        String content = getMonthlyHTML(userName, round);
        return fromHTML(userName, round, content);
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

    public String getUserName() {
        return userName;
    }

    public int getRound() {
        return round;
    }

    public List<MonthlyZone> getZones() {
        return Collections.unmodifiableList(zones);
    }
}
