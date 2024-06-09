package org.joelson.mattias.turfgame.apiv4;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joelson.mattias.turfgame.util.StringUtil;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class MedalFeed extends FeedObject {

    private final User user;
    private final int medal;

    @JsonCreator
    public MedalFeed(
            @Nonnull @JsonProperty("type") String type,
            @Nonnull @JsonProperty("time") String time,
            @Nonnull @JsonProperty("user") User user,
            @JsonProperty("medal") int medal
    ) {
        super(type, time);
        this.user = Objects.requireNonNull(user);
        this.medal = medal;
        if (!getType().equals(type)) {
            throw new RuntimeException("Illegal type " + type);
        }
    }

    @Override
    public String getType() {
        return "medal";
    }

    public int getMedal() {
        return medal;
    }

    @Override
    public String toString() {
        return "MedalFeed{"
                + "type=" + StringUtil.printable(getType())
                + ", time=" + StringUtil.printable(getTime())
                + ", user=" + user
                + ", medal=" + medal
                + '}';
    }
}
