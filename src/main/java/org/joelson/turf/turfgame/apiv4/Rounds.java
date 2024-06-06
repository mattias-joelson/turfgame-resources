package org.joelson.turf.turfgame.apiv4;

import org.joelson.turf.util.JacksonUtil;
import org.joelson.turf.util.URLReader;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public final class Rounds {

    private static final String ROUNDS_REQUEST = "https://api.turfgame.com/v4/rounds"; // NON-NLS

    private Rounds() throws InstantiationException {
        throw new InstantiationException("Should not be instantiated!");
    }

    public static List<Round> readRounds() throws IOException {
        return fromJSON(URLReader.getRequest(ROUNDS_REQUEST));
    }

    static List<Round> fromJSON(String s) {
        return Arrays.asList(JacksonUtil.readValue(s, Round[].class));
    }
}
