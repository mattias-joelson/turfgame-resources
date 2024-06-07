package org.joelson.mattias.turfgame.statistics;

import java.io.Serializable;
import java.util.Objects;

public class Zone implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Municipality municipality;

    private final int id;
    private final String name;
    private final float latitude;
    private final float longitude;

    public Zone(int id, String name, Municipality municipality, float latitude, float longitude) {
        this.municipality = Objects.requireNonNull(municipality);

        this.id = id;
        this.name = Objects.requireNonNull(name);
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Municipality getMunicipality() {
        return municipality;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Zone) {
            Zone zone = (Zone) obj;
            return id == zone.id && municipality.equals(zone.municipality) && name.equals(zone.name)
                    && latitude == zone.latitude && longitude == zone.longitude;
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return "Zone{id:" + id + ",name:'" + name + "',municipality:" + municipality
                + ",latitude:" + latitude + ",longitude:" + longitude + '}';
    }
}
