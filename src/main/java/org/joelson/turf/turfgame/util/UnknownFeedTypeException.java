package org.joelson.turf.turfgame.util;

import java.io.IOException;
import java.io.Serial;

public class UnknownFeedTypeException extends IOException {

    @Serial
    private static final long serialVersionUID = 0;

    private final String type;

    public UnknownFeedTypeException(String type) {
        super("Unknown type " + type);
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
