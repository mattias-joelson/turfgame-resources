package org.joelson.turf.turfgame.util;

import com.fasterxml.jackson.databind.JsonNode;
import org.joelson.turf.turfgame.FeedObject;
import org.joelson.turf.util.FilesUtil;
import org.joelson.turf.util.JacksonUtil;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Consumer;

public class FeedsReader {

    private final Map<String, Class<? extends FeedObject>> typesToHandle;
    private final boolean reversed;
    private final FeedContentErrorHandler errorHandler;

    public FeedsReader(Map<String, Class<? extends FeedObject>> typesToHandle) {
        this(typesToHandle, new DefaultFeedContentErrorHandler(), true);
    }

    public FeedsReader(Map<String, Class<? extends FeedObject>> typesToHandle, Logger errorHandlerLogger) {
        this(typesToHandle, new DefaultFeedContentErrorHandler(errorHandlerLogger), true);
    }

    public FeedsReader(Map<String, Class<? extends FeedObject>> typesToHandle, FeedContentErrorHandler errorHandler) {
        this(typesToHandle, errorHandler, true);
    }

    public FeedsReader(Map<String, Class<? extends FeedObject>> typesToHandle, FeedContentErrorHandler errorHandler,
            boolean reversed) {
        this.typesToHandle = Objects.requireNonNull(typesToHandle);
        this.errorHandler = errorHandler;
        this.reversed = reversed;
    }

    private static String readFile(Path path, Consumer<Path> forEachPath) {
        String content;
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

    public static void printUniqueNodes(Map<String, Class<? extends FeedObject>> typesToHandle, String[] filenames) {
        new FeedsReader(typesToHandle).printUniqueNodes(filenames);
    }

    private static String getJsonNodeTime(JsonNode node) {
        JsonNode timeNode = node.get("time");
        if (timeNode == null) {
            throw new IllegalArgumentException("Node lacks attribute time: " + node);
        }
        return timeNode.asText();
    }

    protected void printUniqueNodes(String[] filenames) {
        SortedSet<JsonNode> feedNodes = new TreeSet<>(new FeedNodeComparator());
        for (String filename : filenames) {
            handleNodeFiles(Path.of(filename), node -> handleNode(feedNodes, node));
        }
    }

    private void handleNodeFiles(Path path, Consumer<JsonNode> forEachNode) {
        try {
            FilesUtil.forEachFile(path, true, new FeedsPathComparator(),
                    p -> handleNodes(forEachNode, readNodeFile(p)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleNodes(Consumer<JsonNode> forEachNode, List<JsonNode> jsonNodes) {
        jsonNodes.reversed().forEach(forEachNode);
    }

    private void handleNode(SortedSet<JsonNode> uniqueNodes, JsonNode node) {
        if (uniqueNodes.contains(node)) {
            System.out.println("    Already contains node " + node);
        } else {
            uniqueNodes.add(node);
            String type = node.get("type").asText();
            Class<? extends FeedObject> feedObjectClass = typesToHandle.get(type);
            if (feedObjectClass != null) {
                FeedObject feedObject = JacksonUtil.treeToValue(node, feedObjectClass);
                if (!feedObject.getType().equals(type)) {
                    throw new RuntimeException("Illegal type " + type + " for " + feedObject);
                }
                System.out.println(" ->  " + feedObject);
            } else {
                throw new RuntimeException("Unknown type " + type + " for node " + node);
            }
        }
    }

    public void handleFeedObjectFile(Path path, Consumer<Path> forEachPath, Consumer<FeedObject> forEachFeedObject) {
        Comparator<Path> pathComparator = (reversed) ? new FeedsPathComparator().reversed() : new FeedsPathComparator();
        handleFeedObjectFile(path, pathComparator, forEachPath, forEachFeedObject);
    }

    public void handleFeedObjectFile(Path path, Comparator<Path> comparePaths, Consumer<Path> forEachPath,
            Consumer<FeedObject> forEachFeedObject) {
        try {
            FilesUtil.forEachFile(path, true, comparePaths,
                    p -> handleFeedObjects(p, readFile(p, forEachPath), forEachFeedObject));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void handleFeedObjects(Path path, String content, Consumer<FeedObject> forEachFeedObject) {
        List<JsonNode> nodes = getJsonNodes(path, content);
        if (!nodes.isEmpty()) {
            String time = null;
            for (JsonNode node : nodes) {
                String nodeTime = getJsonNodeTime(node);
                if (time == null) {
                    time = nodeTime;
                } else if (!reversed) {
                    if (time.compareTo(nodeTime) <= 0) {
                        time = nodeTime;
                    } else {
                        throw new IllegalArgumentException(
                                "Node with time " + nodeTime + " is not after " + time + ": " + node);
                    }
                } else {
                    if (time.compareTo(nodeTime) >= 0) {
                        time = nodeTime;
                    } else {
                        throw new IllegalArgumentException(
                                "Node with time " + nodeTime + " is not before " + time + ": " + node);
                    }
                }
                handleFeedObject(node, forEachFeedObject);
            }
        }
    }

    private List<JsonNode> getJsonNodes(Path path, String content) {
        try {
            List<JsonNode> nodes = Arrays.asList(JacksonUtil.readValue(content, JsonNode[].class));
            return (reversed) ? nodes : nodes.reversed();
        } catch (RuntimeException e) {
            if (errorHandler != null) {
                return errorHandler.handleErrorContent(path, content, e);
            }
            throw e;
        }
    }

    private void handleFeedObject(JsonNode node, Consumer<FeedObject> forEachFeedObject) {
        String type = node.get("type").asText();
        Class<? extends FeedObject> feedObjectClass = typesToHandle.get(type);
        if (feedObjectClass == null) {
            return;
        }
        FeedObject feedObject = JacksonUtil.treeToValue(node, feedObjectClass);
        if (!feedObject.getType().equals(type)) {
            throw new RuntimeException("Illegal type " + type + " for " + feedObject);
        }
        forEachFeedObject.accept(feedObject);
    }

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
