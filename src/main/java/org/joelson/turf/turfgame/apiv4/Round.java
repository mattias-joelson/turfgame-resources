package org.joelson.turf.turfgame.apiv4;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joelson.turf.util.StringUtil;

import javax.annotation.Nonnull;

public class Round {

    private final String name;
    private final String start;

    @JsonCreator
    private Round(
            @Nonnull @JsonProperty(value = "name", required = true) String name,
            @Nonnull @JsonProperty(value = "start", required = true) String start
    ) {
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
        return String.format("Round[name=%s, start=%s]", StringUtil.printable(name), StringUtil.printable(start));
    }
}
