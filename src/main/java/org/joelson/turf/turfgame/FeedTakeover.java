package org.joelson.mattias.turfgame.apiv4;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joelson.mattias.turfgame.util.StringUtil;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class TakeoverFeed extends FeedObject {

    private final Zone zone;

    private final double latitude;
    private final double longitude;
    private final User currentOwner;

    @JsonCreator
    public TakeoverFeed(
            @Nonnull @JsonProperty("type") String type,
            @Nonnull @JsonProperty("time") String time,
            @Nonnull @JsonProperty("zone") Zone zone,
            @JsonProperty("latitude") double latitude,
            @JsonProperty("longitude") double longitude,
            @Nonnull @JsonProperty("currentOwner") User currentOwner) {
        super(type, time);
        this.zone = Objects.requireNonNull(zone);
        this.latitude = latitude;
        this.longitude = longitude;
        this.currentOwner = Objects.requireNonNull(currentOwner);
        if (!getType().equals(type)) {
            throw new RuntimeException("Illegal type " + type);
        }
    }

    @Override
    public String getType() {
        return "takeover";
    }

    public Zone getZone() {
        return zone;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public User getCurrentOwner() {
        return currentOwner;
    }

    @Override
    public String toString() {
        return "TakeoverFeed{"
                + "type=" + StringUtil.printable(getType())
                + ", time=" + StringUtil.printable(getTime())
                + ", zone=" + zone
                + ", latitude=" + latitude
                + ", longitude=" + longitude
                + ", currentOwner=" + currentOwner
                + '}';
    }
}
