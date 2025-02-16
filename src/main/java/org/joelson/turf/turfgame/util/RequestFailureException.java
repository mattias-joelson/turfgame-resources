package org.joelson.turf.turfgame.util;

import java.io.IOException;
import java.io.Serial;
import java.util.Objects;

public class RequestFailureException extends IOException {

    @Serial
    private static final long serialVersionUID = 0;

    private final String requestMessage;

    RequestFailureException(String requestMessage, Throwable cause) {
        super(createMessage(Objects.requireNonNull(requestMessage)), cause);
        this.requestMessage = requestMessage;
    }

    private static String createMessage(String requestMessage) {
        if (requestMessage.length() > 40) {
            return requestMessage.substring(0, 40) + "...";
        } else {
            return requestMessage;
        }
    }

    public String getRequestMessage() {
        return requestMessage;
    }
}
