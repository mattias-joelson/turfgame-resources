package org.joelson.mattias.turfgame.apiv4;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joelson.mattias.turfgame.util.StringUtil;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class ZoneFeed extends FeedObject {

    private final Zone zone;

    @JsonCreator
    public ZoneFeed(
            @Nonnull @JsonProperty("type") String type,
            @Nonnull @JsonProperty("time") String time,
            @Nonnull @JsonProperty("zone") Zone zone
    ) {
        super(type, time);
        this.zone = Objects.requireNonNull(zone);
        if (!getType().equals(type)) {
            throw new RuntimeException("Illegal type " + type);
        }
    }

    @Override
    public String getType() {
        return "zone";
    }

    public Zone getZone() {
        return zone;
    }

    @Override
    public String toString() {
        return "ZoneFeed{"
                + "type=" + StringUtil.printable(getType())
                + ", time=" + StringUtil.printable(getTime())
                + ", zone=" + zone
                + '}';
    }
}
