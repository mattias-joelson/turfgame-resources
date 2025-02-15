package org.joelson.turf.turfgame.util;

import java.io.IOException;
import java.io.Serial;

public class UnknownContentEncodingTypeException extends IOException {

    @Serial
    private static final long serialVersionUID = 0;

    private final int statusCode;
    private final String contentType;
    private final String contentEncoding;
    private final String content;

    public UnknownContentEncodingTypeException(int statusCode, String contentType, String contentEncoding, String content) {
        super(createMessage(statusCode, contentType, contentEncoding, content));
        this.statusCode = statusCode;
        this.contentType = contentType;
        this.contentEncoding = contentEncoding;
        this.content = content;
    }

    public UnknownContentEncodingTypeException(int statusCode, String contentType, String contentEncoding, IOException cause) {
        super(createMessage(statusCode, contentType, contentEncoding, null), cause);
        this.statusCode = statusCode;
        this.contentType = contentType;
        this.contentEncoding = contentEncoding;
        this.content = null;
    }

    private static String createMessage(int status, String contentType, String contentEncoding, String content) {
        if (content == null) {
            return String.format("statusCode=%d, content-type=%s, content-encoding=%s content=<unavailable>",
                    status, contentType, contentEncoding);
        } else if (content.length() > 20) {
            return String.format("statusCode=%d, content-type=%s, content-encoding=%s, content=\"%s\"...",
                    status, contentType, contentEncoding, content.substring(0, 20));
        } else {
            return String.format("statusCode=%d, content-type=%s, content-encoding=%s, content=\"%s\"",
                    status, contentType, contentEncoding, content);
        }
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
