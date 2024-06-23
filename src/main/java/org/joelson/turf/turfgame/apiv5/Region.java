package org.joelson.turf.turfgame.apiv5;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joelson.turf.util.StringUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Region implements org.joelson.turf.turfgame.Region {

    private final int id;
    private final String name;
    private final String country;
    private final Area area;
    private final Area[] areas;
    private final User regionLord;

    @JsonCreator
    public Region(
            @JsonProperty(value = "id", required = true) int id,
            @Nonnull @JsonProperty(value = "name", required = true) String name,
            @Nullable @JsonProperty("country") String country,
            @Nullable @JsonProperty("area") Area area,
            @Nullable @JsonProperty("areas") Area[] areas,
            @Nullable @JsonProperty("regionLord") User regionLord
    ) {
        this.id = id;
        this.name = StringUtil.requireNotNullAndNotTrimmedEmpty(name);
        this.country = StringUtil.requireNullOrNonEmpty(country);
        this.area = area;
        this.areas = areas;
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

    public Area[] getAreas() {
        return areas;
    }

    public User getRegionLord() {
        return regionLord;
    }

    @Override
    public String toString() {
        return String.format("Region[id=%d, name=%s%s%s%s%s]", id, StringUtil.printable(name),
                StringUtil.printable(country, ", country="), StringUtil.printable(area, ", area="),
                StringUtil.printable(areas, ", areas="), StringUtil.printable(regionLord, ", regionLord="));
    }
}
