package org.joelson.turf.turfgame.apiv4;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.joelson.turf.util.JacksonUtil;
import org.joelson.turf.util.URLReader;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.List;

public final class Rounds {

    private static final String ROUNDS_REQUEST = "https://api.turfgame.com/v4/rounds";

    private Rounds() throws InstantiationException {
        throw new InstantiationException("Should not be instantiated!");
    }

    public static List<Round> readRounds() throws IOException {
        URLReader.Response response = URLReader.getTurfgameRequest(ROUNDS_REQUEST);
        if (response.status() != HttpURLConnection.HTTP_OK) {
            System.err.printf("Response status: %d, request: %s", response.status(), ROUNDS_REQUEST);
        }
        return fromJSON(response.body());
    }

    static List<Round> fromJSON(String s) throws RuntimeException {
        try {
            return Arrays.asList(JacksonUtil.readValue(s, Round[].class));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
