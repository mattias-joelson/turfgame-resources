package org.joelson.turf.turfgame.util;

import com.fasterxml.jackson.databind.JsonNode;

import java.nio.file.Path;
import java.util.List;

public interface FeedContentErrorHandler {

    List<JsonNode> handleErrorContent(Path path, String content, RuntimeException e) throws RuntimeException;
}
