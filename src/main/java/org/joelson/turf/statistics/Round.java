package org.joelson.mattias.turfgame.statistics;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

public class Round implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int id;
    private final String name;
    private final ZonedDateTime start;

    public Round(int id, String name, ZonedDateTime start) {
        this.id = id;
        this.name = Objects.requireNonNull(name);
        this.start = Objects.requireNonNull(start);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ZonedDateTime getStart() {
        return start;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Round) {
            Round round = (Round) obj;
            return id == round.id && name.equals(round.name) && start.equals(round.start);
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return "Round{id:" + id + ",name:'" + name + "',start:'" + start + "'}";
    }
}
