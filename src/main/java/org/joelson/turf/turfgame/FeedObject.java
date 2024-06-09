package org.joelson.mattias.turfgame.apiv4;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joelson.mattias.turfgame.util.StringUtil;

import javax.annotation.Nonnull;

public abstract class FeedObject {

    @Nonnull
    private final String time;

    @JsonCreator
    public FeedObject(
            @Nonnull @JsonProperty("type") String type,
            @Nonnull @JsonProperty("time") String time) {
        this.time = StringUtil.requireNotNullAndNotEmpty(time);
    }

    public abstract String getType();

    public final String getTime() {
        return time;
    }
}
