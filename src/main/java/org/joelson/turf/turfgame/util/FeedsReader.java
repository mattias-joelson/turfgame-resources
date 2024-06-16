package org.joelson.turf.turfgame.util;

import com.fasterxml.jackson.databind.JsonNode;
import org.joelson.turf.turfgame.FeedObject;
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

public abstract class FeedsReader {

    private static List<JsonNode> readFeedFile(Path feedPath) {
        String content = null;
        try {
            content = Files.readString(feedPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return readJsonNodes(feedPath, content);
    }

    private static List<JsonNode> readJsonNodes(Path path, String content) {
        System.out.println("*** " + path);
        List<JsonNode> nodes = Arrays.asList(JacksonUtil.readValue(content, JsonNode[].class));
        nodes.sort(new FeedNodeComparator());
        return nodes;
    }

    protected void readFiles(String[] filenames) throws IOException {
        SortedSet<JsonNode> feedNodes = new TreeSet<>(new FeedNodeComparator());
        for (String filename : filenames) {
            FilesUtil.forEachFile(Path.of(filename), true, path -> readFeedNodes(feedNodes, readFeedFile(path)));
        }
    }

    private void readFeedNodes(SortedSet<JsonNode> feedNodes, List<JsonNode> fileNodes) {
        for (JsonNode fileNode : fileNodes) {
            if (feedNodes.contains(fileNode)) {
                System.out.println("    Already contains node " + fileNode);
            } else {
                feedNodes.add(fileNode);
                String type = fileNode.get("type").asText();
                FeedObject feedObject = JacksonUtil.treeToValue(fileNode, getJSONClass(type));
                if (!feedObject.getType().equals(type)) {
                    throw new RuntimeException("Illegal type " + type + " for " + feedObject);
                }
                System.out.println(" ->  " + feedObject);
                //String s = feedObject.toString();
            }
        }
    }

    protected abstract Class<? extends FeedObject> getJSONClass(String type);

    private static class FeedNodeComparator implements Comparator<JsonNode> {

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

        @Override
        public int compare(JsonNode o1, JsonNode o2) {
            int compare = compareInner(o1, o2);
            if (compare == 0 && !o1.equals(o2)) {
                System.out.println(
                        "    --- Node\n\t" + o1.toPrettyString() + "\n    differs from\n\t" + o2.toPrettyString());
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
            return switch (type) {
                case "chat" -> getSenderId(node1) - getSenderId(node2);
                case "medal" -> {
                    int userCompare = getUserId(node1) - getUserId(node2);
                    yield (userCompare == 0) ? getMedalId(node1) - getMedalId(node2) : userCompare;
                }
                case "takeover", "zone" -> getZoneId(node1) - getZoneId(node2);
                default -> throw new RuntimeException("Invalid type " + type);
            };
        }
    }
}
