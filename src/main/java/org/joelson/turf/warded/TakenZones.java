package org.joelson.turf.warded;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.joelson.turf.util.JacksonUtil;

import java.util.HashMap;
import java.util.Map;

public final class TakenZones {

    private static final String PROPERTIES_PROPERTY = "properties";
    private static final String TITLE_PROPERTY = "title";
    private static final String COUNT_PROPERTY = "count";
    private static final char ARRAY_START = '[';
    private static final char ARRAY_END = ']';

    private TakenZones() throws InstantiationException {
        throw new InstantiationException("Should not be instantiated!");
    }

    public static String getUserNameFromHTML(String s) {
        int startIndex = s.indexOf("<a href=/turf/user.php>");
        if (startIndex < 0) {
            return null;
        }
        startIndex += 23;
        int endIndex = s.indexOf("</a>", startIndex);
        return s.substring(startIndex, endIndex);
    }

    public static Map<String, Integer> fromHTML(String s) throws RuntimeException {
        String json = getZonesJSONSting(s);
        Map<String, Integer> zoneCount = new HashMap<>();
        JsonNode[] jsonNodes;
        try {
            jsonNodes = JacksonUtil.readValue(json, JsonNode[].class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        for (JsonNode node : jsonNodes) {
            JsonNode properties = node.get(PROPERTIES_PROPERTY);
            String title = properties.get(TITLE_PROPERTY).asText();
            int count = properties.get(COUNT_PROPERTY).asInt();
            zoneCount.put(title, count);
        }

        return zoneCount;
    }

    private static String getZonesJSONSting(String s) {
        int startIndex = s.indexOf("\"features\": ");
        startIndex = s.indexOf(ARRAY_START, startIndex);
        int endIndex = s.indexOf("});", startIndex);
        endIndex = s.lastIndexOf(ARRAY_END, endIndex) + 1;
        return s.substring(startIndex, endIndex);
    }
}
