package org.joelson.turf.turfgame.util;

import com.fasterxml.jackson.databind.JsonNode;
import org.joelson.turf.util.JacksonUtil;
import org.joelson.turf.util.TimeUtil;
import org.joelson.turf.util.URLReader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class FeedsDownloader {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.printf("Usage:%n\t%s feedsRequest", FeedsDownloader.class);
            return;
        }
        handleFeeds(args[0]);
    }

    public static void handleFeeds(String feedsRequest) {
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
        try {
            String json = getFeedsJSON(feedsRequest, feed, since);
            if (json.equals("[]")) {
                log("No data for " + feed + " since " + since);
                return since;
            }
            Instant lastEntryTime = getLastEntryTime(json);
            if (lastEntryTime == null) {
                log("JSON: " + json);
                log("lastEntryTime is null");
            }
            Path file = getFilePath(filenamePattern, lastEntryTime);
            Files.writeString(file, json, StandardCharsets.UTF_8);
            log("Downloaded " + file + " at " + Instant.now());
            return Instant.from(lastEntryTime).minusSeconds(1);
        } catch (Throwable e) {
            log(Instant.now() + ": " + e);
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
        while (Instant.now().isBefore(until)) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }

    private static void waitUntilNext() {
        Instant until = Instant.now().plusSeconds(5 * 60);
        log("Sleeping until " + until);
        while (Instant.now().isBefore(until)) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }

    private static void log(String msg) {
        System.out.printf("[%s] %s%n", Thread.currentThread().getName(), msg);
    }
}
