package org.joelson.turf.turfgame.apiv5;

import org.joelson.turf.util.TimeUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

public class ZonesV5CSVExtractor {

    private final Path zonesJSONPath;
    private final Path zonesCSVPath;

    public ZonesV5CSVExtractor(Path zonesJSONPath) {
        if (!Files.exists(Objects.requireNonNull(zonesJSONPath)) || Files.isDirectory(zonesJSONPath)) {
            throw new IllegalArgumentException("zonesJSONPath " + zonesJSONPath + " is not an existing file.");
        }
        this.zonesJSONPath = zonesJSONPath;
        zonesCSVPath = Path.of(zonesJSONPath.getParent().toString(), zonesJSONPath.getFileName().toString() + ".csv");
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.printf("Usage:%n\t%s zones.json", ZonesV5CSVExtractor.class.getName());
            System.exit(-1);
        }
        ZonesV5CSVExtractor extractor = new ZonesV5CSVExtractor(Path.of(args[0]));
        extractor.extractZones();
    }

    private void extractZones() throws IOException {
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(zonesCSVPath))) {
            List<Zone> zones = Zones.fromJSON(Files.readString(zonesJSONPath));
            for (Zone zone : zones) {
                handleZone(writer, zone);
            }
        }
    }

    private void handleZone(PrintWriter writer, Zone zone) {
        Instant creationInstant = TimeUtil.turfAPITimestampToInstant(zone.getDateCreated());
        Region region = zone.getRegion();
        String regionString;
        if (region != null) {
            regionString = String.format("%s;%d;%s;%s", FeedsV5VisitsCSVExtractor.countryOf(region),
                    region.getId(), region.getName(), FeedsV5VisitsCSVExtractor.areaOf(region));
        } else {
            regionString = ";;;;";
        }
        writer.printf("%s;%s;%d;%s;%d;%d;%d;%f;%f%n",
                FeedsV5VisitsCSVExtractor.dateAndTimeOf(creationInstant), regionString, zone.getId(),
                zone.getName(), zone.getTakeoverPoints(), zone.getPointsPerHour(), zone.getTotalTakeovers(),
                zone.getLatitude(), zone.getLongitude());
    }
}
