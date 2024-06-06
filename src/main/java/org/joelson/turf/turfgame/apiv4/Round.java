package org.joelson.turf.turfgame.apiv4;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

// TODO fix
public class Round {

    private final String name;
    private final String start;

    @JsonCreator
    private Round(
            @JsonProperty("name") String name,
            @JsonProperty("start") String start) {
        this.name = name;
        this.start = start;
    }

    public String getName() {
        return name;
    }

    public String getStart() {
        return start;
    }

    @Override
    public String toString() {
        return "Round{" +
                "name='" + name + '\'' +
                ", start='" + start + '\'' +
                '}';
    }
}
