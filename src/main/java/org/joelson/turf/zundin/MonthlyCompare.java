package org.joelson.turf.zundin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MonthlyCompare {

    public static void main(String[] args) throws IOException {
        Map<String, Map<String, MonthlyZone>> monthlyFiles = new HashMap<>(args.length);
        int round = 0;
        for (String filename : args) {
            Path path = Path.of(filename);
            String content = Files.readString(path);
            Monthly monthly = Monthly.fromHTML("0beroff", round++, content);
            monthlyFiles.put(filename, toMap(monthly));
            System.out.println(monthly.getZones().size());
        }
        for (int i = 1; i < args.length; i += 1) {
            System.out.println("Comparing " + args[i - 1] + " and " + args[i]);
            Map<String, MonthlyZone> first = monthlyFiles.get(args[i - 1]);
            Map<String, MonthlyZone> second = monthlyFiles.get(args[i]);
            Set<String> lowered = new HashSet<>();
            Set<String> raised = new HashSet<>();
            Set<String> same = new HashSet<>();
            for (MonthlyZone zone : first.values()) {
                MonthlyZone otherZone = second.get(zone.getName());
                if (otherZone != null) {
                    if (otherZone.getTP() > zone.getTP()) {
                        raised.add(zone.getTP() + " -> " + otherZone.getTP() + " : " + zone.getName());
                    } else if (otherZone.getTP() < zone.getTP()) {
                        lowered.add(zone.getTP() + " -> " + otherZone.getTP() + " : " + zone.getName());
                    } else {
                        same.add(zone.getName());
                    }
                }
            }
            System.out.println("Raised:");
            raised.stream().sorted().forEach(System.out::println);
            System.out.println("Lowered:");
            lowered.stream().sorted().forEach(System.out::println);
            System.out.println("Same:");
            same.stream().sorted().forEach(System.out::println);
        }
    }

    private static Map<String, MonthlyZone> toMap(Monthly monthly) {
        List<MonthlyZone> zones = monthly.getZones();
        Map<String, MonthlyZone> monthlyMap = new HashMap<>(zones.size());
        zones.forEach(monthlyZone -> monthlyMap.put(monthlyZone.getName(), monthlyZone));
        return monthlyMap;
    }
}
