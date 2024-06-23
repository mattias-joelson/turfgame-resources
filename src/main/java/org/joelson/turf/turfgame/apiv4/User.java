package org.joelson.turf.turfgame.apiv4;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joelson.turf.util.StringUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;

public class User implements org.joelson.turf.turfgame.User {

    private final int id;
    private final String name;
    private final String country;
    private final Region region;
    private final int points;
    private final int place;
    private final int[] zoneIds;
    private final int pointsPerHour;
    private final int rank;
    private final int[] medals;
    private final int totalPoints;
    private final int blocktime;
    private final int taken;
    private final int uniqueZonesTaken;

    @JsonCreator
    public User(
            @JsonProperty(value = "id", required = true) int id,
            @Nonnull @JsonProperty(value = "name", required = true) String name,
            @Nullable @JsonProperty("country") String country,
            @Nullable @JsonProperty("region") Region region,
            @JsonProperty("points") int points,
            @JsonProperty("place") int place,
            @Nullable @JsonProperty("zones") int[] zoneIds,
            @JsonProperty("pointsPerHour") int pointsPerHour,
            @JsonProperty("rank") int rank,
            @Nullable @JsonProperty("medals") int[] medals,
            @JsonProperty("totalPoints") int totalPoints,
            @JsonProperty("blocktime") int blocktime,
            @JsonProperty("taken") int taken,
            @JsonProperty("uniqueZonesTaken") int uniqueZonesTaken
    ) {
        this.id = id;
        this.name = StringUtil.requireNotNullAndNotTrimmedEmpty(name);
        this.country = country;
        this.region = region;
        this.points = points;
        this.place = place;
        this.zoneIds = zoneIds;
        this.pointsPerHour = pointsPerHour;
        this.rank = rank;
        this.medals = medals;
        this.totalPoints = totalPoints;
        this.blocktime = blocktime;
        this.taken = taken;
        this.uniqueZonesTaken = uniqueZonesTaken;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public Region getRegion() {
        return region;
    }

    public int getPoints() {
        return points;
    }

    public int getPlace() {
        return place;
    }

    public int[] getZoneIds() {
        return zoneIds;
    }

    public int getPointsPerHour() {
        return pointsPerHour;
    }

    public int getRank() {
        return rank;
    }

    public int[] getMedals() {
        return medals;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public int getBlocktime() {
        return blocktime;
    }

    public int getTaken() {
        return taken;
    }

    public int getUniqueZonesTaken() {
        return uniqueZonesTaken;
    }

    @Override
    public String toString() {
        return String.format("User=[id=%d, name=%s%s%s%s]", id, StringUtil.printable(name),
                StringUtil.printable(country, ", country="), StringUtil.printable(region, ", region="),
                (zoneIds != null) ? statisticsToString() : "");
    }

    private String statisticsToString() {
        return String.format(
                ", points=%d, place=%d, zones=%s, pointsPerHour=%d, rank=%d, meadals=%s, totalPoints=%d, "
                        + "blocktime=%d, taken=%d, uniqueZonesTaken=%d",
                points, place, Arrays.toString(zoneIds), pointsPerHour, rank, Arrays.toString(medals), totalPoints,
                blocktime, taken, uniqueZonesTaken);
    }
}
