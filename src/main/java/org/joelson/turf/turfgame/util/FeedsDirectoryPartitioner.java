package org.joelson.turf.turfgame.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class FeedsDirectoryPartitioner {

    public static void main(String[] args) throws IOException {
        String feedpath = null;
        String version = null;
        String server = null;
        for (String arg : args) {
            if (arg.startsWith("-feedpath=")) {
                feedpath = arg.substring(10);
            } else if (arg.startsWith("-version=")) {
                version = arg.substring(9);
            } else if (arg.startsWith("-server=")) {
                server = arg.substring(8);
            } else {
                System.err.printf("Unknown option \"%s\"", arg);
            }
        }
        if (args.length != 3 || feedpath == null || version == null || server == null) {
            exitWithErrorMessage("Usage:%n\t%s -feedpath=C:\\feeds\\feeds_v4 -version=v4 -server=win",
                    FeedsDirectoryPartitioner.class.getName());
        }

        List<String> dates = new ArrayList<>();
        while (!isEmpty(feedpath)) {
            String firstUntil = getFirstUntil(feedpath);
            if (firstUntil == null) {
                exitWithErrorMessage("No first date could be found in feedpath " + feedpath);
            } else {
                String today = LocalDate.now().toString();
                if (firstUntil.compareTo(today) >= 0) {
                    System.out.printf("Have reached present week - %s is after %s.%n", firstUntil, today);
                    break;
                }
            }
            dates.add(firstUntil);
            FeedsPartitioner.main(new String[] {
                    "-feedpath=" + feedpath,
                    "-version=" + version,
                    "-server=" + server,
                    "-until=" + firstUntil
            });
        }
        for (String date : dates) {
            System.out.println("date " + date);
        }
    }

    private static boolean isEmpty(String pathString) {
        Path path = Path.of(pathString);
        Iterator<Path> iterator = path.iterator();
        return !iterator.hasNext();
    }

    private static String getFirstUntil(String feedpath) {
        String firstDate = null;
        try (Stream<Path> feedpathFiles = Files.list(Path.of(feedpath))) {
            Optional<String> possibleFirstDate = feedpathFiles.map(FeedsPartitioner::getDate).sorted().findFirst();
            if (possibleFirstDate.isEmpty()) {
                exitWithErrorMessage("No files present in feedpath " + feedpath);
            } else {
                firstDate = possibleFirstDate.get();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace(System.out);
        }
        if (firstDate == null) {
            return null;
        }

        LocalDate localDate = LocalDate.parse(firstDate);
        DayOfWeek dayOfWeek = localDate.getDayOfWeek();
        if (dayOfWeek == DayOfWeek.SUNDAY) {
            return localDate.toString();
        } else {
            return localDate.plusDays(7 - dayOfWeek.get(ChronoField.DAY_OF_WEEK)).toString();
        }
    }

    private static void exitWithErrorMessage(String format, Object... args) {
        System.err.printf(format, args);
        System.exit(-1);
    }
}
