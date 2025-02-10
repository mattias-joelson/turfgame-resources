package org.joelson.turf.turfgame.apiv5;

import org.joelson.turf.turfgame.FeedObject;

import java.util.Map;

public final class FeedsPrinter {

    private FeedsPrinter() throws InstantiationException {
        throw new InstantiationException("Should not be instantiated.");
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.printf("Usage:\n\t%s feed_file1.json ...%n", FeedsPrinter.class.getName());
            return;
        }
        org.joelson.turf.turfgame.util.FeedsPrinter.printUniqueNodes(typesToHandle(), args);
    }

    private static Map<String, Class<? extends FeedObject>> typesToHandle() {
        return Map.of(
                "chat", FeedChat.class,
                "medal", FeedMedal.class,
                "takeover", FeedTakeover.class,
                "zone", FeedZone.class
        );
    }
}
