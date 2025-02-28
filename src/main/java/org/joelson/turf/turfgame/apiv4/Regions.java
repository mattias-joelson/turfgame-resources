package org.joelson.turf.turfgame.apiv4;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.joelson.turf.turfgame.util.TurfgameURLReader;
import org.joelson.turf.util.JacksonUtil;
import org.joelson.turf.util.URLReader.Response;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public final class Regions {

    private static final String REGIONS_REQUEST = "https://api.turfgame.com/v4/regions";
    private static final String DEFAULT_REGIONS_FILENAME = "regions-all.json";

    private Regions() throws InstantiationException {
        throw new InstantiationException("Should not be instantiated!");
    }

    public static List<Region> readRegions() throws IOException {
        return fromJSON(getAllRegionsJSON());
    }

    private static String getAllRegionsJSON() throws IOException {
        Response response = TurfgameURLReader.getTurfgameRequest(REGIONS_REQUEST);
        if (response.statusCode() != HttpURLConnection.HTTP_OK) {
            System.err.printf("Response statusCode: %d, request: %s%n", response.statusCode(), REGIONS_REQUEST);
        }
        return response.content();
    }

    public static void main(String[] args) throws IOException {
        Files.writeString(Path.of(DEFAULT_REGIONS_FILENAME), getAllRegionsJSON(), StandardCharsets.UTF_8);
    }

    static List<Region> fromJSON(String s) throws RuntimeException {
        try {
            return Arrays.asList(JacksonUtil.readValue(s, Region[].class));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
