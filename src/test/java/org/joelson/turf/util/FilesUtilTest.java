package org.joelson.turf.util;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FilesUtilTest {

    private static Path pathOfResource(String resource) {
        return new File(FilesUtilTest.class.getResource("/" + resource).getFile()).toPath();
    }

    @Test
    void isZipFileTest() {
        assertTrue(FilesUtil.isZipFile(pathOfResource("complex.zip")));
        assertFalse(FilesUtil.isZipFile(pathOfResource("regions.json")));
    }

    @Test
    void forEachFileTest() throws IOException {
        Path zipFilePath = pathOfResource("complex.zip");
        PathFileCounter counter = new PathFileCounter();
        FilesUtil.forEachFile(zipFilePath, true, Path::compareTo, counter::countPath);
        assertEquals(12, counter.noPaths);
        assertEquals(18999, counter.contentHashCode);
    }

    private static class PathFileCounter {
        private int noPaths = 0;
        private int contentHashCode = 0;

        public void countPath(Path path) {
            noPaths += 1;
            String content = null;
            try {
                content = Files.readString(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            contentHashCode += content.hashCode();
        }
    }
}