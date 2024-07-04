package org.joelson.turf.turfgame.util;

import com.fasterxml.jackson.databind.JsonNode;
import org.joelson.turf.util.JacksonUtil;
import org.joelson.turf.util.TimeUtil;
import org.joelson.turf.util.URLReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class FeedsDownloader {

    private static final String FEEDS_V4_REQUEST = "https://api.turfgame.com/v4/feeds";
    private static final String FEEDS_V5_REQUEST = "https://api.turfgame.com/unstable/feeds";

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    private static final Logger logger = LoggerFactory.getLogger(FeedsDownloader.class);

    private static final String FEEDS_V4_PATH_NAME = "feeds_v4";
    private static final String FEEDS_V5_PATH_NAME = "feeds_v5";
    private static final int ERROR_EXIT_STATUS = 1;

    private final Path feedsV4Path;
    private final Path feedsV5Path;

    public FeedsDownloader(Path feedsPath) throws IOException {
        Objects.requireNonNull(feedsPath, "feedsPath is null");
        if (!Files.exists(feedsPath)) {
            exitWithError("Feeds dir does not exist: " + feedsPath);
        }
        verifyDirectoryExists(feedsPath);
        feedsV4Path = createOrVerifyIsDirectory(feedsPath, FEEDS_V4_PATH_NAME);
        feedsV5Path = createOrVerifyIsDirectory(feedsPath, FEEDS_V5_PATH_NAME);
    }

    public static void main(String[] args) throws IOException {
        if (args.length != ERROR_EXIT_STATUS) {
            exitWithError(String.format("Usage:%n\t%s feeds_dir", FeedsDownloader.class));
        }
        new FeedsDownloader(Path.of(args[0])).downloadFeeds();
    }

    private static void exitWithError(String format) {
        System.err.println(format);
        System.exit(ERROR_EXIT_STATUS);
    }

    private static void verifyDirectoryExists(Path feedsPath) {
        if (!Files.isDirectory(feedsPath)) {
            exitWithError("Feeds dir is not a directory: " + feedsPath);
        }
    }

    private static Path createOrVerifyIsDirectory(Path feedsPath, String subPath) throws IOException {
        Path feedsSubPath = feedsPath.resolve(subPath);
        if (Files.exists(feedsSubPath)) {
            verifyDirectoryExists(feedsSubPath);
            return feedsSubPath;
        } else {
            return Files.createDirectories(feedsSubPath);
        }
    }

    private static void waitBetweenFeeds() {
        Instant until = Instant.now().plusSeconds(5);
        waitUntil(until);
    }

    private static void waitUntil(Instant until) {
        while (Instant.now().isBefore(until)) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }

    public void downloadFeeds() {
        try {
            Instant lastV4TakeEntry = null;
            Instant lastV4MedalChatEntry = null;
            Instant lastV4ZoneEntry = null;
            Instant lastV5TakeEntry = null;
            Instant lastV5MedalChatEntry = null;
            Instant lastV5ZoneEntry = null;
            while (true) {
                Instant nextDownload = Instant.now().plusSeconds(5 * 60).truncatedTo(ChronoUnit.SECONDS);
                lastV4TakeEntry = getFeed(feedsV4Path, FEEDS_V4_REQUEST, "takeover", "feeds_takeover_%s.%sjson",
                        lastV4TakeEntry);
                waitBetweenFeeds();
                lastV4MedalChatEntry = getFeed(feedsV4Path, FEEDS_V4_REQUEST, "medal+chat",
                        "feeds_medal_chat_%s.%sjson", lastV4MedalChatEntry);
                waitBetweenFeeds();
                lastV4ZoneEntry = getFeed(feedsV4Path, FEEDS_V4_REQUEST, "zone", "feeds_zone_%s.%sjson",
                        lastV4ZoneEntry);
                waitBetweenFeeds();
                lastV5TakeEntry = getFeed(feedsV5Path, FEEDS_V5_REQUEST, "takeover", "feeds_takeover_%s.%sjson",
                        lastV5TakeEntry);
                waitBetweenFeeds();
                lastV5MedalChatEntry = getFeed(feedsV5Path, FEEDS_V5_REQUEST, "medal+chat",
                        "feeds_medal_chat_%s.%sjson", lastV5MedalChatEntry);
                waitBetweenFeeds();
                lastV5ZoneEntry = getFeed(feedsV5Path, FEEDS_V5_REQUEST, "zone", "feeds_zone_%s.%sjson",
                        lastV5ZoneEntry);
                logger.info("Sleeping until {}", nextDownload);
                waitUntil(nextDownload);
            }
        } catch (Throwable e) {
            logger.error("Exception in downloadsFeeds(feedsV4Path: \"{}\", feedsV5Path: \"{}\") :", feedsV4Path, feedsV5Path, e);
            System.exit(-1);
        }
    }

    private Instant getFeed(Path feedPath, String feedsRequest, String feed, String filenamePattern, Instant since) {
        String logQuantifier = String.format("%s (%s)", feed, (FEEDS_V4_REQUEST.equals(feedsRequest)) ? "v4" : "v5");
        String json = null;
        Instant lastEntryTime = null;
        Path file = null;
        try {
            try {
                json = getFeedsJSON(feedsRequest, feed, since);
            } catch (IOException e) {
                logger.error("{} Unable to get JSON: ", logQuantifier, e);
                return null;
            }
            if (json == null || json.equals("[]")) {
                if (since != null) {
                    logger.error("{} No data since {}.", logQuantifier, since);
                } else {
                    logger.error("{} No data.", logQuantifier);
                }
                return null;
            }
            try {
                lastEntryTime = getLastEntryTime(json);
            } catch (Exception e) {
                logger.error("{} Unable to retrieve time from JSON: ", logQuantifier, e);
                logger.error("{} json: {}", logQuantifier, json);
            }
            try {
                file = getFilePath(feedPath, filenamePattern, lastEntryTime);
                Files.writeString(file, json, StandardCharsets.UTF_8);
                logger.info("Downloaded {}", file);
            } catch (IOException e) {
                logger.error("{} Unable to store to {}:", logQuantifier, file, e);
                Path tempFile = null;
                try {
                    tempFile = Files.createTempFile(feedPath, "feed_download", ".json");
                    Files.writeString(tempFile, json, StandardCharsets.UTF_8);
                    logger.info("Stored {}", tempFile);
                } catch (IOException ex) {
                    logger.error("{} Unable to store to {}:", logQuantifier, tempFile, ex);
                    logger.error("{} json: {}", logQuantifier, json);
                }
                return since;
            }
            return (lastEntryTime == null) ? null : Instant.from(lastEntryTime).minusSeconds(1);
        } catch (Throwable e) {
            logger.error("Exception in getFeed(\"{}\", \"{}\", \"{}\", \"{}\", {}): ", feedPath, feedsRequest, feed, filenamePattern, since, e);
            logger.error("{} json:          {}", logQuantifier, json);
            logger.error("{} lastEntryTime: {}", logQuantifier, lastEntryTime);
            logger.error("{} file:          {}", logQuantifier, file);
            return null;
        }
    }

    private String getFeedsJSON(String feedsRequest, String feed, Instant since) throws IOException {
        String afterDate = "";
        if (since != null) {
            afterDate = "?afterDate=" + TimeUtil.turfAPITimestampFormatter(since);
        }
        return URLReader.getRequest(feedsRequest + '/' + feed + afterDate);
    }

    private Instant getLastEntryTime(String json) {
        Instant latest = null;
        for (JsonNode node : JacksonUtil.readValue(json, JsonNode[].class)) {
            String timeStamp = node.get("time").asText();
            Instant instant = TimeUtil.turfAPITimestampToInstant(timeStamp);
            if (latest == null || instant.isAfter(latest)) {
                latest = instant;
            }
        }
        return latest;
    }

    private Path getFilePath(Path feedPath, String filenamePattern, Instant lastEntryTime) throws IOException {
        String timeString = toTimeString(lastEntryTime);
        String name = String.format(filenamePattern, timeString, "");
        Path filePath = feedPath.resolve(name);
        if (Files.exists(filePath)) {
            String nowString = toTimeString(Instant.now());
            name = String.format(filenamePattern, timeString, nowString + '.');
            filePath = feedPath.resolve(name);
            if (Files.exists(filePath)) {
                filePath = Files.createTempFile(feedPath, name.substring(0, name.indexOf(".json") + 1), ".json");
            }
        }
        return filePath;
    }

    private String toTimeString(Instant instant) {
        if (instant == null) {
            instant = Instant.now();
            logger.debug("toTimeString(null) - using instant {}", instant);
        }
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
        return DATE_TIME_FORMATTER.format(localDateTime);
    }
}
