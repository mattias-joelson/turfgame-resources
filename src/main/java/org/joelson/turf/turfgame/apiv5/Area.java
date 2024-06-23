package org.joelson.turf.turfgame.apiv5;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.joelson.turf.util.StringUtil;

import javax.annotation.Nonnull;

public class Area {

    private final int id;
    private final String name;

    public Area(
            @JsonProperty(value = "id", required = true) int id,
            @Nonnull @JsonProperty(value = "name", required = true) String name
    ) {
        this.id = id;
        this.name = StringUtil.requireNotNullAndNotTrimmedEmpty(name);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return String.format("Area[id=%d, name=%s]", id, StringUtil.printable(name));
    }
}
