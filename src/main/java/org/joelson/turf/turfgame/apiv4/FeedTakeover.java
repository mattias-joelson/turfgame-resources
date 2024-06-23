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
            @Nonnull @JsonProperty(value = "zone", required = true) Zone zone,
            @JsonProperty(value = "latitude", required = true) double latitude,
            @JsonProperty(value = "longitude", required = true) double longitude,
            @Nullable @JsonProperty("previousOwner") User previousOwner,
            @Nonnull @JsonProperty(value = "currentOwner", required = true) User currentOwner
    ) {
        super(type, time, zone, latitude, longitude, previousOwner, currentOwner, null);
    }
}
