package org.joelson.turf.turfgame.apiv4util;

import org.joelson.turf.turfgame.apiv4.Zone;
import org.joelson.turf.turfgame.apiv4.Zones;
import org.joelson.turf.util.FilesUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

public class ZonesDate {

    public static void main(String... args) throws IOException {
        if (args.length <= 0) {
            System.out.printf("Usage:%n\t%s zonefile.json [fonefile.json ...]%n", ZonesDate.class);
        }

        for (String filename : args) {
            FilesUtil.forEachFile(Path.of(filename), true, ZonesDate::findLastCreateDate);
        }
    }

    private static void findLastCreateDate(Path path) {
        String json = null;
        try {
            json = Files.readString(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        List<Zone> zones = Zones.fromJSON(json);
        //findSingleLastDateCreated(path, zones);
        findLast10DateCreated(path, zones);
    }

    private static void findLast10DateCreated(Path path, List<Zone> zones) {
        System.out.println(path.toString() + ':');
        zones.stream().map(Zone::getDateCreated).sorted(Comparator.reverseOrder()).limit(25)
                .forEach(System.out::println);
    }

    private static void findSingleLastDateCreated(Path path, List<Zone> zones) {
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
        System.out.printf("%s: %s%n", path, createDate);
    }
}
