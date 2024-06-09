package org.joelson.mattias.turfgame.apiv4;

import com.fasterxml.jackson.databind.JsonNode;
import org.joelson.mattias.turfgame.util.FilesUtil;
import org.joelson.mattias.turfgame.util.JacksonUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FeedsReader {

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println(String.format("Usage:\n\t%s feed_file1.json ...", FeedsReader.class.getName()));
            return;
        }
        SortedSet<JsonNode> feedNodes = new TreeSet<>(new FeedNodeComparator());
        for (String filename : args) {
            System.out.println("*** Reading " + filename);
            Path feedPath = Path.of(filename);
            if (FilesUtil.isZipFile(feedPath)) {
                ZipFile zipFile = new ZipFile(feedPath.toFile());
                zipFile.stream().forEach(zipEntry -> readFeedNodes(feedNodes, readZipEntry(feedPath, zipFile, zipEntry)));
            } else {
                readFeedNodes(feedNodes, readFeedFile(feedPath));
            }
        }
    }

    private static void readFeedNodes(SortedSet<JsonNode> feedNodes, List<JsonNode> fileNodes) {
        List<FeedObject> objects = new ArrayList<>();
        for (JsonNode fileNode : fileNodes) {
            switch (fileNode.get("type").asText()) {
                case "chat":
                    objects.add(JacksonUtil.treeToValue(fileNode, ChatFeed.class));
                    break;
                case "medal":
                    objects.add(JacksonUtil.treeToValue(fileNode, MedalFeed.class));
                    break;
                case "takeover":
                    objects.add(JacksonUtil.treeToValue(fileNode, TakeoverFeed.class));
                    break;
                case "zone":
                    objects.add(JacksonUtil.treeToValue(fileNode, ZoneFeed.class));
                    break;
                default:
                    throw new RuntimeException("Unknown type " + fileNode.get("type").asText());
            }
        }
        for (JsonNode fileNode : fileNodes) {
            if (feedNodes.contains(fileNode)) {
                System.out.println("    Already contains node " + fileNode);
            } else {
                feedNodes.add(fileNode);
            }
        }
    }

    private static List<JsonNode> readFeedFile(Path feedPath) throws IOException {
        return readJsonNodes(feedPath.toString(), Files.readString(feedPath));
    }

    private static List<JsonNode> readZipEntry(Path feedPath, ZipFile zipFile, ZipEntry zipEntry) {
        System.out.println("  * Reading " + zipEntry.getName());
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(zipFile.getInputStream(zipEntry)));
            List<String> rows = new ArrayList<>();
            String row;
            while ((row = reader.readLine()) != null) {
                rows.add(row);
            }
            String content = String.join("\n", rows);
            return readJsonNodes(feedPath + " -> " + zipEntry.getName(), content);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
            return Collections.emptyList();
        }
    }

    private static List<JsonNode> readJsonNodes(String path, String content) {
        if (content.isEmpty() || content.startsWith("<html>")) {
            System.err.println("--- File " + path + " contains no data!");
            return Collections.emptyList();
        }
        List<JsonNode> nodes = Arrays.asList(JacksonUtil.readValue(content, JsonNode[].class));
        nodes.sort(new FeedNodeComparator());
        return nodes;
    }

    private static class FeedNodeComparator implements Comparator<JsonNode> {

        @Override
        public int compare(JsonNode o1, JsonNode o2) {
            int compare = compareInner(o1, o2);
            if (compare == 0 && !o1.equals(o2)) {
                System.out.println("    --- Node\n\t" + o1.toPrettyString() + "\n    differs from\n\t" + o2.toPrettyString());
            }
            return compare;
        }

        public int compareInner(JsonNode node1, JsonNode node2) {
            int timeCompare = getTime(node1).compareTo(getTime(node2));
            if (timeCompare != 0) {
                return timeCompare;
            }
            String type = getType(node1);
            int typeCompare = type.compareTo(getType(node2));
            if (typeCompare != 0) {
                return typeCompare;
            }
            switch (type) {
                case "chat":
                    return getSenderId(node1) - getSenderId(node2);
                case "medal":
                    int userCompare = getUserId(node1) - getUserId(node2);
                    return (userCompare == 0) ? getMedalId(node1) - getMedalId(node2) : userCompare;
                case "takeover":
                case "zone":
                    return getZoneId(node1) - getZoneId(node2);
                default:
                    throw new RuntimeException("Invalid type " + type);
            }
        }

        private static String getTime(JsonNode node) {
            return node.get("time").asText();
        }

        private static String getType(JsonNode node) {
            return node.get("type").asText();
        }

        private static int getSenderId(JsonNode node) {
            return node.get("sender").get("id").intValue();
        }

        private static int getUserId(JsonNode node) {
            return node.get("user").get("id").intValue();
        }

        private static int getMedalId(JsonNode node) {
            return node.get("medal").intValue();
        }

        private static int getZoneId(JsonNode node) {
            return node.get("zone").get("id").intValue();
        }
    }
}
