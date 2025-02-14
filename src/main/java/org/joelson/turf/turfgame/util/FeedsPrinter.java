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
                        Path.of(filename), path -> true, feedObject -> handleFeedObject(stringObjects, feedObject));
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Handling filename " + filename);
                System.exit(-1);
            }
        }

        errorHandler.messageErrorPaths(20);
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
