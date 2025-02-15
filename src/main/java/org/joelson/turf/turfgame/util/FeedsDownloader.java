package org.joelson.turf.turfgame.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.joelson.turf.util.JacksonUtil;
import org.joelson.turf.util.TimeUtil;
import org.joelson.turf.util.URLReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class FeedsDownloader {

    private static final String FEEDS_V4_REQUEST = "https://api.turfgame.com/v4/feeds";
    private static final String FEEDS_V5_REQUEST = "https://api.turfgame.com/v5/feeds";
    private static final String FEEDS_V6_REQUEST = "https://api.turfgame.com/unstable/feeds";

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    private static final Logger logger = LoggerFactory.getLogger(FeedsDownloader.class);

    private static final String FEEDS_V4_PATH_NAME = "feeds_v4";
    private static final String FEEDS_V5_PATH_NAME = "feeds_v5";
    private static final String FEEDS_V6_PATH_NAME = "feeds_v6";
    private static final int ERROR_EXIT_STATUS = 1;

    private final Path feedsV4Path;
    private final Path feedsV5Path;
    private final Path feedsV6Path;
    private final int timeOffset;

    public FeedsDownloader(Path feedsPath, int timeOffset) throws IOException {
        Objects.requireNonNull(feedsPath, "feedsPath is null");
        if (!Files.exists(feedsPath)) {
            exitWithError("Feeds dir does not exist: " + feedsPath);
        }
        if (timeOffset < 0 || timeOffset >= 5 * 60) {
            exitWithError("Invalid time offset " + timeOffset);
        }
        verifyDirectoryExists(feedsPath);
        feedsV4Path = createOrVerifyIsDirectory(feedsPath, FEEDS_V4_PATH_NAME);
        feedsV5Path = createOrVerifyIsDirectory(feedsPath, FEEDS_V5_PATH_NAME);
        feedsV6Path = createOrVerifyIsDirectory(feedsPath, FEEDS_V6_PATH_NAME);
        this.timeOffset = timeOffset;
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            exitWithError(String.format("Usage:%n\t%s feeds_dir time_offset", FeedsDownloader.class));
        }
        new FeedsDownloader(Path.of(args[0]), Integer.parseInt(args[1])).downloadFeeds();
    }

    private static void exitWithError(String msg) {
        logger.error(msg);
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

    private static void waitUntilNextRequest() {
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

    private Instant calcFirstDownloadTime() {
        // truncate down to minutes
        Instant now = Instant.now().truncatedTo(ChronoUnit.MINUTES);
        // get minutes
        int minutes = now.atZone(ZoneId.systemDefault()).getMinute();
        // mod 5 down
        Instant mod = now.minusSeconds((minutes % 5) * 60);
        // add time offset
        Instant next = mod.plusSeconds(timeOffset);
        // add 5 minutes until next is after now
        while (next.isBefore(Instant.now())) {
            next = next.plusSeconds(5 * 60);
        }
        return next;
    }

    public void downloadFeeds() {
        Instant nextDownload = calcFirstDownloadTime();
        try {
            Instant lastV4TakeEntry = null;
            Instant lastV4MedalChatEntry = null;
            Instant lastV4ZoneEntry = null;
            Instant lastV5TakeEntry = null;
            Instant lastV5MedalChatEntry = null;
            Instant lastV5ZoneEntry = null;
            Instant lastV6TakeEntry = null;
            Instant lastV6MedalChatEntry = null;
            Instant lastV6ZoneEntry = null;
            while (true) {
                logger.info("Sleeping until {}", nextDownload);
                waitUntil(nextDownload);
                nextDownload = nextDownload.plusSeconds(5 * 60);
                lastV4TakeEntry = downloadFeed(feedsV4Path, FEEDS_V4_REQUEST, "takeover", "feeds_takeover_%s.%sjson",
                        lastV4TakeEntry);
                waitUntilNextRequest();
                lastV4MedalChatEntry = downloadFeed(feedsV4Path, FEEDS_V4_REQUEST, "medal+chat",
                        "feeds_medal_chat_%s.%sjson", lastV4MedalChatEntry);
                waitUntilNextRequest();
                lastV4ZoneEntry = downloadFeed(feedsV4Path, FEEDS_V4_REQUEST, "zone", "feeds_zone_%s.%sjson",
                        lastV4ZoneEntry);
                waitUntilNextRequest();
                lastV5TakeEntry = downloadFeed(feedsV5Path, FEEDS_V5_REQUEST, "takeover", "feeds_takeover_%s.%sjson",
                        lastV5TakeEntry);
                waitUntilNextRequest();
                lastV5MedalChatEntry = downloadFeed(feedsV5Path, FEEDS_V5_REQUEST, "medal+chat",
                        "feeds_medal_chat_%s.%sjson", lastV5MedalChatEntry);
                waitUntilNextRequest();
                lastV5ZoneEntry = downloadFeed(feedsV5Path, FEEDS_V5_REQUEST, "zone", "feeds_zone_%s.%sjson",
                        lastV5ZoneEntry);
                waitUntilNextRequest();
                lastV6TakeEntry = downloadFeed(feedsV6Path, FEEDS_V6_REQUEST, "takeover", "feeds_takeover_%s.%sjson",
                        lastV6TakeEntry);
                waitUntilNextRequest();
                lastV6MedalChatEntry = downloadFeed(feedsV6Path, FEEDS_V6_REQUEST, "medal+chat",
                        "feeds_medal_chat_%s.%sjson", lastV6MedalChatEntry);
                waitUntilNextRequest();
                lastV6ZoneEntry = downloadFeed(feedsV6Path, FEEDS_V6_REQUEST, "zone", "feeds_zone_%s.%sjson",
                        lastV6ZoneEntry);
            }
        } catch (Throwable e) {
            logger.error("Exception in downloadsFeeds(feedsV4Path: \"{}\", feedsV5Path: \"{}\", feedsV6Path: \"{}\") :",
                    feedsV4Path, feedsV5Path, feedsV6Path, e);
            System.exit(-1);
        }
    }

    private Instant downloadFeed(
            Path feedPath, String feedRequest, String feed, String filenamePattern, Instant since) {
        String version = switch (feedRequest) {
            case FEEDS_V4_REQUEST -> "v4";
            case FEEDS_V5_REQUEST -> "v5";
            case FEEDS_V6_REQUEST -> "v6/unstable";
            default -> String.format("unknown(%s)", feedRequest);
        };
        String logQuantifier = String.format("%s (%s)", feed, version);
        String content = null;
        Instant lastEntryTime = null;
        Path file = null;
        try {
            try {
                content = downloadFeedContent(feedRequest, feed, since);
            } catch (IOException e) {
                logger.error("{} Unable to get content: ", logQuantifier, e);
                return null;
            }
            if (content == null || content.equals("[]")) {
                if (since != null) {
                    logger.error("{} No data since {}.", logQuantifier, since);
                } else {
                    logger.error("{} No data.", logQuantifier);
                }
                return null;
            }
            try {
                lastEntryTime = getLastEntryTime(content);
            } catch (JsonProcessingException e) {
                logger.error("{} Unable to retrieve time from JSON: ", logQuantifier, e);
                logger.error("{} content: {}", logQuantifier, content);
            }
            try {
                file = getFilePath(feedPath, filenamePattern, lastEntryTime);
                Files.writeString(file, content, StandardCharsets.UTF_8);
                logger.info("Downloaded {}", file);
            } catch (IOException e) {
                logger.error("{} Unable to store to {}:", logQuantifier, file, e);
                Path tempFile = null;
                try {
                    tempFile = Files.createTempFile(feedPath, "feed_download", ".content");
                    Files.writeString(tempFile, content, StandardCharsets.UTF_8);
                    logger.info("Stored {}", tempFile);
                } catch (IOException ex) {
                    logger.error("{} Unable to store to {}:", logQuantifier, tempFile, ex);
                    logger.error("{} content: {}", logQuantifier, content);
                }
                return since;
            }
            return (lastEntryTime == null) ? null : Instant.from(lastEntryTime).minusSeconds(1);
        } catch (Throwable e) {
            logger.error("Exception in getFeed(\"{}\", \"{}\", \"{}\", \"{}\", {}): ", feedPath, feedRequest, feed, filenamePattern, since, e);
            logger.error("{} content:       {}", logQuantifier, content);
            logger.error("{} lastEntryTime: {}", logQuantifier, lastEntryTime);
            logger.error("{} file:          {}", logQuantifier, file);
            return null;
        }
    }

    private String downloadFeedContent(String feedRequest, String feed, Instant since) throws IOException {
        String afterDate = "";
        if (since != null) {
            afterDate = "?afterDate=" + TimeUtil.turfAPITimestampFormatter(since);
        }
        String request = feedRequest + '/' + feed + afterDate;
        URLReader.Response response = URLReader.getTurfgameRequest(request);
        if (response.status() != HttpURLConnection.HTTP_OK) {
            logger.error("Not 200/OK, response status: {}, request URL: {}", response.status(), request);
        }
        return response.body();
    }

    private Instant getLastEntryTime(String json) throws JsonProcessingException {
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
