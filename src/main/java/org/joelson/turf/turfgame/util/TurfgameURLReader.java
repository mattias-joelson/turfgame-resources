package org.joelson.turf.turfgame.util;

import org.joelson.turf.util.URLReader;

import java.io.IOException;
import java.io.InputStream;
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

    public static final int HTTP_TOO_MANY_REQUESTS = 429;
    public static final String ERROR_MESSAGE_TOO_MANY_REQUESTS =
            "{\"errorMessage\":\"Only one request per second allowed\",\"errorCode\":195887108}";

    private TurfgameURLReader() throws InstantiationException {
        throw new InstantiationException("Should not be instantiated!");
    }

    public static Response getTurfgameRequest(String request) throws RequestFailureException, RequestContentException {
        String requestMessage = "GET " + request;
        HttpResponse<InputStream> httpResponse;
        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .headers(TURFGAME_GET_HEADERS)
                    .uri(new URI(request))
                    .GET().build();
            httpResponse = httpClient.send(httpRequest, BodyHandlers.ofInputStream());
        } catch (URISyntaxException | InterruptedException | IOException e) {
            throw new RequestFailureException(requestMessage, e);
        }
        return getTurfgameBody(requestMessage, httpResponse);
    }

    public static Response postTurfgameRequest(String request, String json)
            throws RequestFailureException, RequestContentException {
        String requestMessage = "POST " + request + " " + json;
        HttpResponse<InputStream> httpResponse;
        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .headers(TURFGAME_POST_HEADERS)
                    .uri(new URI(request))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            httpResponse = httpClient.send(httpRequest, BodyHandlers.ofInputStream());
        } catch (URISyntaxException | InterruptedException | IOException e) {
            throw new RequestFailureException(requestMessage, e);
        }
        return getTurfgameBody(requestMessage, httpResponse);
    }

    private static Response getTurfgameBody(String request, HttpResponse<InputStream> httpResponse)
            throws RequestContentException {
        String contentType = httpResponse.headers().firstValue(CONTENT_TYPE).orElse(null);
        String contentEncoding = httpResponse.headers().firstValue(CONTENT_ENCODING).orElse(null);
        String content;
        try {
            if (CONTENT_ENCODING_GZIP.equals(contentEncoding)) {
                content = URLReader.readUTF8Stream(new GZIPInputStream(httpResponse.body()));
            } else {
                content = URLReader.readUTF8Stream(httpResponse.body());
            }
        } catch (IOException e) {
            throw new RequestContentException(request, httpResponse.statusCode(), contentType, contentEncoding, e);
        }
        if (httpResponse.statusCode() == HTTP_TOO_MANY_REQUESTS && ERROR_MESSAGE_TOO_MANY_REQUESTS.equals(content)) {
            return new Response(HTTP_TOO_MANY_REQUESTS, ERROR_MESSAGE_TOO_MANY_REQUESTS);
        }
        if ((contentEncoding != null && !CONTENT_ENCODING_GZIP.equals(contentEncoding))
                || !CONTENT_TYPE_APPLICATION_JSON_CHARSET_UTF_8.equals(contentType)) {
            throw new RequestContentException(request, httpResponse.statusCode(), contentType, contentEncoding,
                    content);
        }
        return new Response(httpResponse.statusCode(), content);
    }
}
