package org.joelson.turf.turfgame.apiv5;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joelson.turf.util.StringUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Region {

    private final int id;
    private final String name;
    private final String country;
    private final Area area;
    private final User regionLord;

    @JsonCreator
    public Region(
            @Nonnull @JsonProperty("id") int id,
            @Nonnull @JsonProperty("name") String name,
            @Nullable @JsonProperty("country") String country,
            @Nullable @JsonProperty("area") Area area,
            @Nullable @JsonProperty("regionLord") User regionLord
    ) {
        this.country = StringUtil.requireNullOrNonEmpty(country);
        this.area = area;
        this.name = StringUtil.requireNotNullAndNotTrimmedEmpty(name);
        this.id = id;
        this.regionLord = regionLord;
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

    public Area getArea() {
        return area;
    }

    public User getRegionLord() {
        return regionLord;
    }
}
