package org.joelson.turf.turfgame.apiv4;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joelson.turf.util.StringUtil;

// TODO fix
public final class Zone {

    private final String name;
    private final int id;
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
    private Zone(
            @JsonProperty("name") String name,
            @JsonProperty("id") int id,
            @JsonProperty("region") Region region,
            @JsonProperty("latitude") double latitude,
            @JsonProperty("longitude") double longitude,
            @JsonProperty("dateCreated") String dateCreated,
            @JsonProperty("takeoverPoints") int takeoverPoints,
            @JsonProperty("pointsPerHour") int pointsPerHour,
            @JsonProperty("totalTakeovers") int totalTakeovers,
            @JsonProperty("previousOwner") User previousOwner,
            @JsonProperty("currentOwner") User currentOwner,
            @JsonProperty("dateLastTaken") String dateLastTaken
    ) {
        this.name = name;
        this.id = id;
        this.region = region;
        this.latitude = latitude;
        this.longitude = longitude;
        this.dateCreated = dateCreated;
        this.takeoverPoints = takeoverPoints;
        this.pointsPerHour = pointsPerHour;
        this.totalTakeovers = totalTakeovers;
        this.previousOwner = previousOwner;
        this.currentOwner = currentOwner;
        this.dateLastTaken = dateLastTaken;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
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
        return "Zone{"
                + "name=" + StringUtil.printable(name)
                + ", id=" + id
                + ", region=" + region
                + ", latitude=" + latitude
                + ", longitude=" + longitude
                + ", dateCreated=" + StringUtil.printable(dateCreated)
                + ", takeoverPoints=" + takeoverPoints
                + ", pointsPerHour=" + pointsPerHour
                + ", totalTakeovers=" + totalTakeovers
                + ", previousOwner=" + previousOwner
                + ", currentOwner=" + currentOwner
                + ", dateLastTaken=" + StringUtil.printable(dateLastTaken)
                + '}';
    }
}
