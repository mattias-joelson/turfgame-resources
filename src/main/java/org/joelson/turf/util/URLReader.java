package org.joelson.turf.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

public final class URLReader {

    private URLReader() throws InstantiationException {
        throw new InstantiationException("Should not be instantiated!");
    }

    public static String getRequest(String request) throws IOException {
        try {
            HttpRequest httpRequest = HttpRequest.newBuilder().uri(new URI(request)).GET().build();
            HttpResponse<String> httpResponse = HttpClient.newBuilder().build()
                    .send(httpRequest, BodyHandlers.ofString());
            return httpResponse.body();
        } catch (URISyntaxException | InterruptedException e) {
            throw new IOException(e);
        }
    }

    public static String postRequest(String request, String json) throws IOException {
        try {
            HttpRequest httpRequest = HttpRequest.newBuilder().uri(new URI(request))
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> httpResponse = HttpClient.newBuilder().build()
                    .send(httpRequest, BodyHandlers.ofString());
            return httpResponse.body();
        } catch (URISyntaxException | InterruptedException e) {
            throw new IOException(e);
        }
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
