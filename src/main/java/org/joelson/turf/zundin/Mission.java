package org.joelson.mattias.turfgame.zundin;

import org.joelson.mattias.turfgame.util.URLReader;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Mission {
    
    private static final String ZONE_TABLE = "<th align='right'>Pos</th><th align='left'>Zone</th><th></th>";
    private static final String ZONE_ENTRY = "</td><td><a href='zone.php?zoneid=";
    
    public static List<Integer> fromZundin(String user, int mission) throws IOException {
        return fromHTML(getMissionHTML(user, mission));
    }

    private static String getMissionHTML(String user, int mission) throws IOException {
        String request = String.format("https://frut.zundin.se/mission.php?missionid=%d", mission);
        if (user != null && user.length() > 0) {
            request += "&userid=" + user;
        }
        return URLReader.getRequest(request);
    }

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println(String.format("Usage:\n\t%s [user=username] mission [mission ...]",
                    Mission.class.getName()));
            return;
        }
        String username = null;
        List<Integer> missions = new ArrayList<>();
        for (String arg : args) {
            if (arg.startsWith("user=")) {
                username = arg.substring(5);
            } else {
                missions.add(Integer.parseInt(arg));
            }
        }
        for (Integer mission : missions) {
            String html = getMissionHTML(username, mission);
            PrintWriter writer = new PrintWriter(getFileName(username, mission), "UTF8");
            writer.println(html);
            writer.close();
        }
    }
    
    private static String getFileName(String username, Integer mission) {
        if (username == null) {
            return "mission_" + mission + ".html";
        }
        return "mission_" + mission + "_" + username.toLowerCase() + ".html";
    }

    public static List<Integer> fromHTML(String html) {
        int pos = html.indexOf(ZONE_TABLE);
        pos = html.indexOf(ZONE_ENTRY, pos);
        List<Integer> zones = new ArrayList<>();
        while (pos != -1 && pos < html.length()) {
            int end = html.indexOf('\'', pos + ZONE_ENTRY.length());
            String zone = html.substring(pos + ZONE_ENTRY.length(), end);
            zones.add(Integer.valueOf(zone));
            pos = html.indexOf(ZONE_ENTRY, end);
        }
        return zones;
    }
}
