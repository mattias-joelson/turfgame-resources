package org.joelson.turf.turfgame.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class FeedsRenamer {

    private static final Logger logger = LoggerFactory.getLogger(FeedsRenamer.class);

    private static final int ERROR_EXIT_STATUS = -1;

    private final Path logPath;
    private final Map<String, String> renameCandidates = new HashMap<>();
    private String lastUnablePath = null;
    private int filesRenamed;
    private int filesFailedRenamed;
    private int fromMissing;
    private int toMissing;

    public FeedsRenamer(Path logPath) {
        if (!Files.exists(Objects.requireNonNull(logPath)) || Files.isDirectory(logPath)) {
            exitWithError(String.format("File %s does not exist or is a directory.", logPath));
        }
        this.logPath = logPath;
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            exitWithError(String.format("""
                            Usage:
                            %s feeds_log_file
                            
                            An existing log file of FeedsDownloader""",
                    FeedsRenamer.class.getName()));
        }
        new FeedsRenamer(Path.of(args[0])).renameFiles();
    }

    private static void exitWithError(String msg) {
        logger.error(msg);
        System.exit(ERROR_EXIT_STATUS);
    }

    private void renameFiles() throws IOException {
        try (Stream<String> lineStream = Files.lines(logPath)) {
            lineStream.forEach(line -> parseLine(line, renameCandidates));
        }
        renameCandidates.entrySet().forEach(this::renameFile);
        logger.info("renameCandidates.size()={}, filesRenamed={}, filesFailedRenamed={}, fromMissing={},  toMissing={}",
                renameCandidates.size(), filesRenamed, filesFailedRenamed, fromMissing, toMissing);
    }

    private void parseLine(String line, Map<String, String> renameCandidates) {
        if (line.startsWith("[ERROR] ")) {
            if (line.contains(" Unable to store to ") && line.endsWith(":")) {
                if (lastUnablePath != null) {
                    exitWithError(String.format("Read line \"%s\", lastUnablePath=\"%s\"", line, lastUnablePath));
                }
                lastUnablePath = line.substring(line.indexOf(" Unable to store to ") + 20, line.length() - 1);
            }
        } else if (line.startsWith("[INFO ] ")) {
            if (line.contains(" Stored ") && line.contains("feed_download") && line.endsWith(".content")) {
                if (lastUnablePath == null) {
                    exitWithError(String.format("Read line \"%s\", lastUnablePath=null", line));
                }
                String contentPath = line.substring(line.indexOf(" Stored ") + 8);
                renameCandidates.put(contentPath, lastUnablePath);
                lastUnablePath = null;
            }
        }
    }

    private void renameFile(Map.Entry<String, String> renameCandidate) {
        Path fromPath = Path.of(renameCandidate.getKey());
        Path toPath = Path.of(renameCandidate.getValue());
        if (Files.exists(fromPath)) {
            try {
                Files.move(fromPath, toPath);
                filesRenamed += 1;
            } catch (IOException e) {
                logger.error("Failed to move {} to {}", fromPath, toPath, e);
                filesFailedRenamed += 1;
            }
        } else {
            fromMissing += 1;
            if (!Files.exists(toPath)) {
                toMissing += 1;
            }
        }
    }
}
