package org.joelson.turf.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class JacksonUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private JacksonUtil() throws InstantiationException {
        throw new InstantiationException("Should not be instantiated!");
    }

    public static <T> T readValue(String content, Class<T> valueType) throws JsonProcessingException {
        return OBJECT_MAPPER.readValue(content, valueType);
    }

    public static <T> T treeToValue(TreeNode n, Class<T> valueType) throws JsonProcessingException {
        return OBJECT_MAPPER.treeToValue(n, valueType);
    }
}
