package org.joelson.turf.statistics;

import java.io.Serial;
import java.io.Serializable;

public class Country implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String name;

    public Country(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Country && name.equals(((Country) obj).name);
    }

    @Override
    public String toString() {
        return "Country{name:'" + name + "'}";
    }
}
