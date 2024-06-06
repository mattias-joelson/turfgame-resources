package org.joelson.turf.turfgame.apiv4util;

public class FeedsDownloader {

    private static final String FEEDS_REQUEST = "https://api.turfgame.com/v4/feeds";

    public static void main(String[] args) {
        org.joelson.turf.turfgame.util.FeedsDownloader.handleFeeds(FEEDS_REQUEST);
    }
}
