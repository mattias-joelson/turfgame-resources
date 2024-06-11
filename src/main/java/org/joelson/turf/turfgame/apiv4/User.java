package org.joelson.turf.turfgame.apiv4;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joelson.turf.util.StringUtil;

import java.util.Arrays;

public class User implements org.joelson.turf.turfgame.User {

    private final String name;
    private final int id;
    private final String country;
    private final Region region;
    private final int uniqueZonesTaken;
    private final int pointsPerHour;
    private final int rank;
    private final int totalPoints;
    private final int taken;
    private final int points;
    private final int place;
    private final int blocktime;
    private final int[] medals;
    private final int[] zones;

    @JsonCreator
    public User(
            @JsonProperty("name") String name,
            @JsonProperty("id") int id,
            @JsonProperty("country") String country,
            @JsonProperty("region") Region region,
            @JsonProperty("uniqueZonesTaken") int uniqueZonesTaken,
            @JsonProperty("pointsPerHour") int pointsPerHour,
            @JsonProperty("rank") int rank,
            @JsonProperty("totalPoints") int totalPoints,
            @JsonProperty("taken") int taken,
            @JsonProperty("points") int points,
            @JsonProperty("place") int place,
            @JsonProperty("blocktime") int blocktime,
            @JsonProperty("medals") int[] medals,
            @JsonProperty("zones") int[] zones
    ) {
        this.name = name;
        this.id = id;
        this.country = country;
        this.region = region;
        this.uniqueZonesTaken = uniqueZonesTaken;
        this.pointsPerHour = pointsPerHour;
        this.rank = rank;
        this.totalPoints = totalPoints;
        this.taken = taken;
        this.points = points;
        this.place = place;
        this.blocktime = blocktime;
        this.medals = medals;
        this.zones = zones;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getCountry() {
        return country;
    }

    public Region getRegion() {
        return region;
    }

    public int getUniqueZonesTaken() {
        return uniqueZonesTaken;
    }

    public int getPointsPerHour() {
        return pointsPerHour;
    }

    public int getRank() {
        return rank;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public int getTaken() {
        return taken;
    }

    public int getPoints() {
        return points;
    }

    public int getPlace() {
        return place;
    }

    public int getBlocktime() {
        return blocktime;
    }

    public int[] getMedals() {
        return medals;
    }

    public int[] getZones() {
        return zones;
    }

    @Override
    public String toString() {
        return "User{"
                + "name=" + StringUtil.printable(name)
                + ", id=" + id
                + ", country=" + StringUtil.printable(country)
                + ", region=" + region
                + ", uniqueZonesTaken=" + uniqueZonesTaken
                + ", pointsPerHour=" + pointsPerHour
                + ", rank=" + rank
                + ", totalPoints=" + totalPoints
                + ", taken=" + taken
                + ", points=" + points
                + ", place=" + place
                + ", blocktime=" + blocktime
                + ", medals=" + Arrays.toString(medals)
                + ", zones=" + Arrays.toString(zones)
                + '}';
    }
}
