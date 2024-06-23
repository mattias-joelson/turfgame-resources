package org.joelson.turf.statistics;

import java.io.Serializable;
import java.util.Objects;

public class Municipality implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Region region;

    private final String name;

    public Municipality(String name, Region region) {
        this.region = Objects.requireNonNull(region);

        this.name = Objects.requireNonNull(name);
    }

    public Region getRegion() {
        return region;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Municipality municipality) {
            return Objects.equals(region, municipality.region) && Objects.equals(name, municipality.name);
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return 31 * region.hashCode() + name.hashCode();
    }

    @Override
    public String toString() {
        return "Municipality{region:" + region + ",name:'" + name + "'}";
    }
}
