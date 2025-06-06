package org.joelson.turf.turfgame.util;

import org.joelson.turf.turfgame.Zone;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ZoneUtil {

    private static final double R = 6371.0e3; // radius of Earth in meters

    private ZoneUtil() throws InstantiationException {
        throw new InstantiationException("Should not be instantiated!");
    }

    public static <Z extends Zone> double calcDistance(Z zone1, Z zone2) {
        return calcDistance(zone1.getLatitude(), zone1.getLongitude(), zone2.getLatitude(), zone2.getLongitude());
    }

    public static <Z extends Zone> double calcDistance(double latitude, double longitude, Z zone) {
        return calcDistance(latitude, longitude, zone.getLatitude(), zone.getLongitude());
    }

    /**
     * @see
     * <a href="https://stackoverflow.com/questions/27928/calculate-distance-between-two-latitude-longitude-points-haversine-formula">Stack overflow</a>
     * @see <a href="https://en.wikipedia.org/wiki/Haversine_formula">Haversine formula</a>
     */
    public static double calcDistance(double p1latitude, double p1longitude, double p2Latitude, double p2Longitude) {
        double phi1 = toRadians(p1latitude);
        double phi2 = toRadians(p2Latitude);
        double deltaPhi = toRadians(p2Latitude - p1latitude);
        double deltaLambda = toRadians(p2Longitude - p1longitude);

        double a = StrictMath.sin(deltaPhi / 2) * StrictMath.sin(deltaPhi / 2)
                + StrictMath.cos(phi1) * StrictMath.cos(phi2) * StrictMath.sin(deltaLambda / 2) * StrictMath.sin(deltaLambda / 2);
        double c = 2 * StrictMath.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private static double toRadians(double degrees) {
        return degrees * Math.PI / 180;
    }

    public static <Z extends Zone> Map<String, Z> toNameMap(Collection<Z> zones) {
        Map<String, Z> zonesMap = new HashMap<>(zones.size());
//        for (Z zone : zones) {
//            Z prevZone = zonesMap.putIfAbsent(zone.getName(), zone);
//            if (prevZone != null) {
//                throw new IllegalArgumentException(String.format("Contains two zones with name %s (id %d and %d).",
//                        zone.getName(), prevZone.getId(), zone.getId()));
//            }
//        }
        zones.forEach(z -> zonesMap.put(z.getName(), z));
        return zonesMap;
    }

    public static <Z extends Zone> Map<Integer, Z> toIdMap(Collection<Z> zones) {
        Map<Integer, Z> zonesMap = new HashMap<>(zones.size());
        for (Z zone : zones) {
            Z prevZone = zonesMap.putIfAbsent(zone.getId(), zone);
            if (prevZone != null) {
                throw new IllegalArgumentException(String.format("Contains two zones with id %d (name %s and %s).",
                        zone.getId(), prevZone.getName(), zone.getName()));
            }
        }
        return zonesMap;
    }
}
