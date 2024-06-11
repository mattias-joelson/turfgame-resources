package org.joelson.turf.turfgame;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joelson.turf.util.StringUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Objects;

public abstract class FeedTakeover<U extends User, Z extends Zone> extends FeedObject {

    private final Z zone;
    private final double latitude;
    private final double longitude;
    private final U previousOwner;
    private final U currentOwner;
    private final U[] assists;

    @JsonCreator
    public FeedTakeover(
            @Nonnull @JsonProperty(value = "type", required = true) String type,
            @Nonnull @JsonProperty(value = "time", required = true) String time,
            @Nonnull @JsonProperty(value = "zone", required = true) Z zone,
            @JsonProperty(value = "latitude", required = true) double latitude,
            @JsonProperty(value = "longitude", required = true) double longitude,
            @Nullable @JsonProperty("previousOwner") U previousOwner,
            @Nonnull @JsonProperty(value = "currentOwner", required = true) U currentOwner,
            @Nullable @JsonProperty("assists") U[] assists
    ) {
        super(type, time);
        this.zone = Objects.requireNonNull(zone);
        this.latitude = latitude;
        this.longitude = longitude;
        this.currentOwner = Objects.requireNonNull(currentOwner);
        this.previousOwner = previousOwner;
        this.assists = assists;
    }

    @Override
    public String getType() {
        return "takeover";
    }

    public Z getZone() {
        return zone;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public U getPreviousOwner() {
        return previousOwner;
    }

    public U getCurrentOwner() {
        return currentOwner;
    }

    public U[] getAssists() {
        return assists;
    }

    @Override
    public String toString() {
        return String.format("TakeoverFeed[%s, zone=%s, latitude=%f, longitude=%f%s, currentOwner=%s%s]",
                innerToString(), zone, latitude, longitude, StringUtil.printable(previousOwner, ", previousOwner="),
                currentOwner, StringUtil.printable(assists, ", assists="));
    }
}
