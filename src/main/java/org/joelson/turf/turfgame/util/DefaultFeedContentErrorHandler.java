package org.joelson.turf.turfgame.util;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipException;

public class DefaultFeedContentErrorHandler implements FeedContentErrorHandler {

    private final List<Path> errorPaths = new ArrayList<>();

    public void handleErrorContent(Path path, String content, IOException e) {
        errorPaths.add(path);
        if (e instanceof MismatchedInputException
                && content.startsWith("{\"errorMessage\":\"Only one request per second allowed\",\"errorCode\":")) {
            message("Path %s contains error message - only one request per second allowed.", path);
            return;
        } else if (e instanceof JsonParseException) {
            if (content.isEmpty()) {
                message("Path %s is empty.", path);
                return;
            } else if (content.charAt(0) == 0 && allZeroes(content)) {
                message("Path %s contains only zeroes.", path);
                return;
            } else if (isHTML(content)) {
                if (content.contains("504 Gateway Time-out")) {
                    message("Path %s contains HTML response - 504 Gateway Time-out", path);
                    return;
                } else if (content.contains("Status 500") && content.contains("Internal Server Error")) {
                    message("Path %s contains HTML response - 500 Internal Server Error", path);
                    return;
                } else {
                    message("Path %s probably contains HTML - %s...", path, content.substring(0, 40));
                    return;
                }
            }
        } else if (e instanceof ConflictingFeedTypeException cfte) {
            message("FeedObject of type %s when expecting type %s.", cfte.getType(), cfte.getExpectedType());
            return;
        } else if (e instanceof UnknownFeedTypeException ufte) {
            message("FeedObject of unknown type %s.", ufte.getType());
            return;
        } else if (e instanceof NoFeedTypeException)  {
            message("JSON node lacking type.");
            return;
        } else if (e instanceof MalformedInputException) {
            message("MalformedInputException - Unable to read path %s.", path);
            return;
        } else if (e instanceof ZipException) {
            message("ZipException - Unable to read path %s.", path);
            return;
        }
        if (content == null) {
            message("*** Unhandled exception type %s for path %s lacking content.",
                    e.getClass().getName(), path);
        } else {
            String partOfContent = content.substring(0, 20);
            message("*** Unhandled exception type %s for path %s having content starting with \"%s\"",
                    e.getClass().getName(), path, partOfContent);
        }
    }

    private void message(String format, Object... args) {
        message(String.format(format, args));
    }

    protected void message(String msg) {
        System.err.println(msg);
    }

    public List<Path> getErrorPaths() {
        return Collections.unmodifiableList(errorPaths);
    }

    public void messageErrorPaths(int maxPaths) {
        if (!errorPaths.isEmpty()) {
            message("Error in paths: %d", errorPaths.size());
            int max = Math.min(maxPaths, errorPaths.size());
            errorPaths.stream().limit(max).forEach(path -> message("    %s", path));
            if (errorPaths.size() > max) {
                message("    ...");
            }
        }
    }

    private static boolean allZeroes(String s) {
        return s.chars().allMatch(ch -> ch == 0);
    }

    private static boolean isHTML(String content) {
        String contentPart = content.substring(0, 15).toLowerCase();
        return contentPart.startsWith("<!doctype html>") || contentPart.startsWith("<html>");
    }
}
