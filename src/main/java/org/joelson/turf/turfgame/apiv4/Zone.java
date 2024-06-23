package org.joelson.turf.turfgame.apiv4;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joelson.turf.util.StringUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class Zone implements org.joelson.turf.turfgame.Zone {

    private final int id;
    private final String name;
    private final Region region;
    private final double latitude;
    private final double longitude;
    private final String dateCreated;
    private final int takeoverPoints;
    private final int pointsPerHour;
    private final int totalTakeovers;
    private final User previousOwner;
    private final User currentOwner;
    private final String dateLastTaken;

    @JsonCreator
    public Zone(
            @JsonProperty(value = "id", required = true) int id,
            @Nonnull @JsonProperty(value = "name", required = true) String name,
            @Nullable @JsonProperty("region") Region region,
            @JsonProperty(value = "latitude", required = true) double latitude,
            @JsonProperty(value = "longitude", required = true) double longitude,
            @Nonnull @JsonProperty(value = "dateCreated", required = true) String dateCreated,
            @JsonProperty(value = "takeoverPoints", required = true) int takeoverPoints,
            @JsonProperty(value = "pointsPerHour", required = true) int pointsPerHour,
            @JsonProperty(value = "totalTakeovers", required = true) int totalTakeovers,
            @Nullable @JsonProperty("previousOwner") User previousOwner,
            @Nullable @JsonProperty("currentOwner") User currentOwner,
            @Nullable @JsonProperty("dateLastTaken") String dateLastTaken
    ) {
        this.id = id;
        this.name = StringUtil.requireNotNullAndNotTrimmedEmpty(name);
        this.region = region;
        this.latitude = latitude;
        this.longitude = longitude;
        this.dateCreated = StringUtil.requireNotNullAndNotTrimmedEmpty(dateCreated);
        this.takeoverPoints = takeoverPoints;
        this.pointsPerHour = pointsPerHour;
        this.totalTakeovers = totalTakeovers;
        this.previousOwner = previousOwner;
        this.currentOwner = currentOwner;
        this.dateLastTaken = dateLastTaken;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
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

    public User getPreviousOwner() {
        return previousOwner;
    }

    public User getCurrentOwner() {
        return currentOwner;
    }

    public String getDateLastTaken() {
        return dateLastTaken;
    }

    @Override
    public String toString() {
        return String.format(
                "Zone[id=%d, name=%s, region=%s, latitude=%f, longitude=%f, dateCreated=%s, takeoverPoints=%d, "
                        + "pointsPerHour=%d, totalTakeovers=%d%s%s%s",
                id, StringUtil.printable(name), region, latitude, longitude, StringUtil.printable(dateCreated),
                takeoverPoints, pointsPerHour, totalTakeovers, StringUtil.printable(previousOwner, ", previousOwner="),
                StringUtil.printable(currentOwner, ", currentOwner="),
                StringUtil.printable(dateLastTaken, ", dateLastTaken="));
    }
}
