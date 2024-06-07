package org.joelson.mattias.turfgame.warded;

import com.fasterxml.jackson.databind.JsonNode;
import org.joelson.mattias.turfgame.util.JacksonUtil;

import java.util.HashMap;
import java.util.Map;

public final class TakenZones {
    
    private static final String PROPERTIES_PROPERTY = "properties"; // NON-NLS
    private static final String TITLE_PROPERTY = "title"; // NON-NLS
    private static final String COUNT_PROPERTY = "count"; // NON-NLS
    private static final char ARRAY_START = '[';
    private static final char ARRAY_END = ']';

    private TakenZones() throws InstantiationException {
        throw new InstantiationException("Should not be instantiated!"); //NON-NLS
    }

    public static String getUserNameFromHTML(String s) {
        int startIndex = s.indexOf("<a href=/turf/user.php>"); //NON-NLS
        if (startIndex < 0) {
            return null;
        }
        startIndex += 23;
        int endIndex = s.indexOf("</a>", startIndex); //NON-NLS
        return s.substring(startIndex, endIndex);
    }
    
    public static Map<String, Integer> fromHTML(String s) {
        String json = getZonesJSONSting(s);
        Map<String, Integer> zoneCount = new HashMap<>();
        for (JsonNode node : JacksonUtil.readValue(json, JsonNode[].class)) {
            JsonNode properties = node.get(PROPERTIES_PROPERTY);
            String title = properties.get(TITLE_PROPERTY).asText();
            int count = properties.get(COUNT_PROPERTY).asInt();
            zoneCount.put(title, count);
        }

        return zoneCount;
    }

    private static String getZonesJSONSting(String s) {
        int startIndex = s.indexOf("\"features\": "); //NON-NLS
        startIndex = s.indexOf(ARRAY_START, startIndex);
        int endIndex = s.indexOf("});", startIndex);
        endIndex = s.lastIndexOf(ARRAY_END, endIndex) + 1;
        return s.substring(startIndex, endIndex);
    }
}
