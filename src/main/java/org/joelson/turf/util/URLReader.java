package org.joelson.turf.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;
import java.util.zip.GZIPInputStream;

public final class URLReader {

    private static final HttpClient httpClient = HttpClient.newHttpClient();

    private static final String ACCEPT_ENCODING = "Accept-Encoding";
    private static final String CONTENT_ENCODING = "Content-Encoding";
    private static final String CONTENT_TYPE = "Content-Type";

    private static final String CONTENT_ENCODING_GZIP = "gzip";
    private static final String CONTENT_TYPE_APPLICATION_JSON_CHARSET_UTF_8 = "application/json;charset=utf-8";

    private static final String[] TURFGAME_GET_HEADERS = { ACCEPT_ENCODING, CONTENT_ENCODING_GZIP };
    private static final String[] TURFGAME_POST_HEADERS = {
            CONTENT_TYPE, CONTENT_TYPE_APPLICATION_JSON_CHARSET_UTF_8, ACCEPT_ENCODING, CONTENT_ENCODING_GZIP };

    private URLReader() throws InstantiationException {
        throw new InstantiationException("Should not be instantiated!");
    }

    public static String getRequest(String request) throws IOException {
        try {
            HttpRequest httpRequest = HttpRequest.newBuilder().uri(new URI(request)).GET().build();
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, BodyHandlers.ofString());
            return httpResponse.body();
        } catch (URISyntaxException | InterruptedException e) {
            throw new IOException(e);
        }
    }

    public static String getTurfgameRequest(String request) throws IOException {
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder().headers(TURFGAME_GET_HEADERS).uri(new URI(request));
            HttpRequest httpRequest = builder.GET().build();
            HttpResponse<InputStream> httpResponse = httpClient.send(httpRequest, BodyHandlers.ofInputStream());
            return getTurfgameBody(httpResponse);
        } catch (URISyntaxException | InterruptedException e) {
            throw new IOException(e);
        }
    }

    public static String postTurfgameRequest(String request, String json) throws IOException {
        try {
            HttpRequest httpRequest = HttpRequest.newBuilder().uri(new URI(request))
                    .headers(TURFGAME_POST_HEADERS).POST(BodyPublishers.ofString(json)).build();
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, BodyHandlers.ofString());
            return httpResponse.body();
        } catch (URISyntaxException | InterruptedException e) {
            throw new IOException(e);
        }
    }

    private static String getTurfgameBody(HttpResponse<InputStream> httpResponse) throws IOException {
        String contentEncoding = httpResponse.headers().firstValue(CONTENT_ENCODING).orElse(null);
        String contentType = httpResponse.headers().firstValue(CONTENT_TYPE).orElse(null);
        if (!CONTENT_ENCODING_GZIP.equals(contentEncoding) || !CONTENT_TYPE_APPLICATION_JSON_CHARSET_UTF_8.equals(
                contentType)) {
            throw new UnsupportedEncodingException(
                    String.format("%s=%s, %s=%s", CONTENT_TYPE, contentType, CONTENT_ENCODING, contentEncoding));
        }
        return readStream(new GZIPInputStream(httpResponse.body()));
    }

    static String readStream(InputStream inputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            StringBuilder builder = new StringBuilder();
            String line = reader.readLine();
            while (line != null) {
                builder.append(line).append('\n');
                line = reader.readLine();
            }
            return builder.toString();
        }
    }

    public static <R> R readProperties(File file, Function<String, R> function) throws IOException {
        try (FileInputStream input = new FileInputStream(file)) {
            return function.apply(URLReader.readStream(input));
        }
    }
}
