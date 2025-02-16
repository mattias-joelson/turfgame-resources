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
import java.util.function.Predicate;

public class FeedsReader {

    private final Map<String, Class<? extends FeedObject>> typesToHandle;
    private final boolean filesReversed;
    private final boolean feedReversed;
    private final FeedContentErrorHandler errorHandler;

    public FeedsReader(Map<String, Class<? extends FeedObject>> typesToHandle, FeedContentErrorHandler errorHandler) {
        this(typesToHandle, errorHandler, false, true);
    }

    public FeedsReader(Map<String, Class<? extends FeedObject>> typesToHandle, FeedContentErrorHandler errorHandler,
            boolean filesReversed, boolean feedReversed) {
        this.typesToHandle = Objects.requireNonNull(typesToHandle);
        this.errorHandler = Objects.requireNonNull(errorHandler);
        this.filesReversed = filesReversed;
        this.feedReversed = feedReversed;
    }

    private static String getJsonNodeTime(JsonNode node) {
        JsonNode timeNode = node.get("time");
        if (timeNode == null) {
            throw new IllegalArgumentException("Node lacks attribute time: " + node);
        }
        return timeNode.asText();
    }

    public void handleFeedObjectPath(Path path, Predicate<Path> forEachPath, Consumer<FeedObject> forEachFeedObject)
            throws IOException {
        Comparator<Path> pathComparator = (filesReversed) ? new FeedsPathComparator().reversed() : new FeedsPathComparator();
        FilesUtil.forEachFile(path, true, pathComparator,
                p -> handleFeedObjectFile(p, forEachPath, forEachFeedObject));
    }

    private void handleFeedObjectFile(Path path, Predicate<Path> forEachPath, Consumer<FeedObject> forEachFeedObject) {
        if (!forEachPath.test(path)) {
            return;
        }
        String content = null;
        try {
            content = Files.readString(path);
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
                } else if (time.compareTo(nodeTime) <= 0) {
                    time = nodeTime;
                } else {
                    throw new IllegalArgumentException(
                            "Node with time " + nodeTime + " is not after " + time + ": " + node);
                }
                handleFeedObject(node, forEachFeedObject);
            }
        }
    }

    private List<JsonNode> getJsonNodes(String content) throws JsonProcessingException {
        List<JsonNode> nodes = Arrays.asList(JacksonUtil.readValue(content, JsonNode[].class));
        return (feedReversed) ? nodes.reversed() : nodes;
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
