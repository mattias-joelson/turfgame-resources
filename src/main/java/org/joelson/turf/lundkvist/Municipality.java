package org.joelson.turf.lundkvist;

import org.joelson.turf.util.URLReader;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Municipality {

    private static final String SELECT_TAG = "<select name=";
    private static final String SELECTED_ATTRIBUTE = "selected";
    private static final String ZONE_LINK = "<td><a href='https://turfgame.com/map/";
    private static final String ROW_END = "</td></tr>";

    private Municipality() throws InstantiationException {
        throw new InstantiationException("Should not be instantiated");
    }

    public static Map<String, Boolean> fromLundkvist(String userName, String country, int region, String municipality)
            throws IOException {
        return fromHTML(getMunicipalityHTML(userName, country, region, municipality));
    }

    private static String getMunicipalityHTML(String userName, String country, int region, String municipality)
            throws IOException {
        String request = String.format("https://turf.lundkvist.com/?user=%s&country=%s&region=%d&city=%s",
                userName, country, region, municipality);
        URLReader.Response response = URLReader.getRequest(request);
        if (response.status() != HttpURLConnection.HTTP_OK) {
            System.err.printf("Response status: %d, request: %s", response.status(), request);
        }
        return response.body();
    }

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.printf("Usage:\n\t%s user=username country=country region=regionnumber city [city ...]%n",
                    Municipality.class.getName());
            return;
        }
        String username = "";
        String country = "";
        int region = -1;
        List<String> cities = new ArrayList<>();
        for (String arg : args) {
            if (arg.startsWith("user=")) {
                username = arg.substring(5);
            } else if (arg.startsWith("country=")) {
                country = arg.substring(8);
            } else if (arg.startsWith("region=")) {
                region = Integer.parseInt(arg.substring(7));
            } else {
                cities.add(arg);
            }
        }
        for (String city : cities) {
            String html = getMunicipalityHTML(username, country, region, city);
            PrintWriter writer = new PrintWriter("lundkvist_" + region + "_" + city.toLowerCase() + ".html",
                    StandardCharsets.UTF_8);
            writer.println(html);
            writer.close();
        }
    }

    public static String nameFromHTML(String html) {
        int pos = -1;
        for (int i = 0; i < 3; i += 1) {
            pos = assertPosition(html.indexOf(SELECT_TAG, pos + 1));
        }
        pos = assertPosition(html.indexOf(SELECTED_ATTRIBUTE, pos));
        pos = assertPosition(html.indexOf('>', pos));
        int endPos = assertPosition(html.indexOf(" (", pos));
        return html.substring(pos + 1, endPos);
    }

    private static int assertPosition(int pos) {
        if (pos == -1) {
            throw new RuntimeException("Could not find municipality name");
        }
        return pos;
    }

    public static Map<String, Boolean> fromHTML(String html) {
        int pos = html.indexOf(ZONE_LINK);
        if (pos == -1) {
            return Collections.emptyMap();
        }
        Map<String, Boolean> municipalityZones = new HashMap<>();
        while (pos > -1 && pos < html.length()) {
            String zoneName = zoneNameFromHTML(pos, html);
            int end = lineEndInHTML(pos, html);
            boolean taken = zoneTakenFromHTML(end, html);
            municipalityZones.put(zoneName, taken);
            pos = html.indexOf(ZONE_LINK, end + ROW_END.length());
        }
        return municipalityZones;
    }

    private static String zoneNameFromHTML(int pos, String html) {
        int end = html.indexOf("'", pos + ZONE_LINK.length());
        String zoneName = html.substring(pos + ZONE_LINK.length(), end);
        return zoneName;
    }

    private static int lineEndInHTML(int pos, String html) {
        int end = html.indexOf(ROW_END, pos);
        return end;
    }

    private static boolean zoneTakenFromHTML(int end, String html) {
        int start = html.lastIndexOf('>', end);
        String taken = html.substring(start + 1, end);
        return taken.equalsIgnoreCase("ja");
    }
}
