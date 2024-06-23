package org.joelson.turf.turfgame.util;

import com.fasterxml.jackson.databind.JsonNode;
import org.joelson.turf.util.FilesUtil;
import org.joelson.turf.util.JacksonUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FeedsTimeTypeReader {

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.printf("Usage:\n\t%s feed_file1.json ...%n", FeedsTimeTypeReader.class.getName());
            return;
        }
        for (String filename : args) {
            System.out.println("*** Reading " + filename);
            FilesUtil.forEachFile(Path.of(filename), true, new FeedsPathComparator(),
                    FeedsTimeTypeReader::readFeedFile);
        }
    }

    private static void readFeedFile(Path feedPath) {
        String content = null;
        try {
            content = Files.readString(feedPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (JsonNode node : JacksonUtil.readValue(content, JsonNode[].class)) {
            System.out.println("    " + node.get("time") + " - " + node.get("type"));
        }
    }
}
