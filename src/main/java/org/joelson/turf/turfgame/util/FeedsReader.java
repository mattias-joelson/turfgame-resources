package org.joelson.turf.turfgame.util;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class FeedsReader {

    private final Map<String, Class<? extends FeedObject>> typesToHandle;
    private final boolean reversed;
    private final FeedContentErrorHandler errorHandler;

    public FeedsReader(Map<String, Class<? extends FeedObject>> typesToHandle, FeedContentErrorHandler errorHandler) {
        this(typesToHandle, errorHandler, true);
    }

    public FeedsReader(Map<String, Class<? extends FeedObject>> typesToHandle, FeedContentErrorHandler errorHandler,
            boolean reversed) {
        this.typesToHandle = Objects.requireNonNull(typesToHandle);
        this.errorHandler = Objects.requireNonNull(errorHandler);
        this.reversed = reversed;
    }

    private static String readFile(Path path, Consumer<Path> forEachPath) throws IOException {
        String content = Files.readString(path);
        forEachPath.accept(path);
        return content;
    }

    private static String getJsonNodeTime(JsonNode node) {
        JsonNode timeNode = node.get("time");
        if (timeNode == null) {
            throw new IllegalArgumentException("Node lacks attribute time: " + node);
        }
        return timeNode.asText();
    }

    public void handleFeedObjectPath(Path path, Consumer<Path> forEachPath, Consumer<FeedObject> forEachFeedObject)
            throws IOException {
        Comparator<Path> pathComparator = (reversed) ? new FeedsPathComparator().reversed() : new FeedsPathComparator();
        FilesUtil.forEachFile(path, true, pathComparator,
                p -> handleFeedObjectFile(p, forEachPath, forEachFeedObject));
    }

    private void handleFeedObjectFile(Path path, Consumer<Path> forEachPath, Consumer<FeedObject> forEachFeedObject) {
        String content = null;
        try {
            content = readFile(path, forEachPath);
            handleFeedObjects(content, forEachFeedObject);
        } catch (IOException e) {
            errorHandler.handleErrorContent(path, content, e);
        }
    }

    private void handleFeedObjects(String content, Consumer<FeedObject> forEachFeedObject)
            throws IOException {
        List<JsonNode> nodes = getJsonNodes(content);
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

    private List<JsonNode> getJsonNodes(String content) throws JsonProcessingException {
        List<JsonNode> nodes = Arrays.asList(JacksonUtil.readValue(content, JsonNode[].class));
        return (reversed) ? nodes : nodes.reversed();
    }

    private void handleFeedObject(JsonNode node, Consumer<FeedObject> forEachFeedObject)
            throws JsonProcessingException, ConflictingFeedTypeException {
        String type = node.get("type").asText();
        Class<? extends FeedObject> feedObjectClass = typesToHandle.get(type);
        if (feedObjectClass == null) {
            return;
        }
        FeedObject feedObject = JacksonUtil.treeToValue(node, feedObjectClass);
        if (!feedObject.getType().equals(type)) {
            throw new ConflictingFeedTypeException(feedObject, type);
        }
        forEachFeedObject.accept(feedObject);
    }
}
