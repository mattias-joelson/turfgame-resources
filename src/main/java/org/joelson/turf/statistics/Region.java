package org.joelson.mattias.turfgame.statistics;

import java.io.Serializable;
import java.util.Objects;

public class Region implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Country country;

    private final int id;
    private final String name;

    public Region(int id, String name, Country country) {
        this.country = country;

        this.id = id;
        this.name = Objects.requireNonNull(name);
    }
    public Country getCountry() {
        return country;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Region) {
            Region region = (Region) obj;
            return id == region.id && name.equals(region.name);
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return "Region{" + countryToString() + "id:" + id + ",name:'" + name + "'}";
    }

    private String countryToString() {
        return (country == null) ? "" : "country:" + country + ',';
    }
}
