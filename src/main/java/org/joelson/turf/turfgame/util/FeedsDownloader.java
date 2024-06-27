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

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    private static Path logPath;

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.printf("Usage:%n\t%s feedsRequest", FeedsDownloader.class);
            return;
        }
        handleFeeds(args[0]);
    }

    public static void handleFeeds(String feedsRequest) throws IOException {
        logPath = Files.createTempFile("turfgame-feedsdownloader-", ".txt");
        System.out.println(logPath);
        log("Created log file " + logPath);

        Instant lastTakeEntry = null;
        Instant lastMedalChatEntry = null;
        Instant lastZoneEntry = null;
        while (true) {
            lastTakeEntry = getFeed(feedsRequest, "takeover", "feeds_takeover_%s.%sjson", lastTakeEntry);
            waitBetweenFeeds();
            lastMedalChatEntry = getFeed(feedsRequest, "medal+chat", "feeds_medal_chat_%s.%sjson", lastMedalChatEntry);
            waitBetweenFeeds();
            lastZoneEntry = getFeed(feedsRequest, "zone", "feeds_zone_%s.%sjson", lastZoneEntry);
            waitUntilNext();
        }
    }

    private static Instant getFeed(String feedsRequest, String feed, String filenamePattern, Instant since) {
        String json;
        try {
            json = getFeedsJSON(feedsRequest, feed, since);
        } catch (IOException e) {
            log(String.format("%s: Unable to get JSON - %s", Instant.now(), e));
            return since;
        }
        if ("[]".equals(json)) {
            log("No data for " + feed + " since " + since);
            return since;
        }
        Instant lastEntryTime;
        try {
            lastEntryTime = getLastEntryTime(json);
        } catch (Exception e) {
            log("Unable to retrieve time from JSON: " + e);
            lastEntryTime = null;
        }
        if (lastEntryTime == null) {
            log("JSON: " + json);
            log("lastEntryTime is null");
        }
        Path file = null;
        try {
            file = getFilePath(filenamePattern, lastEntryTime);
            Files.writeString(file, json, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log(String.format("%s: Unable to store to %s - %s", Instant.now(), file, e));
            Path tempFile = null;
            try {
                tempFile = Files.createTempFile("feed_download", ".json");
                Files.writeString(tempFile, json, StandardCharsets.UTF_8);
            } catch (IOException ex) {
                log(String.format("%s: Unable to store to %s - %s", Instant.now(), tempFile, e));
                log(json);
            }
            return since;
        }
        log("Downloaded " + file + " at " + Instant.now());
        return (lastEntryTime == null) ? null : Instant.from(lastEntryTime).minusSeconds(1);
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

    private static Path getFilePath(String filenamePattern, Instant lastEntryTime) throws IOException {
        String timeString = toTimeString(lastEntryTime);
        String name = String.format(filenamePattern, timeString, "");
        Path filePath = Path.of(".", name);
        if (Files.exists(filePath)) {
            String nowString = toTimeString(Instant.now());
            name = String.format(filenamePattern, timeString, nowString + '.');
            filePath = Path.of(".", name);
            if (Files.exists(filePath)) {
                filePath = Files.createTempFile(Path.of("."), name.substring(0, name.indexOf(".json") + 1), ".json");
            }
        }
        return filePath;
    }

    private static String toTimeString(Instant instant) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
        return DATE_TIME_FORMATTER.format(localDateTime);
    }

    private static void waitBetweenFeeds() {
        Instant until = Instant.now().plusSeconds(5);
        waitUntil(until);
    }

    private static void waitUntilNext() {
        Instant until = Instant.now().plusSeconds(5 * 60);
        log("Sleeping until " + until);
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
        String s = String.format("[%s] %s%n", Thread.currentThread().getName(), msg);
        System.out.printf(s);
        try {
            Files.writeString(logPath, s, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.printf("[%s] Unable to log to file %s - %s%n", Thread.currentThread().getName(), logPath, e);
        }
    }
}
