package org.joelson.turf.turfgame.apiv4;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joelson.turf.util.StringUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class Region implements org.joelson.turf.turfgame.Region {

    private final int id;
    private final String name;
    private final String country;
    private final User regionLord;

    @JsonCreator
    private Region(
            @JsonProperty(value = "id", required = true) int id,
            @Nonnull @JsonProperty(value = "name", required = true) String name,
            @Nullable @JsonProperty("country") String country,
            @Nullable @JsonProperty("regionLord") User regionLord
    ) {
        this.id = id;
        this.name = StringUtil.requireNotNullAndNotTrimmedEmpty(name);
        this.country = StringUtil.requireNullOrNonEmpty(country);
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

    public User getRegionLord() {
        return regionLord;
    }

    @Override
    public String toString() {
        return String.format("Region[id=%d, name=%s%s%s]", id, StringUtil.printable(name),
                StringUtil.printable(country, ", country="), StringUtil.printable(regionLord, ", regionLord="));
    }
}
