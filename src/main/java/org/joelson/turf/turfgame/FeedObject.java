package org.joelson.turf.turfgame;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joelson.turf.util.StringUtil;

import javax.annotation.Nonnull;

public abstract class FeedObject {

    @Nonnull
    private final String time;

    @JsonCreator
    public FeedObject(
            @Nonnull @JsonProperty(value = "type", required = true) String type,
            @Nonnull @JsonProperty(value = "time", required = true) String time
    ) {
        this.time = StringUtil.requireNotNullAndNotEmpty(time);
    }

    public abstract String getType();

    public final String getTime() {
        return time;
    }

    protected final String innerToString() {
        return String.format("type=%s, time=%s", StringUtil.printable(getType()), StringUtil.printable(time));
    }
}
