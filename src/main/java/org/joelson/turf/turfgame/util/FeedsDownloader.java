package org.joelson.turf.turfgame.util;

import com.fasterxml.jackson.databind.JsonNode;
import org.joelson.turf.util.JacksonUtil;
import org.joelson.turf.util.TimeUtil;
import org.joelson.turf.util.URLReader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class FeedsDownloader {

    private static final String FEEDS_V4_REQUEST = "https://api.turfgame.com/v4/feeds";
    private static final String FEEDS_V5_REQUEST = "https://api.turfgame.com/unstable/feeds";

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    private static Path logPath;

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.printf("Usage:%n\t%s feedsv4_dir feedsv5_dir", FeedsDownloader.class);
            return;
        }
        Path feedsV4Path = Path.of(args[0]);
        if (!Files.exists(feedsV4Path) || !Files.isDirectory(feedsV4Path)) {
            System.out.println("Feeds v4 dir does not exist or is not a directory: " + feedsV4Path);
            return;
        }
        Path feedsV5Path = Path.of(args[1]);
        if (!Files.exists(feedsV5Path) || !Files.isDirectory(feedsV5Path)) {
            System.out.println("Feeds v5 dir does not exist or is not a directory: " + feedsV5Path);
            return;
        }
        handleFeeds(feedsV4Path, feedsV5Path);
    }

    public static void handleFeeds(Path feedsV4Path, Path feedsV5Path) {
        try {
            logPath = Files.createTempFile("turfgame-feedsdownloader-", ".txt");
            System.out.println(logPath);
            log("Created log file " + logPath);

            Instant lastV4TakeEntry = null;
            Instant lastV4MedalChatEntry = null;
            Instant lastV4ZoneEntry = null;
            Instant lastV5TakeEntry = null;
            Instant lastV5MedalChatEntry = null;
            Instant lastV5ZoneEntry = null;
            while (true) {
                Instant nextDownload = Instant.now().plusSeconds(5 * 60);
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
                log("Sleeping until " + nextDownload);
                waitUntil(nextDownload);
            }
        } catch (Throwable e) {
            log("Exception in handleFeeds(" + feedsV4Path + ", " + feedsV5Path + ") - " + e);
        }
    }

    private static Instant getFeed(
            Path feedPath, String feedsRequest, String feed, String filenamePattern, Instant since) {
        String json = null;
        Instant lastEntryTime = null;
        Path file = null;
        try {
            try {
                json = getFeedsJSON(feedsRequest, feed, since);
            } catch (IOException e) {
                log(Instant.now() + ": Unable to get JSON - " + e);
                return since;
            }
            if (json == null || json.equals("[]")) {
                log("No data for " + feed + " since " + since);
                return since;
            }
            try {
                lastEntryTime = getLastEntryTime(json);
            } catch (Exception e) {
                log("Unable to retrieve time from JSON: " + e);
            }
            try {
                file = getFilePath(feedPath, filenamePattern, lastEntryTime);
                Files.writeString(file, json, StandardCharsets.UTF_8);
                log("Downloaded " + file);
            } catch (IOException e) {
                log(Instant.now() + ": Unable to store to " + file + " - " + e);
                Path tempFile = null;
                try {
                    tempFile = Files.createTempFile("feed_download", ".json");
                    Files.writeString(tempFile, json, StandardCharsets.UTF_8);
                } catch (IOException ex) {
                    log(Instant.now() + ": Unable to store to " + tempFile + " - " + e);
                    log(json);
                }
                return since;
            }
            return (lastEntryTime == null) ? null : Instant.from(lastEntryTime).minusSeconds(1);
        } catch (Throwable e) {
            log("Exception in getFeed(" + feedPath + "\"" + feedsRequest + "\", \"" + feed + "\", \"" + filenamePattern
                    + "\", " + since + ") - " + e);
            log("  json:          " + json);
            log("  lastEntryTime: " + lastEntryTime);
            log("  file:          " + file);
            return null;
        }
    }

    private static String getFeedsJSON(String feedsRequest, String feed, Instant since) throws IOException {
        String afterDate = "";
        if (since != null) {
            afterDate = "?afterDate=" + TimeUtil.turfAPITimestampFormatter(since);
        }
        return URLReader.getRequest(feedsRequest + '/' + feed + afterDate);
    }

    private static Instant getLastEntryTime(String json) {
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

    private static Path getFilePath(Path feedPath, String filenamePattern, Instant lastEntryTime) throws IOException {
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

    private static String toTimeString(Instant instant) {
        if (instant == null) {
            instant = Instant.now();
            log("toTimeString(null) - using instant " + instant);
        }
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
        return DATE_TIME_FORMATTER.format(localDateTime);
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

    private static void log(String msg) {
        String s = "[" + Thread.currentThread().getName() + "] " + msg + "\n";
        System.out.print(s);
        try {
            Files.writeString(logPath, s, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.println(
                    "[" + Thread.currentThread().getName() + "] Unable to log to file " + logPath + " - " + e);
        }
    }
}
