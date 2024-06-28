package org.joelson.turf.turfgame.apiv5util;

import org.joelson.turf.turfgame.FeedObject;
import org.joelson.turf.turfgame.apiv5.FeedChat;
import org.joelson.turf.turfgame.apiv5.FeedMedal;
import org.joelson.turf.turfgame.apiv5.FeedTakeover;
import org.joelson.turf.turfgame.apiv5.FeedZone;

import java.util.Map;

public final class FeedsReader {

    private FeedsReader() throws InstantiationException {
        throw new InstantiationException("Should not be instantiated.");
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.printf("Usage:\n\t%s feed_file1.json ...%n", FeedsReader.class.getName());
            return;
        }
        org.joelson.turf.turfgame.util.FeedsReader.printUniqueNodes(typesToHandle(), args);
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
