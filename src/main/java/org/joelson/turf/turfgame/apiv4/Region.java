package org.joelson.turf.turfgame.apiv4;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joelson.turf.util.StringUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

// TODO fix
public final class Region {

    private final String country;
    private final String name;
    private final int id;

    @JsonCreator
    private Region(
            @Nullable @JsonProperty("country") String country,
            @Nonnull @JsonProperty("name") String name,
            @JsonProperty("id") int id,
            @JsonProperty("regionLord") User regionLord) {
        this.country = StringUtil.requireNullOrNonEmpty(country);
        this.name = StringUtil.requireNotNullAndNotTrimmedEmpty(name);
        this.id = id;
    }

    public String getCountry() {
        return country;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Region{"
                + "country=" + StringUtil.printable(country)
                + ", name=" + StringUtil.printable(name)
                + ", id=" + id
                + '}';
    }
}
