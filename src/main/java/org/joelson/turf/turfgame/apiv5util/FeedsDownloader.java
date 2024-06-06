package org.joelson.turf.turfgame.apiv5util;

public class FeedsDownloader {

    private static final String FEEDS_REQUEST = "https://api.turfgame.com/unstable/feeds";

    public static void main(String[] args) {
        org.joelson.turf.turfgame.util.FeedsDownloader.handleFeeds(FEEDS_REQUEST);
    }
}
