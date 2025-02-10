package org.joelson.turf.turfgame.util;

import org.joelson.turf.turfgame.FeedObject;

import java.io.IOException;
import java.io.Serial;

public class ConflictingFeedTypeException extends IOException {

    @Serial
    private static final long serialVersionUID = 0;

    private final String type;
    private final String expectedType;

    public ConflictingFeedTypeException(FeedObject object, String expectedType) {
        super(createMessage(object, expectedType));
        this.type = object.getType();
        this.expectedType = expectedType;
    }

    private static String createMessage(FeedObject object, String expectedType) {
        return String.format("FeedObject with type %s does not have expected type %s", object.getType(), expectedType);
    }

    public String getType() {
        return type;
    }

    public String getExpectedType() {
        return expectedType;
    }
}
