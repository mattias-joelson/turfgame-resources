package org.joelson.mattias.turfgame.apiv4;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
//import com.sun.istack.Nullable;
import org.joelson.mattias.turfgame.util.StringUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.Objects;

public final class ChatFeed extends FeedObject {

    @Nullable
    private final String country;
    @Nullable
    private final Region region;
    @Nonnull
    private final User sender;
    @Nonnull
    private final String message;

    @JsonCreator
    public ChatFeed(
            @Nonnull @JsonProperty("type") String type,
            @Nonnull @JsonProperty("time") String time,
            @Nullable @JsonProperty("country") String country,
            @Nullable @JsonProperty("region") Region region,
            @Nonnull @JsonProperty("sender") User sender,
            @Nonnull @JsonProperty("message") String message
    ) {
        super(type, time);
        this.country = country;
        this.region = region;
        this.sender = Objects.requireNonNull(sender);
        this.message = StringUtil.requireNotNullAndNotEmpty(message);
        if (!getType().equals(type)) {
            throw new RuntimeException("Illegal type " + type);
        }
    }

    @Override
    public String getType() {
        return "chat";
    }

    @Nullable
    public String getCountry() {
        return country;
    }

    public Region getRegion() {
        return region;
    }

    public User getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "ChatFeed{"
                + "type=" + StringUtil.printable(getType())
                + ", time=" + StringUtil.printable(getTime())
                + ", country=" + StringUtil.printable(message)
                + ", region=" + region
                + ", sender=" + sender
                + ", message=" + StringUtil.printable(message)
                + '}';
    }
}
