package org.joelson.turf.turfgame;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nonnull;
import java.util.Objects;

public abstract class FeedZone<Z extends Zone> extends FeedObject {

    private final Z zone;

    @JsonCreator
    public FeedZone(
            @Nonnull @JsonProperty(value = "type", required = true) String type,
            @Nonnull @JsonProperty(value = "time", required = true) String time,
            @Nonnull @JsonProperty(value = "zone", required = true) Z zone
    ) {
        super(type, time);
        this.zone = Objects.requireNonNull(zone);
    }

    @Override
    public String getType() {
        return "zone";
    }

    public Z getZone() {
        return zone;
    }

    @Override
    public String toString() {
        return String.format("FeedZone[%s, zone=%s]", innerToString(), zone);
    }
}
