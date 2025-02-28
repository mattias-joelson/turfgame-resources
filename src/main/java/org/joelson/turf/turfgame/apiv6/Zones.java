package org.joelson.turf.turfgame.apiv6;

import org.joelson.turf.turfgame.util.TurfgameURLReader;
import org.joelson.turf.util.URLReader.Response;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class Zones {

    private static final String ALL_ZONES_REQUEST = "https://api.turfgame.com/unstable/zones/all";
    private static final String DEFAULT_ZONES_FILENAME = "zones-all.v6.json";

    private Zones() throws InstantiationException {
        throw new InstantiationException("Should not be instantiated!");
    }

    private static String getAllZonesJSON() throws IOException {
        Response response = TurfgameURLReader.getTurfgameRequest(ALL_ZONES_REQUEST);
        if (response.statusCode() != HttpURLConnection.HTTP_OK) {
            System.err.printf("Response statusCode: %d, request: %s%n", response.statusCode(), ALL_ZONES_REQUEST);
        }
        return response.content();
    }

    public static void main(String[] args) throws IOException {
        Files.writeString(Path.of(DEFAULT_ZONES_FILENAME), getAllZonesJSON(), StandardCharsets.UTF_8);
    }
}
