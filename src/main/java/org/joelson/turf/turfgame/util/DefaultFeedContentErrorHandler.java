package org.joelson.turf.turfgame.util;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class DefaultFeedContentErrorHandler implements FeedContentErrorHandler {

    private final Logger logger;

    public DefaultFeedContentErrorHandler() {
        this(LoggerFactory.getLogger(DefaultFeedContentErrorHandler.class));
    }

    public DefaultFeedContentErrorHandler(Logger logger) {
        this.logger = logger;
    }

    public List<JsonNode> handleErrorContent(Path path, String content, RuntimeException e) throws RuntimeException {
        Throwable cause = e.getCause();
        if (cause instanceof MismatchedInputException
                && content.startsWith("{\"errorMessage\":\"Only one request per second allowed\",\"errorCode\":")) {
            logger.warn("    Path {} contains error message - only one request per second allowed.", path);
            return Collections.emptyList();
        } else if (cause instanceof JsonParseException) {
            if (content.isEmpty()) {
                logger.warn("    Path {} is empty.", path);
                return Collections.emptyList();
            } else if (content.charAt(0) == 0 && allZeroes(content)) {
                logger.warn("    Path {} is contains only zeroes.", path);
                return Collections.emptyList();
            } else if (content.startsWith("<html>")) {
                if (content.contains("504 Gateway Time-out")) {
                    logger.warn("    Path {} contains HTML response - 504 Gateway Time-out", path);
                    return Collections.emptyList();
                }
            }
        }
        throw new RuntimeException(e);
    }

    private static boolean allZeroes(String s) {
        return s.chars().allMatch(ch -> ch == 0);
    }
}
