package org.joelson.turf.turfgame.apiv4util;

import org.joelson.turf.turfgame.apiv4.Zone;
import org.joelson.turf.turfgame.apiv4.Zones;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;

public class ZonesDate {

    public static void main(String... args) throws IOException {
        if (args.length <= 0) {
            System.out.printf("Usage:%n\t%s zonefile.json [fonefile.json ...]%n", ZonesDate.class);
        }

        for (String filename : args) {
            findLastCreateDate(filename);
        }
    }

    private static void findLastCreateDate(String filename) throws IOException {
        Path path = Paths.get(filename);
        String json = Files.readString(path);
        List<Zone> zones = Zones.fromJSON(json);
        //findSingleLastDateCreated(filename, zones);
        findLast10DateCreated(filename, zones);
    }

    private static void findLast10DateCreated(String filename, List<Zone> zones) {
        System.out.println(filename + ':');
        zones.stream().map(Zone::getDateCreated).sorted(Comparator.reverseOrder()).limit(25)
                .forEach(System.out::println);
    }

    private static void findSingleLastDateCreated(String filename, List<Zone> zones) {
        String createDate = "";
        for (Zone zone : zones) {
            if (createDate == null) {
                System.out.println("createDate: " + createDate);
            }
            if (zone == null) {
                System.out.println("zone: " + zone);
            }
            if (zone.getDateCreated() == null) {
                System.out.println("zone.getDateCreated(): " + zone.getDateCreated());
                System.out.println("zone: " + zone);
                System.out.println("zone.getName(): " + zone.getName());
                continue;
            }
            if (createDate.compareTo(zone.getDateCreated()) < 0) {
                createDate = zone.getDateCreated();
            }
        }
        System.out.printf("%s: %s%n", filename, createDate);
    }
}
