package org.joelson.turf.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.stream.Collectors;

public final class URLReader {

    public record Response(int statusCode, String content) {

        public Response {
            if (statusCode < 100 || statusCode > 599) {
                throw new IllegalArgumentException("Invalid statusCode " + statusCode);
            }
            Objects.requireNonNull(content);
        }
    }

    private static final HttpClient httpClient = HttpClient.newHttpClient();

    private URLReader() throws InstantiationException {
        throw new InstantiationException("Should not be instantiated!");
    }

    public static Response getRequest(String request) throws IOException {
        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(new URI(request))
                    .GET().build();
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, BodyHandlers.ofString());
            return new Response(httpResponse.statusCode(), httpResponse.body());
        } catch (URISyntaxException | InterruptedException e) {
            throw new IOException(e);
        }
    }

    public static String getRequestAndPrintStatusCode(String request) throws IOException {
        Response response = getRequest(request);
        if (response.statusCode() != HttpURLConnection.HTTP_OK) {
            System.err.printf("Response statusCode: %d, request: %s%n", response.statusCode(), request);
        }
        return response.content();
    }

    public static String readUTF8Stream(InputStream inputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }
}
