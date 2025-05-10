package org.joelson.turf.turfgame.apiv5;

import org.joelson.turf.util.TimeUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PeriodZoneDistribution {

    private final Instant from;
    private final Instant to;
    private final Path zonesPath;

    public PeriodZoneDistribution(Instant from, Instant to, Path zonesPath) {
        this.from = Objects.requireNonNull(from);
        this.to = Objects.requireNonNull(to);
        if (!Files.exists(Objects.requireNonNull(zonesPath)) || !Files.isRegularFile(zonesPath)) {
            throw new IllegalArgumentException("zonesPath " + zonesPath + " is not a file.");
        }
        this.zonesPath = zonesPath;
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            System.err.printf("Usage:%n\t%s 2025-01-05 2025-05-04 zones-all.v5.json",
                    PeriodZoneDistribution.class.getName());
            System.exit(-1);
        }
        new PeriodZoneDistribution(dateToInstant(args[0]), dateToInstant(args[1]),
                Path.of(args[2])).printDistribution();
    }

    private static Instant dateToInstant(String date) {
        LocalDateTime localDateTime = LocalDateTime.parse(date + "T12:00:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        return localDateTime.atZone(ZoneId.of("Europe/Stockholm")).toInstant();
    }

    private void printDistribution() throws IOException {
        String json = Files.readString(zonesPath);
        List<Zone> zones = Zones.fromJSON(json);
        List<Zone> periodZones = zones.stream().filter(this::inPeriod).toList();
        Map<String, Map<String, Map<String, List<Zone>>>> countryRegionAreaZones = new HashMap<>();
        Map<Integer, List<Zone>> nonCountryRegionZones = new HashMap<>();
        for (Zone zone : periodZones) {
            Region region = zone.getRegion();
            String country = region.getCountry();
            if (country != null) {
                String area = region.getArea().getName();
                countryRegionAreaZones.computeIfAbsent(country, c -> new HashMap<>())
                        .computeIfAbsent(region.getName(), i -> new HashMap<>())
                        .computeIfAbsent(area, a -> new ArrayList<>())
                        .add(zone);
            } else {
                nonCountryRegionZones.computeIfAbsent(region.getId(), i -> new ArrayList<>()).add(zone);
            }
        }
        System.out.printf("Zones: %d%n", periodZones.size());
        countryRegionAreaZones.entrySet().stream().sorted(Map.Entry.comparingByKey())
                .forEach(craZones -> printCountryDistributionSynopsis(true, craZones.getKey(), craZones.getValue()));
        nonCountryRegionZones.values().stream()
                .sorted(Comparator.comparing(znes -> znes.getFirst().getRegion().getName()))
                .forEach(zoneList -> printNonCountryDistributionSynopsis(true, zoneList));

        System.out.printf("Zones: %d%n", periodZones.size());
        countryRegionAreaZones.entrySet().stream().sorted(Map.Entry.comparingByKey())
                .forEach(craZones -> printCountryDistributionSynopsis(false, craZones.getKey(), craZones.getValue()));
        nonCountryRegionZones.values().stream()
                .sorted(Comparator.comparing(znes -> znes.getFirst().getRegion().getName()))
                .forEach(zoneList -> printNonCountryDistributionSynopsis(false, zoneList));
    }

    private boolean inPeriod(Zone zone) {
        Instant creationTime = TimeUtil.turfAPITimestampToInstant(zone.getDateCreated());
        return creationTime.equals(from) || (creationTime.isAfter(from) && creationTime.isBefore(to));
    }

    private void printCountryDistributionSynopsis(
            boolean synopsis, String country, Map<String, Map<String, List<Zone>>> regionAreaZones) {
        int countryZones = 0;
        for (Map<String, List<Zone>> regionZones : regionAreaZones.values()) {
            for (List<Zone> areaZones : regionZones.values()) {
                countryZones += areaZones.size();
            }
        }
        System.out.printf("Country %s: %d zones%n", country, countryZones);
        regionAreaZones.entrySet().stream().sorted(Map.Entry.comparingByKey())
                .forEach(raZones -> printRegionDistributionSynopsis(synopsis, raZones.getKey(), raZones.getValue()));
    }

    private void printRegionDistributionSynopsis(boolean synopsis, String region, Map<String, List<Zone>> areaZones) {
        int regionZones = areaZones.values().stream().mapToInt(List::size).sum();
        System.out.printf("    Region %s: %d zones%n", region, regionZones);
        areaZones.entrySet().stream().sorted(Map.Entry.comparingByKey())
                .forEach(aZones -> printAreaDistribution(synopsis, aZones.getKey(), aZones.getValue()));
    }

    private void printAreaDistribution(boolean synopsis, String area, List<Zone> zones) {
        System.out.printf("        Area %s: %d zones%n", area, zones.size());
        if (!synopsis) {
            zones.stream().sorted(Comparator.comparing(Zone::getDateCreated)).forEach(this::printAreaZone);
        }
    }

    private void printAreaZone(Zone zone) {
        System.out.printf("            %s: %s%s%n",
                toLocalDateTime(zone.getDateCreated()), zone.getName(), typeOf(zone));
    }

    private void printNonCountryDistributionSynopsis(boolean synopsis, List<Zone> zones) {
        System.out.printf("Region %s: %d zones%n", zones.getFirst().getRegion().getName(), zones.size());
        if (!synopsis) {
            zones.stream().sorted(Comparator.comparing(Zone::getDateCreated)).forEach(this::printNonCountryZone);
        }
    }

    private void printNonCountryZone(Zone zone) {
        System.out.printf("    %s: %s%s%n", toLocalDateTime(zone.getDateCreated()), zone.getName(), typeOf(zone));
    }

    private static String typeOf(Zone zone) {
        Type type = zone.getType();
        if (type != null) {
            return String.format(" (%d - %s)", type.getId(), type.getName());
        }
        return "";
    }

    private static String toLocalDateTime(String dateCreated) {
        Instant instant = TimeUtil.turfAPITimestampToInstant(dateCreated);
        LocalDateTime localDateTime = instant.atZone(ZoneId.of("Europe/Stockholm")).toLocalDateTime();
        return DateTimeFormatter.ISO_LOCAL_DATE.format(localDateTime.toLocalDate()) + " "
                + DateTimeFormatter.ISO_LOCAL_TIME.format(localDateTime.toLocalTime());
    }
}
