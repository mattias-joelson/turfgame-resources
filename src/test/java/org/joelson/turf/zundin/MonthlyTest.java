package org.joelson.mattias.turfgame.zundin;

import org.joelson.mattias.turfgame.apiv4.Zone;
import org.joelson.mattias.turfgame.apiv4.ZonesTest;
import org.joelson.mattias.turfgame.statistics.Statistics;
import org.joelson.mattias.turfgame.statistics.StatisticsInitializer;
import org.joelson.mattias.turfgame.util.URLReaderTest;
import org.joelson.mattias.turfgame.util.ZoneUtil;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MonthlyTest {

    private static final String OBEROFF = "Oberoff";
    private static final int ROUND = 113;

    @Test
    public void parseStatisticsPartFile() throws Exception {
        Monthly monthly = getPartMonthly();
        assertNotNull(monthly);
        assertEquals(OBEROFF, monthly.getUserName());
        assertEquals(ROUND, monthly.getRound());
        assertEquals(423, (monthly.getZones()).size());
        int takes = monthly.getZones().stream()
                .map(MonthlyZone::getTakes)
                .mapToInt(Integer::intValue)
                .sum();
        assertEquals(872, takes);
        int sumPoints = monthly.getZones().stream()
                .map(MonthlyZone::getPoints)
                .mapToInt(Integer::intValue)
                .sum();
        assertEquals(159616, sumPoints);
        long totalDuration = monthly.getZones().stream()
                .mapToLong(zone -> zone.getTakes() * zone.getAverageDuration().toSeconds())
                .sum();
        long averageDuration = totalDuration / takes;
        assertEquals(Duration.ofSeconds(13 * 3600 + 43 * 60 + 25), Duration.ofSeconds(averageDuration));
    }

    @Test
    public void testAddPartToStatistics() throws Exception {
        Monthly monthly = getPartMonthly();
        Statistics statistics = StatisticsInitializer.initialize();
        Monthly.addToStatistics(monthly, statistics);
    }

    private static Monthly getPartMonthly() throws Exception {
        return readProperties("monthly_oberoff_round113_part.html");
    }

    @Test
    public void parseStatisticsFile() throws Exception {
        Monthly monthly = getMonthly();
        assertNotNull(monthly);
        assertEquals(OBEROFF, monthly.getUserName());
        assertEquals(ROUND, monthly.getRound());
        assertTrue(monthly.getZones().size() > 0);
    }

    @Test
    public void testAddToStatistics() throws Exception {
        Monthly monthly = getMonthly();
        Statistics statistics = StatisticsInitializer.initialize();
        Monthly.addToStatistics(monthly, statistics);
    }

    public static Monthly getMonthly() throws Exception {
        return readProperties("monthly_0beroff_round167.fairy.html");
    }

    private static Monthly readProperties(String resource) throws Exception {
        return URLReaderTest.readProperties(resource, s -> Monthly.fromHTML(OBEROFF, ROUND, s));
    }

    @Test
    public void createYearly() throws Exception {
        File outFile = new File(URLReaderTest.class.getResource("/yearly.html").getFile());
        File startFile = new File(URLReaderTest.class.getResource("/yearly_start.html").getFile());
        File endFile = new File(URLReaderTest.class.getResource("/yearly_end.html").getFile());
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outFile), StandardCharsets.UTF_8));
                FileInputStream startInput = new FileInputStream(startFile);
                FileInputStream endInput = new FileInputStream(endFile)) {
            
            int ch;
            while ((ch = startInput.read()) != -1) {
                writer.write(ch);
            }

            Monthly monthly = getMonthly();
            Map<String, Zone> zones = ZoneUtil.toNameMap(ZonesTest.getAllZones());
            boolean comma = false;
            for (MonthlyZone zone : monthly.getZones()) {
                Zone turfZone = zones.get(zone.getName());
                if (!turfZone.getRegion().getName().equals("Stockholm")) {
                    continue;
                }
                if (comma) {
                    writer.println(',');
                } else {
                    comma = true;
                }
                writer.println("\t\t{");
                writer.println("\t\t\t\"type\": \"Feature\",");
                writer.println("\t\t\t\"geometry\": {");
                writer.println("\t\t\t\t\"type\": \"Point\",");
                writer.println("\t\t\t\t\"coordinates\": [");
                writer.println("\t\t\t\t\t" + turfZone.getLongitude() + ',');
                writer.println("\t\t\t\t\t" + turfZone.getLatitude());
                writer.println("\t\t\t\t]");
                writer.println("\t\t\t},");
                writer.println("\t\t\t\"properties\": {");
                writer.println("\t\t\t\t\"title\": \"" + zone.getName() + "\",");
                writer.println("\t\t\t\t\"count\": " + (zone.getTakes() + zone.getAssists()));
                writer.println("\t\t\t}");
                writer.print("\t\t}");
            }
            writer.println();

            while ((ch = endInput.read()) != -1) {
                writer.write(ch);
            }
        }
    }
}

