package org.joelson.turf.turfgame.util;

import org.joelson.turf.turfgame.FeedObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Stream;

public class FeedsVerifier {

    private static Path lastPath;
    private static FeedObject lastFeedObject;

    public static void main(String[] args) throws IOException {
        if (args.length != 1 || !Files.isDirectory(Path.of(args[0]))) {
            System.out.printf("Usage:%n\t%s directory_with_zipped_feeds", FeedsVerifier.class);
            System.exit(1);
        }

        Path dirPath = Path.of(args[0]);
        try (Stream<Path> files = Files.list(dirPath)) {
            files.forEach(FeedsVerifier::verifyPath);
        }
    }

    private static void verifyPath(Path path) {
        if (path.toString().contains("v4")) {
            verifyV4Zip(path);
        } else if (path.toString().contains("v5")) {
            verifyV5Zip(path);
        } else {
            throw new RuntimeException(path.toString());
        }
    }

    private static void verifyV4Zip(Path path) {
        verifyZip("v4", path, Map.of("chat", org.joelson.turf.turfgame.apiv4.FeedChat.class,
                "medal", org.joelson.turf.turfgame.apiv4.FeedMedal.class,
                "takeover", org.joelson.turf.turfgame.apiv4.FeedTakeover.class,
                "zone", org.joelson.turf.turfgame.apiv4.FeedZone.class));
    }

    private static void verifyV5Zip(Path path) {
        verifyZip("v5", path, Map.of("chat", org.joelson.turf.turfgame.apiv5.FeedChat.class,
                "medal", org.joelson.turf.turfgame.apiv5.FeedMedal.class,
                "takeover", org.joelson.turf.turfgame.apiv5.FeedTakeover.class,
                "zone", org.joelson.turf.turfgame.apiv5.FeedZone.class));
    }

    private static void rememberPath(Path path) {
        lastPath = path;
        lastFeedObject = null;
    }

    private static void rememberFeedObject(FeedObject feedObject) {
        lastFeedObject = feedObject;
    }

    private static void verifyZip(String version, Path path, Map<String, Class<? extends FeedObject>> chat) {
        System.out.printf("--> %s %s%n", version, path);
        lastPath = null;
        lastFeedObject = null;
        FeedsReader feedsReader = new FeedsReader(chat);
        try {
            feedsReader.handleFeedObjectFile(path, FeedsVerifier::rememberPath, FeedsVerifier::rememberFeedObject);
        } catch (Exception e) {
            System.err.printf("Error handling %s:%n", path);
            System.err.printf("  lastPath: %s%n", lastPath);
            System.err.printf("  lastObj:  %s%n", lastFeedObject);
            e.printStackTrace();
        }
    }
}
