package org.joelson.turf.turfgame.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

public class FeedsPathComparator implements Comparator<Path> {

    @Override
    public int compare(Path o1, Path o2) {
        if (Files.isDirectory(o1)) {
            if (Files.isDirectory(o2)) {
                return o1.toString().compareTo(o2.toString());
            } else {
                return -1;
            }
        } else if (Files.isDirectory(o2)) {
            return 1;
        } else {
            String o1FileName = o1.getFileName().toString();
            String o2FileName = o2.getFileName().toString();
            if (o1FileName.endsWith(".json") && o2FileName.endsWith(".json")) {
                return o1FileName.substring(0, o1FileName.length() - 5)
                        .compareTo(o2FileName.substring(0, o2FileName.length() - 5));
            } else {
                return o1FileName.compareTo(o2FileName);
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof FeedsPathComparator && getClass().equals(obj.getClass());
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
