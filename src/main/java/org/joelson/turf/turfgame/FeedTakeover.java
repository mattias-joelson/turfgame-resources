package org.joelson.turf.turfgame;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Objects;

public abstract class FeedTakeover<U extends User, Z extends Zone> extends FeedObject {

    private final U[] assists;
    private final U previousOwner;
    private final U currentOwner;
    private final double longitude;
    private final double latitude;
    private final Z zone;

    @JsonCreator
    public FeedTakeover(
            @Nonnull @JsonProperty(value = "type", required = true) String type,
            @Nonnull @JsonProperty(value = "time", required = true) String time,
            @Nullable @JsonProperty("assists") U[] assists,
            @Nullable @JsonProperty("previousOwner") U previousOwner,
            @Nonnull @JsonProperty(value = "currentOwner", required = true) U currentOwner,
            @JsonProperty(value = "longitude", required = true) double longitude,
            @JsonProperty(value = "latitude", required = true) double latitude,
            @Nonnull @JsonProperty(value = "zone", required = true) Z zone
    ) {
        super(type, time);
        this.assists = assists;
        this.previousOwner = previousOwner;
        this.currentOwner = Objects.requireNonNull(currentOwner);
        this.longitude = longitude;
        this.latitude = latitude;
        this.zone = Objects.requireNonNull(zone);
    }

    @Override
    public String getType() {
        return "takeover";
    }

    public U[] getAssists() {
        return assists;
    }

    public U getPreviousOwner() {
        return previousOwner;
    }

    public U getCurrentOwner() {
        return currentOwner;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public Z getZone() {
        return zone;
    }

    @Override
    public String toString() {
        return String.format(
                "TakeoverFeed[%s, assists=%s, previousOwner=%s, currentOwner=%s, longitude=%f, latitude=%f, zone=%s]",
                innerToString(), Arrays.toString(assists), previousOwner, currentOwner, longitude, latitude, zone);
    }
}
