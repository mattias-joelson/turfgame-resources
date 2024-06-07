package org.joelson.mattias.turfgame.lundkvist;

import org.joelson.mattias.turfgame.apiv4.Zone;
import org.joelson.mattias.turfgame.apiv4.ZonesTest;
import org.joelson.mattias.turfgame.util.URLReaderTest;
import org.joelson.mattias.turfgame.warded.HeatmapTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MunicipalityTest {
    
    @Test
    public void readSolna() throws Exception {
        Map<String, Boolean> zones = getSolnaZones();
        assertTrue(zones.size() >= 216);
        int taken = 0;
        for (Boolean takenZone : zones.values()) {
            if (takenZone) {
                taken += 1;
            }
        }
        assertTrue(taken >= 216);
    }
    
    @Test
    public void readStockholm() throws Exception {
        Map<String, Boolean> zones = getStockholmZones();
        assertTrue(zones.size() >= 1458);
        int taken = 0;
        for (Boolean takenZone : zones.values()) {
            if (takenZone) {
                taken += 1;
            }
        }
        assertTrue(taken >= 1454);
    }
    
    @Test
    public void readSundbyberg() throws Exception {
        Map<String, Boolean> zones = getSundbybergZones();
        assertTrue(zones.size() >= 112);
        int taken = 0;
        for (Boolean takenZone : zones.values()) {
            if (takenZone) {
                taken += 1;
            }
        }
        assertTrue(taken >= 111);
    }

    @Test
    public void circleZonesSorted() throws Exception {
        System.out.println("Circle zones in date order");
        List<Zone> zones = getZonesSorted(HeatmapTest.getCircleZones(), Comparator.comparing(Zone::getDateCreated));
        IntStream.range(0, zones.size())
                .forEach(i -> System.out.println(String.format("%3d - %s, %s",
                        i + 1, zones.get(i).getName(), zones.get(i).getDateCreated())));
    }

    private List<Zone> getZonesSorted(Set<String> filterZones, Comparator<Zone> comparator) throws Exception {
        List<Zone> zones = ZonesTest.getAllZones();
        return zones.stream()
                .filter(zone -> filterZones.contains(zone.getName()))
                .sorted(comparator)
                .collect(Collectors.toList());
    }
    
    @Test
    public void solnaZonesSorted() throws Exception {
        System.out.println("Solna zones in date order");
        List<Zone> zones = ZonesTest.getAllZones();
        Map<String, Boolean> solnaLundkvist = getSolnaZones();
        List<Zone> solnaZones = zones.stream()
                .filter(zone -> solnaLundkvist.containsKey(zone.getName()))
                .sorted(Comparator.comparing(Zone::getDateCreated))
                .collect(Collectors.toList());
        IntStream.range(0, solnaZones.size())
                .forEach(i -> System.out.println(String.format("%3d - %s, %s", i, solnaZones.get(i).getName(), solnaZones.get(i).getDateCreated())));
    }

    @Test
    public void danderydZonesSorted() throws Exception {
        System.out.println("Danderyd zones in date order");
        List<Zone> zones = ZonesTest.getAllZones();
        Map<String, Boolean> danderydLundkvist = getDanderydZones();
        List<Zone> solnaZones = zones.stream()
                .filter(zone -> danderydLundkvist.containsKey(zone.getName()))
                .sorted(Comparator.comparing(Zone::getName))
                .collect(Collectors.toList());
        IntStream.range(0, solnaZones.size())
                .forEach(i -> System.out.println(String.format("%3d - %s, %s", i, solnaZones.get(i).getName(), solnaZones.get(i).getDateCreated())));
    }

    public static Map<String, Boolean> getDanderydZones() throws IOException {
        return URLReaderTest.readProperties("lundkvist_141_danderyd.html", Municipality::fromHTML);
    }
    
    public static Map<String, Boolean> getHuddingeZones() throws Exception {
        return URLReaderTest.readProperties("lundkvist_141_huddinge.html", Municipality::fromHTML);
    }

    public static Map<String, Boolean> getJarfallaZones() throws Exception {
        return URLReaderTest.readProperties("lundkvist_141_jarfalla.html", Municipality::fromHTML);
    }

    public static Map<String, Boolean> getNackaZones() throws Exception {
        return URLReaderTest.readProperties("lundkvist_141_nacka.html", Municipality::fromHTML);
    }

    public static Map<String, Boolean> getSollentunaZones() throws Exception {
        return URLReaderTest.readProperties("lundkvist_141_sollentuna.html", Municipality::fromHTML);
    }

    public static Map<String, Boolean> getSolnaZones() throws Exception {
        return URLReaderTest.readProperties("lundkvist_141_solna.html", Municipality::fromHTML);
    }
    
    public static Map<String, Boolean> getStockholmZones() throws Exception {
        return URLReaderTest.readProperties("lundkvist_141_stockholm.html", Municipality::fromHTML);
    }
    
    public static Map<String, Boolean> getSundbybergZones() throws Exception {
        return URLReaderTest.readProperties("lundkvist_141_sundbyberg.html", Municipality::fromHTML);
    }
    
    public static Map<String, Boolean> getTabyZones() throws Exception {
        return URLReaderTest.readProperties("lundkvist_141_taby.html", Municipality::fromHTML);
    }

    public static Map<String, Boolean> getUpplandsBroZones() throws Exception {
        return URLReaderTest.readProperties("lundkvist_141_upplands-bro.html", Municipality::fromHTML);
    }
    
    public static Map<String, Boolean> getVallentunaZones() throws Exception {
        return URLReaderTest.readProperties("lundkvist_141_vallentuna.html", Municipality::fromHTML);
    }

    public static Map<String, Boolean> getLeifonsSolnaZones() throws Exception {
        return URLReaderTest.readProperties("lundkvist_leifons_141_solna.html", Municipality::fromHTML);
    }
    
    public static Map<String, Boolean> getLeifonsSundbybergZones() throws Exception {
        return URLReaderTest.readProperties("lundkvist_leifons_141_sundbyberg.html", Municipality::fromHTML);
    }
}
