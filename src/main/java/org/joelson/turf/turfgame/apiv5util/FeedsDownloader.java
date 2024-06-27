package org.joelson.turf.turfgame.apiv5util;

import java.io.IOException;

public class FeedsDownloader {

    private static final String FEEDS_REQUEST = "https://api.turfgame.com/unstable/feeds";

    public static void main(String[] args) throws IOException {
        org.joelson.turf.turfgame.util.FeedsDownloader.handleFeeds(FEEDS_REQUEST, "v5");
    }
}
