package org.joelson.turf.turfgame.util;

import org.joelson.turf.util.URLReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.zip.GZIPInputStream;

import static org.joelson.turf.util.URLReader.Response;

public final class TurfgameURLReader {

    private static final HttpClient httpClient = HttpClient.newHttpClient();

    private static final String ACCEPT_ENCODING = "Accept-Encoding";
    private static final String CONTENT_ENCODING = "Content-Encoding";
    private static final String CONTENT_ENCODING_GZIP = "gzip";

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_TYPE_APPLICATION_JSON_CHARSET_UTF_8 = "application/json;charset=utf-8";

    private static final String[] TURFGAME_GET_HEADERS = { ACCEPT_ENCODING, CONTENT_ENCODING_GZIP };
    private static final String[] TURFGAME_POST_HEADERS = {
            CONTENT_TYPE, CONTENT_TYPE_APPLICATION_JSON_CHARSET_UTF_8, ACCEPT_ENCODING, CONTENT_ENCODING_GZIP };

    private TurfgameURLReader() throws InstantiationException {
        throw new InstantiationException("Should not be instantiated!");
    }

    public static Response getTurfgameRequest(String request) throws IOException {
        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .headers(TURFGAME_GET_HEADERS)
                    .uri(new URI(request))
                    .GET().build();
            HttpResponse<InputStream> httpResponse = httpClient.send(httpRequest, BodyHandlers.ofInputStream());
            return new Response(httpResponse.statusCode(), getTurfgameBody(httpResponse));
        } catch (URISyntaxException | InterruptedException e) {
            throw new IOException(e);
        }
    }

    public static Response postTurfgameRequest(String request, String json) throws IOException {
        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .headers(TURFGAME_POST_HEADERS)
                    .uri(new URI(request))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<InputStream> httpResponse = httpClient.send(httpRequest, BodyHandlers.ofInputStream());
            return new Response(httpResponse.statusCode(), getTurfgameBody(httpResponse));
        } catch (URISyntaxException | InterruptedException e) {
            throw new IOException(e);
        }
    }

    private static String getTurfgameBody(HttpResponse<InputStream> httpResponse) throws IOException {
        String contentEncoding = httpResponse.headers().firstValue(CONTENT_ENCODING).orElse(null);
        String contentType = httpResponse.headers().firstValue(CONTENT_TYPE).orElse(null);
        if ((contentEncoding != null && !CONTENT_ENCODING_GZIP.equals(contentEncoding))
                || !CONTENT_TYPE_APPLICATION_JSON_CHARSET_UTF_8.equals(contentType)) {
            throw new UnsupportedEncodingException(
                    String.format("%s=%s, %s=%s", CONTENT_TYPE, contentType, CONTENT_ENCODING, contentEncoding));
        }
        if (contentEncoding == null) {
            return URLReader.readUTF8Stream(httpResponse.body());
        } else {
            return URLReader.readUTF8Stream(new GZIPInputStream(httpResponse.body()));
        }
    }
}
