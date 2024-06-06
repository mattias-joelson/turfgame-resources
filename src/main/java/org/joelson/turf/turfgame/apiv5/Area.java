package org.joelson.turf.turfgame.apiv5;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.joelson.turf.util.StringUtil;

import javax.annotation.Nonnull;

public class Area {

    private final int id;
    private final String name;

    public Area(
            @Nonnull @JsonProperty("id") int id,
            @Nonnull @JsonProperty("name") String name
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
}
