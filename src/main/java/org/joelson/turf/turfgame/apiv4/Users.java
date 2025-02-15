package org.joelson.turf.turfgame.apiv4;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.joelson.turf.turfgame.util.TurfgameURLReader;
import org.joelson.turf.util.JacksonUtil;
import org.joelson.turf.util.URLReader.Response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class Users {
    
    private static final String USERS_REQUEST = "https://api.turfgame.com/v4/users";
    private static final String NAME_PARAMETER = "name";
    private static final String ID_PARAMETER = "id";
    
    private Users() throws InstantiationException {
        throw new InstantiationException("Should not be instantiated!");
    }
    
    public static List<User> getUsers(Object... inputObjects) throws IOException {
        if (inputObjects.length == 0) {
            return Collections.emptyList();
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        JsonFactory factory = new JsonFactory();
        JsonGenerator generator = factory.createGenerator(stream, JsonEncoding.UTF8);
        generator.writeStartArray();
        for (Object obj : inputObjects) {
            generator.writeStartObject();
            if (obj instanceof String) {
                generator.writeStringField(NAME_PARAMETER, (String) obj);
            } else if (obj instanceof Integer) {
                generator.writeNumberField(ID_PARAMETER, (Integer) obj);
            } else {
                throw new IllegalArgumentException("Unknown input object type " + obj.getClass());
            }
            generator.writeEndObject();
        }
        generator.writeEndArray();
        generator.close();
        String requestJSON = stream.toString(StandardCharsets.UTF_8);
        Response response = TurfgameURLReader.postTurfgameRequest(USERS_REQUEST, requestJSON);
        if (response.status() != HttpURLConnection.HTTP_OK) {
            System.err.printf("Response status: %d, request URL: %s, JSON: %s%n", response.status(), USERS_REQUEST,
                    requestJSON);
        }
        return fromJSON(response.body());
    }

    static List<User> fromJSON(String s) throws JsonProcessingException {
        return Arrays.asList(JacksonUtil.readValue(s, User[].class));
    }

    public static void main(String[] args) throws IOException {
        System.out.println(getUsers((Object[]) args));
    }
}
