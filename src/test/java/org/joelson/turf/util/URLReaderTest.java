package org.joelson.turf.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.function.Function;

public final class URLReaderTest {

    public static <R> R readProperties(String resource, Function<String, R> function) throws IOException {
        File file = new File(URLReaderTest.class.getResource("/" + resource).getFile());
        try (FileInputStream input = new FileInputStream(file)) {
            return function.apply(URLReader.readUTF8Stream(input));
        }
    }
}
