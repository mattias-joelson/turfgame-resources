package org.joelson.turf.turfgame.apiv4;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FeedTakeover extends org.joelson.turf.turfgame.FeedTakeover<User, Zone> {

    @JsonCreator
    public FeedTakeover(
            @Nonnull @JsonProperty(value = "type", required = true) String type,
            @Nonnull @JsonProperty(value = "time", required = true) String time,
            @Nullable @JsonProperty("previousOwner") User previousOwner,
            @Nonnull @JsonProperty(value = "currentOwner", required = true) User currentOwner,
            @JsonProperty("longitude") double longitude,
            @JsonProperty("latitude") double latitude,
            @Nonnull @JsonProperty(value = "zone", required = true) Zone zone
    ) {
        super(type, time, null, previousOwner, currentOwner, longitude, latitude, zone);
    }
}
