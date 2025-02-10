package org.joelson.turf.turfgame.apiv4util;

import org.joelson.turf.turfgame.apiv4.Zone;
import org.joelson.turf.turfgame.apiv4.Zones;
import org.joelson.turf.turfgame.util.FeedsPathComparator;
import org.joelson.turf.util.FilesUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class ZonesFind {

    private ZonesFind() throws InstantiationException {
        throw new InstantiationException("Should not be instantiated");
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.printf("Usage:\n\t%s zone_name file [files...]%n", ZonesFind.class);
            return;
        }
        String zoneName = args[0];
        for (int i = 1; i < args.length; i += 1) {
            FilesUtil.forEachFile(Path.of(args[i]), true, new FeedsPathComparator(), path -> readFile(path, zoneName));
        }
    }

    private static void readFile(Path path, String zoneName) {
        List<Zone> zones = null;
        try {
            String json = Files.readString(path);
            zones = Zones.fromJSON(json);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Exception reading path " + path);
            System.exit(-1);
        }
        Zone zone = zones.stream().filter(z -> z.getName().equals(zoneName)).findFirst().orElse(null);
        System.out.printf("%s: %s%n", path, toString(zone));
    }

    private static String toString(Zone zone) {
        if (zone == null) {
            return String.valueOf((Object) null);
        }
        return String.format("{ %d, %s, %d - %s, %s, %f, %f, %d, %d }", zone.getId(), zone.getName(),
                zone.getRegion().getId(), zone.getRegion().getName(), zone.getDateCreated(), zone.getLatitude(),
                zone.getLongitude(), zone.getTakeoverPoints(), zone.getPointsPerHour());
    }
}
