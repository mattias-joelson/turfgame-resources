package org.joelson.turf.turfgame.apiv4;

import org.joelson.turf.turfgame.util.FeedsPathComparator;
import org.joelson.turf.util.FilesUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

public class ZonesDate {

    public static void main(String... args) throws IOException {
        if (args.length == 0) {
            System.out.printf("Usage:%n\t%s zonefile.json [zonefile.json ...]%n", ZonesDate.class);
        }

        for (String filename : args) {
            FilesUtil.forEachFile(Path.of(filename), true, new FeedsPathComparator(), ZonesDate::findLastCreateDate);
        }
    }

    private static void findLastCreateDate(Path path) {
        List<Zone> zones = null;
        try {
            String json = Files.readString(path);
            zones = Zones.fromJSON(json);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Exception reading path " + path);
            System.exit(-1);
        }
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
            if (zone == null) {
                System.out.println("zone: " + null);
                continue;
            }
            if (zone.getDateCreated() == null) {
                System.out.println("zone.getDateCreated(): " + null);
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
