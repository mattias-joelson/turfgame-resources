package org.joelson.turf.turfgame.apiv5;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FeedChat extends org.joelson.turf.turfgame.FeedChat<Region, User> {

    public FeedChat(
            @Nonnull @JsonProperty(value = "type", required = true) String type,
            @Nonnull @JsonProperty(value = "time", required = true) String time,
            @Nullable @JsonProperty("country") String country,
            @Nullable @JsonProperty("region") Region region,
            @Nonnull @JsonProperty(value = "sender", required = true) User sender,
            @Nonnull @JsonProperty(value = "message", required = true) String message
    ) {
        super(type, time, country, region, sender, message);
    }
}
