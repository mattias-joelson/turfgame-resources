package org.joelson.turf.turfgame.apiv5util;

import org.joelson.turf.turfgame.FeedObject;
import org.joelson.turf.turfgame.apiv5.FeedChat;
import org.joelson.turf.turfgame.apiv5.FeedMedal;
import org.joelson.turf.turfgame.apiv5.FeedTakeover;
import org.joelson.turf.turfgame.apiv5.FeedZone;

import java.io.IOException;

public class FeedsReader extends org.joelson.turf.turfgame.util.FeedsReader {

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.printf("Usage:\n\t%s feed_file1.json ...%n", FeedsReader.class.getName());
            return;
        }
        new FeedsReader().readFiles(args);
    }

    @Override
    protected Class<? extends FeedObject> getJSONClass(String type) {
        return switch (type) {
            case "chat" -> FeedChat.class;
            case "medal" -> FeedMedal.class;
            case "takeover" -> FeedTakeover.class;
            case "zone" -> FeedZone.class;
            default -> throw new IllegalArgumentException("Unknown type " + type);
        };
    }
}
