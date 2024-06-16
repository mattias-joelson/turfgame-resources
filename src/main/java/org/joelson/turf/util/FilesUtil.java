package org.joelson.turf.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.function.Consumer;

public final class FilesUtil {

    private static final String DEFAULT_OUTPUT_DIRECTORY = "output";
    private static final OpenOption[] DEFAULT_OPEN_OPTIONS =
            { StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING };

    private FilesUtil() throws InstantiationException {
        throw new InstantiationException("Should not be instantiated!");
    }

    private static Path filenamePath(String... filenameParts) {
        if (filenameParts.length == 0) {
            throw new IllegalArgumentException("At least one filename part must be specified.");
        }
        return Paths.get(DEFAULT_OUTPUT_DIRECTORY, filenameParts);
    }

    private static Path createParentDirectory(Path path) throws IOException {
        if (!Files.exists(path.getParent())) {
            Files.createDirectory(path.getParent());
        }
        return path;
    }

    public static OutputStream newDefaultOutputStream(String... filenameParts) throws IOException {
        return Files.newOutputStream(createParentDirectory(filenamePath(filenameParts)), DEFAULT_OPEN_OPTIONS);
    }

    public static BufferedWriter newDefaultWriter(String... filenameParts) throws IOException {
        return Files.newBufferedWriter(createParentDirectory(filenamePath(filenameParts)), DEFAULT_OPEN_OPTIONS);
    }

    public static boolean isZipFile(Path path) {
        try (InputStream in = Files.newInputStream(path)) {
            byte[] header = new byte[4];
            in.read(header);
            if (header[0] == 0x50 && header[1] == 0x4B) {
                if ((header[2] == 0x03 && header[3] == 0x04)
                        || (header[2] == 0x05 && header[3] == 0x06)
                        || (header[2] == 0x07 && header[3] == 0x08)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void forEachFile(Path path, boolean readZipFiles, Consumer<Path> pathConsumer) throws IOException {
        if (Files.isDirectory(path)) {
            for (Path child : Files.list(path).toList()) {
                forEachFile(child, readZipFiles, pathConsumer);
            }
        } else if (readZipFiles && isZipFile(path)) {
            try (FileSystem fs = FileSystems.newFileSystem(path)) {
                forEachFile(fs.getPath("/"), readZipFiles, pathConsumer);
            }
        } else {
            pathConsumer.accept(path);
        }
    }
}
