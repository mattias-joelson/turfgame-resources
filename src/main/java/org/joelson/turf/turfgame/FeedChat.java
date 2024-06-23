package org.joelson.turf.turfgame;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joelson.turf.util.StringUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public abstract class FeedChat<R extends Region, U extends User> extends FeedObject {

    @Nullable
    private final String country;
    @Nullable
    private final R region;
    @Nonnull
    private final U sender;
    @Nonnull
    private final String message;

    @JsonCreator
    public FeedChat(
            @Nonnull @JsonProperty(value = "type", required = true) String type,
            @Nonnull @JsonProperty(value = "time", required = true) String time,
            @Nullable @JsonProperty("country") String country,
            @Nullable @JsonProperty("region") R region,
            @Nonnull @JsonProperty(value = "sender", required = true) U sender,
            @Nonnull @JsonProperty(value = "message", required = true) String message
    ) {
        super(type, time);
        this.country = country;
        this.region = region;
        this.sender = Objects.requireNonNull(sender);
        this.message = StringUtil.requireNotNullAndNotEmpty(message);
    }

    @Override
    public String getType() {
        return "chat";
    }

    @Nullable
    public String getCountry() {
        return country;
    }

    public R getRegion() {
        return region;
    }

    public U getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return String.format("FeedChat[%s%s%s, sender=%s, message=%s]", innerToString(),
                StringUtil.printable(country, ", country="), StringUtil.printable(region, ", region="), sender,
                StringUtil.printable(message));
    }
}
