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
import java.util.function.Consumer;

public abstract class FeedsReader {

    private static String readFile(Path path, Consumer<Path> forEachPath) {
        String content = null;
        try {
            content = Files.readString(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        forEachPath.accept(path);
        return content;
    }

    private static List<JsonNode> readNodeFile(Path path) {
        String content = readFile(path, p -> System.out.println("*** " + p));
        List<JsonNode> nodes = Arrays.asList(JacksonUtil.readValue(content, JsonNode[].class));
        nodes.sort(new FeedNodeComparator());
        return nodes;
    }

    protected void printUniqueNodes(String[] filenames) {
        SortedSet<JsonNode> feedNodes = new TreeSet<>(new FeedNodeComparator());
        for (String filename : filenames) {
            handleNodeFiles(Path.of(filename), node -> handleNode(feedNodes, node));
        }
    }

    private void handleNodeFiles(Path path, Consumer<JsonNode> forEachNode) {
        try {
            FilesUtil.forEachFile(path, true, p -> handleNodes(forEachNode, readNodeFile(p)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleNodes(Consumer<JsonNode> forEachNode, List<JsonNode> jsonNodes) {
        jsonNodes.forEach(forEachNode);
    }

    private void handleNode(SortedSet<JsonNode> uniqueNodes, JsonNode node) {
        if (uniqueNodes.contains(node)) {
            System.out.println("    Already contains node " + node);
        } else {
            uniqueNodes.add(node);
            String type = node.get("type").asText();
            FeedObject feedObject = JacksonUtil.treeToValue(node, getJSONClass(type));
            if (!feedObject.getType().equals(type)) {
                throw new RuntimeException("Illegal type " + type + " for " + feedObject);
            }
            System.out.println(" ->  " + feedObject);
        }
    }

    public void handleFeedObjectFile(Path path, Consumer<Path> forEachPath, Consumer<FeedObject> forEachFeedObject) {
        try {
            FilesUtil.forEachFile(path, true, p -> handleFeedObjects(readFile(p, forEachPath), forEachFeedObject));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void handleFeedObjects(String content, Consumer<FeedObject> forEachFeedObject) {
        List<JsonNode> nodes = Arrays.asList(JacksonUtil.readValue(content, JsonNode[].class));
        nodes.forEach(node -> handleFeedObject(node, forEachFeedObject));
    }

    private void handleFeedObject(JsonNode node, Consumer<FeedObject> forEachFeedObject) {
        String type = node.get("type").asText();
        FeedObject feedObject = JacksonUtil.treeToValue(node, getJSONClass(type));
        if (!feedObject.getType().equals(type)) {
            throw new RuntimeException("Illegal type " + type + " for " + feedObject);
        }
        forEachFeedObject.accept(feedObject);
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
