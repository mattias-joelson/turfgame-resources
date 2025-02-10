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
        JsonNode[] nodes = null;
        try {
            String content = Files.readString(feedPath);
            nodes = JacksonUtil.readValue(content, JsonNode[].class);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Exception reading path " + feedPath);
            System.exit(-1);
        }
        for (JsonNode node : nodes) {
            System.out.println("    " + node.get("time") + " - " + node.get("type"));
        }
    }
}
