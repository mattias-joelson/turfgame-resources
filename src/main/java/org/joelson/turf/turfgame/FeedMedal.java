package org.joelson.turf.turfgame;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joelson.turf.util.StringUtil;

import javax.annotation.Nonnull;
import java.util.Objects;

public abstract class FeedMedal<U extends User> extends FeedObject {

    private final U user;
    private final int medal;

    @JsonCreator
    public FeedMedal(
            @Nonnull @JsonProperty(value = "type", required = true) String type,
            @Nonnull @JsonProperty(value = "time", required = true) String time,
            @Nonnull @JsonProperty(value = "user", required = true) U user,
            @JsonProperty(value = "medal", required = true) int medal
    ) {
        super(type, time);
        this.user = Objects.requireNonNull(user);
        this.medal = medal;
    }

    @Override
    public String getType() {
        return "medal";
    }

    public U getUser() {
        return user;
    }

    public int getMedal() {
        return medal;
    }

    @Override
    public String toString() {
        return String.format("FeedMedal[%s, user=%s, medal=%d]", innerToString(), user, medal);
    }
}
