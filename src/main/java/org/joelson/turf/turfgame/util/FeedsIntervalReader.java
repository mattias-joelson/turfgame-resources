package org.joelson.turf.turfgame.util;

import com.fasterxml.jackson.databind.JsonNode;
import org.joelson.turf.util.FilesUtil;
import org.joelson.turf.util.JacksonUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class FeedsIntervalReader {

    private static int fileCount = 0;
    private static final SortedSet<FeedInterval> feedIntervals = new TreeSet<>(new FeedIntervalComparator());
    private static final DefaultFeedContentErrorHandler errorHandler = new DefaultFeedContentErrorHandler();

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.printf("Usage:\n\t%s feed_file1.json ...%n", FeedsIntervalReader.class.getName());
            System.exit(-1);
        }

        for (String filename : args) {
            FilesUtil.forEachFile(Path.of(filename), true, new FeedsPathComparator(),
                    FeedsIntervalReader::readFeedFile);
        }
        feedIntervals.forEach(feedInterval
                -> System.out.printf("%s: %s - %s%n", feedInterval.type, feedInterval.start, feedInterval.end));
        List<Path> errorPaths = errorHandler.getErrorPaths();
        if (!errorPaths.isEmpty()) {
            System.err.println("Files with errors: " + errorPaths.size());
            for (Path errorPath : errorPaths) {
                System.err.println("    " + errorPath);
            }
        }
    }

    private static void readFeedNodes(List<JsonNode> fileNodes) throws IOException {
        if (fileNodes.size() <= 1) {
            return;
        }
        String nodeType = fileNodes.getFirst().get("type").asText();
        FeedType type = switch (nodeType) {
            case "chat", "medal" -> FeedType.CHAT_MEDAL;
            case "takeover" -> FeedType.TAKEOVER;
            case "zone" -> FeedType.ZONE;
            case null -> throw new NoFeedTypeException();
            default -> throw new UnknownFeedTypeException(nodeType);
       };
        String start = fileNodes.getLast().get("time").asText();
        String end = fileNodes.getFirst().get("time").asText();
        if (start.equals(end)) {
            return;
        }
        FeedInterval newInterval = new FeedInterval(type, start, end);
        outer:
        while (true) {
            for (FeedInterval feedInterval : feedIntervals) {
                if (feedInterval.intersects(newInterval)) {
                    newInterval = joinIntervals(feedInterval, newInterval);
                    feedIntervals.remove(feedInterval);
                    continue outer;
                }
            }
            break;
        }
        feedIntervals.add(newInterval);
    }

    private static FeedInterval joinIntervals(FeedInterval interval1, FeedInterval interval2) {
        String start = interval1.start.compareTo(interval2.start) <= 0 ? interval1.start : interval2.start;
        String end = interval1.end.compareTo(interval2.end) > 0 ? interval1.end : interval2.end;
        return new FeedInterval(interval1.type, start, end);
    }

    private static void readFeedFile(Path feedPath) {
        if (fileCount % 100 == 0) {
            System.out.printf("*** Reading %s (%d)%n", feedPath, fileCount);
        }
        fileCount += 1;
        String content = null;
        try {
            content = Files.readString(feedPath);
            List<JsonNode> jsonNodes = readJsonNodes(content);
            readFeedNodes(jsonNodes);
        } catch (IOException e) {
            errorHandler.handleErrorContent(feedPath, content, e);
        }
    }

    private static List<JsonNode> readJsonNodes(String content) throws IOException {
        return Arrays.asList(JacksonUtil.readValue(content, JsonNode[].class));
    }

    private enum FeedType {
        CHAT_MEDAL, TAKEOVER, ZONE
    }

    private record FeedInterval(FeedType type, String start, String end) {

        private FeedInterval {
            if (start.compareTo(end) >= 0) {
                throw new IllegalArgumentException("Wrong order " + start + " and " + end);
            }
        }

        public boolean intersects(FeedInterval that) {
            if (type != that.type) {
                return false;
            }
            if (end.compareTo(that.start) < 0 || that.end.compareTo(start) < 0) {
                return false;
            }
            if (end.compareTo(that.start) >= 0 || that.end.compareTo(start) >= 0) {
                return true;
            }
            throw new IllegalStateException("Should not come here...");
        }
    }

    private static class FeedIntervalComparator implements Comparator<FeedInterval> {
        @Override
        public int compare(FeedInterval fi1, FeedInterval fi2) {
            if (fi1.type != fi2.type) {
                return fi1.type.ordinal() - fi2.type.ordinal();
            }
            int startCompare = fi1.start.compareTo(fi2.start);
            if (startCompare != 0) {
                return startCompare;
            }
            return fi1.end.compareTo(fi2.end);
        }
    }
}
