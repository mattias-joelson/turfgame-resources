package org.joelson.turf.turfgame.apiv4;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nonnull;

public class FeedZone extends org.joelson.turf.turfgame.FeedZone<Zone> {

    @JsonCreator
    public FeedZone(
            @Nonnull @JsonProperty(value = "type", required = true) String type,
            @Nonnull @JsonProperty(value = "time", required = true) String time,
            @Nonnull @JsonProperty(value = "zone", required = true) Zone zone
    ) {
        super(type, time, zone);
    }
}
