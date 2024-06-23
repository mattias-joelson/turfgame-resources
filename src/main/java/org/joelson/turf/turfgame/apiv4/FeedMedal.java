package org.joelson.turf.turfgame.apiv4;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nonnull;

public class FeedMedal extends org.joelson.turf.turfgame.FeedMedal<User> {

    public FeedMedal(
            @Nonnull @JsonProperty(value = "type", required = true) String type,
            @Nonnull @JsonProperty(value = "time", required = true) String time,
            @Nonnull @JsonProperty(value = "user", required = true) User user,
            @JsonProperty(value = "medal", required = true) int medal
    ) {
        super(type, time, user, medal);
    }
}
