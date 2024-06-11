package org.joelson.turf.turfgame.apiv5;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joelson.turf.util.StringUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Zone implements org.joelson.turf.turfgame.Zone {

    private final String name;
    private final int id;
    private final Type type;
    private final Region region;
    private final double latitude;
    private final double longitude;
    private final String dateCreated;
    private final int takeoverPoints;
    private final int pointsPerHour;
    private final int totalTakeovers;

    @JsonCreator
    public Zone(
            @Nonnull @JsonProperty("name") String name,
            @JsonProperty("id") int id,
            @Nonnull @JsonProperty("type") Type type,
            @Nonnull @JsonProperty("region") Region region,
            @JsonProperty("latitude") double latitude,
            @JsonProperty("longitude") double longitude,
            @Nullable @JsonProperty("dateCreated") String dateCreated,
            @JsonProperty("takeoverPoints") int takeoverPoints,
            @JsonProperty("pointsPerHour") int pointsPerHour,
            @JsonProperty("totalTakeovers") int totalTakeovers
    ) {
        this.name = StringUtil.requireNotNullAndNotTrimmedEmpty(name);
        this.id = id;
        this.type = type;
        this.region = region;
        this.latitude = latitude;
        this.longitude = longitude;
        // this.dateCreated = StringUtil.requireNotNullAndNotTrimmedEmpty(dateCreated);
        this.dateCreated = StringUtil.requireNullOrNonEmpty(dateCreated);
        this.takeoverPoints = takeoverPoints;
        this.pointsPerHour = pointsPerHour;
        this.totalTakeovers = totalTakeovers;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public Type getType() {
        return type;
    }

    public Region getRegion() {
        return region;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public int getTakeoverPoints() {
        return takeoverPoints;
    }

    public int getPointsPerHour() {
        return pointsPerHour;
    }

    public int getTotalTakeovers() {
        return totalTakeovers;
    }
}
