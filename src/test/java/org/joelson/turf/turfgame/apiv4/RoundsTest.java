package org.joelson.turf.turfgame.apiv4;

import org.joelson.turf.util.URLReaderTest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RoundsTest {

    private static List<Round> getRounds() throws Exception {
        return URLReaderTest.readProperties("rounds.json", Rounds::fromJSON);
    }

    @Test
    public void parseRounds() throws Exception {
        List<Round> rounds = getRounds();
        assertEquals(7, rounds.size());
    }
}
