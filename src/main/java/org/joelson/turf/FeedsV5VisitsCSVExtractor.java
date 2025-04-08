package org.joelson.turf;

import org.joelson.turf.turfgame.FeedObject;
import org.joelson.turf.turfgame.apiv5.Area;
import org.joelson.turf.turfgame.apiv5.FeedTakeover;
import org.joelson.turf.turfgame.apiv5.Region;
import org.joelson.turf.turfgame.apiv5.User;
import org.joelson.turf.turfgame.apiv5.Zone;
import org.joelson.turf.turfgame.util.DefaultFeedContentErrorHandler;
import org.joelson.turf.turfgame.util.FeedsReader;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FeedsV5VisitsCSVExtractor {

    private final Path saveDirectory;
    private final Path[] feedFiles;

    public FeedsV5VisitsCSVExtractor(Path saveDirectory, Path[] feedFiles) {
        if (!Files.exists(Objects.requireNonNull(saveDirectory)) || !Files.isDirectory(saveDirectory)) {
            throw new IllegalArgumentException("saveDirectory does not exist or is not directory - " + saveDirectory);
        }
        for (Path feedFile : Objects.requireNonNull(feedFiles)) {
            if (!Files.exists(feedFile)) {
                throw new IllegalArgumentException("Feed file does not exist - " + feedFile);
            }
        }
        this.saveDirectory = saveDirectory;
        this.feedFiles = feedFiles;
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.printf("Usage:%n\t%s saveDirectory feeds_files ... ", FeedsV5VisitsCSVExtractor.class.getName());
            System.exit(-1);
        }
        FeedsV5VisitsCSVExtractor extractor = new FeedsV5VisitsCSVExtractor(Path.of(args[0]), toFeedFiles(args));
        extractor.extractVisits();
    }

    private static Path[] toFeedFiles(String[] args) {
        List<Path> feedFiles = new ArrayList<>();
        for (int i = 1; i < args.length; i += 1) {
            feedFiles.add(Path.of(args[i]));
        }
        return feedFiles.toArray(new Path[0]);
    }

    private void extractVisits() throws IOException {
        for (Path feedFile : feedFiles) {
            String fileName = feedFile.getFileName().toString();
            if (fileName.endsWith(".zip")) {
                fileName = fileName.substring(0, fileName.length() - 4);
            }
            fileName += ".csv";
            Path csvPath = Path.of(saveDirectory.toString(), fileName);
            System.out.printf("Reading %s, writing %s%n", feedFile, csvPath);
            try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(csvPath))) {
                FeedsReader feedsReader = new FeedsReader(Map.of("takeover", FeedTakeover.class),
                        new DefaultFeedContentErrorHandler());
                feedsReader.handleFeedObjectPath(feedFile, FeedsV5VisitsCSVExtractor::includePath,
                        feedObject -> handleVisit(writer, feedObject));
            }
        }
    }

    private void handleVisit(PrintWriter writer, FeedObject feedObject) {
        if (feedObject instanceof FeedTakeover takeover) {
            Zone zone = Objects.requireNonNull(takeover.getZone());
            Region region = Objects.requireNonNull(zone.getRegion());
            String baseCSV = String.format("%s;%s;%d;%s;%s;%d;%s;%d;%d",
                    takeover.getTime(), countryOf(region), region.getId(), region.getName(), areaOf(region),
                    zone.getId(), zone.getName(), zone.getTakeoverPoints(), zone.getPointsPerHour());
            User currentOwner = Objects.requireNonNull(takeover.getCurrentOwner());
            User previousOwner = zone.getPreviousOwner();
            if (previousOwner == null) {
                writer.printf("%s;%d;%s;takeover;neutral%n", baseCSV, currentOwner.getId(), currentOwner.getName());
            } else if (previousOwner.getId() != currentOwner.getId()) {
                writer.printf("%s;%d;%s;takeover%n", baseCSV, currentOwner.getId(), currentOwner.getName());
            } else {
                writer.printf("%s;%d;%s;revisit%n", baseCSV, currentOwner.getId(), currentOwner.getName());
            }
            for (User assister : Objects.requireNonNullElse(takeover.getAssists(), new User[0])) {
                if (previousOwner == null) {
                    writer.printf("%s;%d;%s;assist;neutral%n", baseCSV, assister.getId(), assister.getName());
                } else {
                    writer.printf("%s;%d;%s;assist%n", baseCSV, assister.getId(), assister.getName());
                }
            }
        }
    }

    private static String countryOf(Region region) {
        return Objects.requireNonNullElse(region.getCountry(), "");
    }

    private static String areaOf(Region region) {
        Area area = region.getArea();
        if (area != null) {
            return String.format("%d;%s", area.getId(), area.getName());
        } else {
            return ";";
        }
    }

    private static boolean includePath(Path path) {
        return path.toString().contains("v5");
    }
}
