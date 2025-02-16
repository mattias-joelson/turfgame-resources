package org.joelson.turf.turfgame.util;

import java.io.IOException;
import java.io.Serial;

public class RequestContentException extends IOException {

    @Serial
    private static final long serialVersionUID = 0;

    private final String requestMessage;
    private final int statusCode;
    private final String contentType;
    private final String contentEncoding;
    private final String content;

    public RequestContentException(
            String requestMessage, int statusCode, String contentType, String contentEncoding, String content) {
        super(createMessage(statusCode, createContentTypePart(contentType), createContentEncodingPart(contentEncoding),
                content));
        this.requestMessage = requestMessage;
        this.statusCode = statusCode;
        this.contentType = contentType;
        this.contentEncoding = contentEncoding;
        this.content = content;
    }

    public RequestContentException(
            String requestMessage, int statusCode, String contentType, String contentEncoding, Throwable cause) {
        super(createMessage(statusCode, createContentTypePart(contentType), createContentEncodingPart(contentEncoding),
                null), cause);
        this.requestMessage = requestMessage;
        this.statusCode = statusCode;
        this.contentType = contentType;
        this.contentEncoding = contentEncoding;
        this.content = null;
    }

    private static String createMessage(
            int statusCode, String contentTypePart, String contentEncodingPart, String content) {
        if (content == null) {
            return String.format("statusCode=%d,%s%s content=<unavailable>",
                    statusCode, contentTypePart, contentEncodingPart);
        } else if (content.length() > 20) {
            return String.format("statusCode=%d,%s%s content=\"%s\"...",
                    statusCode, contentTypePart, contentEncodingPart, content.substring(0, 20));
        } else {
            return String.format("statusCode=%d,%s%s content=\"%s\"",
                    statusCode, contentTypePart, contentEncodingPart, content);
        }
    }

    private static String createContentTypePart(String contentType) {
        if (contentType != null) {
            return String.format(" content-type=%s,", contentType);
        } else {
            return "";
        }
    }

    private static String createContentEncodingPart(String contentEncoding) {
        if (contentEncoding != null) {
            return String.format(" content-encoding=%s,", contentEncoding);
        } else {
            return "";
        }
    }

    public String getRequestMessage() {
        return requestMessage;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getContentType() {
        return contentType;
    }

    public String getContentEncoding() {
        return contentEncoding;
    }

    public String getContent() {
        return content;
    }
}
