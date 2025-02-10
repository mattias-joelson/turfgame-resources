package org.joelson.turf.turfgame.util;

import org.joelson.turf.turfgame.FeedObject;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class FeedsPrinter {

    private FeedsPrinter() throws InstantiationException {
        throw new InstantiationException("Should not be instantiated.");
    }

    public static void printUniqueNodes(Map<String, Class<? extends FeedObject>> typesToHandle, String[] filenames) {
        SortedSet<String> stringObjects = new TreeSet<>();
        DefaultFeedContentErrorHandler errorHandler = new DefaultFeedContentErrorHandler();
        FeedsReader feedsReader = new FeedsReader(typesToHandle, errorHandler);
        for (String filename : filenames) {
            try {
                feedsReader.handleFeedObjectPath(
                        Path.of(filename), path -> {}, feedObject -> handleFeedObject(stringObjects, feedObject));
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Handling filename " + filename);
                System.exit(-1);
            }
        }

        List<Path> errorPaths = errorHandler.getErrorPaths();
        if (!errorPaths.isEmpty()) {
            System.err.println("Error in paths: " + errorPaths.size());
            int max = Math.min(20, errorPaths.size());
            for (int i = 0; i < max; i += 1) {
                System.out.println("    " + errorPaths.get(i));
            }
            if (errorPaths.size() > 20) {
                System.err.println("    ...");
            }
        }
    }

    private static void handleFeedObject(SortedSet<String> uniqueObjects, FeedObject feedObject) {
        String stringObject = feedObject.toString();
        if (uniqueObjects.contains(stringObject)) {
            System.out.println("    Already contains object " + stringObject);
        } else {
            uniqueObjects.add(stringObject);
            System.out.println(" -> " + stringObject);
        }
    }
}
