package org.joelson.turf.turfgame.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

public class FeedsPartitioner {

    public static void main(String[] args) throws IOException {
        String feedpath = null;
        String version = null;
        String server = null;
        String until = null;
        for (String arg : args) {
            if (arg.startsWith("-feedpath=")) {
                feedpath = arg.substring(10);
            } else if (arg.startsWith("-version=")) {
                version = arg.substring(9);
            } else if (arg.startsWith("-server=")) {
                server = arg.substring(8);
            } else if (arg.startsWith("-until=")) {
                until = arg.substring(7);
            } else {
                System.err.printf("Unknown option \"%s\"", arg);
            }
        }
        if (args.length != 4 || feedpath == null || version == null || server == null || until == null) {
            exitWithErrorMessage("Usage:%n\t%s -feedpath=C:\\feeds\\feeds_v4 -version=v4 -server=win -until=2024-06-16", FeedsPartitioner.class.getName());
        }
        String date = until;

        Path partitionDirectory = Path.of(feedpath, "partition");
        if (Files.exists(partitionDirectory)) {
            exitWithErrorMessage("Can not create directory %s - file exists.", partitionDirectory);
        }

        Files.createDirectory(partitionDirectory);
        System.out.printf("<create directory %s>%n", partitionDirectory);

//        int noFiles = Files.list(Path.of(feedpath)).mapToInt(path -> 1).sum();
//        System.out.println("noFiles: " + noFiles);
//
//        noFiles = Files.list(Path.of(feedpath))
//                .filter(path -> pathNotLarger(date, path))
//                .mapToInt(path -> 1).sum();
//        System.out.println("noFiles: " + noFiles);
        Files.list(Path.of(feedpath)).filter(path -> includeFile(date, path))
                .forEach(path -> moveFile(partitionDirectory, path));

        if (Files.list(partitionDirectory).count() == 0) {
            Files.delete(partitionDirectory);
            exitWithErrorMessage("No files to include until %s", date);
        }
        String firstDate = Files.list(partitionDirectory).filter(path -> includeFile(date, path))
                .map(FeedsPartitioner::getDate).sorted().findFirst().orElseThrow();
        System.out.println("firstDate: " + firstDate);

        Path finalPartition = Path.of(feedpath, "feeds_" + version + '_' + firstDate + "." + server);
        System.out.printf("<move %s to %s>%n", partitionDirectory, finalPartition);
        Files.move(partitionDirectory, finalPartition);
        System.out.printf("archive:%n\t7z a %s %s%n", finalPartition.getFileName() + ".zip", finalPartition);
        String[] command = {
                "\"C:\\Program Files\\7-Zip\\7z.exe\"",
                "a",
                finalPartition.getFileName() + ".zip",
                finalPartition.toString()};
        int status = invokeProcess(command);

        if (status == 0) {
            Files.list(finalPartition).forEach(path -> {
                try {
                    Files.delete(path);
                    System.out.printf("<removed %s>%n", path);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            Files.delete(finalPartition);
            System.out.printf("<removed %s>%n", finalPartition);
        }
    }

    private static int invokeProcess(String[] command) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        Process process = processBuilder.command(command).start();
        InputStream inputStream = process.getInputStream();
        try (BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = inputReader.readLine()) != null) {
                System.out.println(line);
            }
        }
        int status = 0;
        try {
            status = process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
            exitWithErrorMessage("Error when waiting on command { %s } to finish", String.join(" ", command));
        }
        if (status != 0) {
            exitWithErrorMessage("Command { %s } did not exit with status 0 but %d",
                    String.join(" ", command), status);
        }
        return status;
    }

    private static void exitWithErrorMessage(String format, Object... args) {
        System.err.printf(format, args);
        System.exit(-1);
    }

    private static void moveFile(Path partitionDirectory, Path path) {
        Path destination = partitionDirectory.resolve(path.getFileName());
        System.out.printf("<move %s to %s>%n", path, destination);
        try {
            Files.move(path, destination);
        } catch (IOException e) {
            e.printStackTrace();
            exitWithErrorMessage("Could not move %s to %s", path, partitionDirectory);
        }
    }

    private static boolean includeFile(String date, Path path) {
        try {
            return !Files.isDirectory(path) && getDate(path).compareTo(date) <= 0;
        } catch (RuntimeException e) {
            return false;
        }
//        String filename = path.getFileName().toString();
//        int startIndex = -1;
//        for (int i = 0; i < filename.length(); i += 1) {
//            if (Character.isDigit(filename.charAt(i))) {
//                startIndex = i;
//                break;
//            }
//        }
//        if (startIndex < 0) {
//            return false;
//        }
//        return filename.substring(startIndex, startIndex + date.length()).compareTo(date) <= 0;
    }

    private static String getDate(Path path) {
        String filename = path.getFileName().toString();
        int startIndex = -1;
        for (int i = 0; i < filename.length(); i += 1) {
            if (Character.isDigit(filename.charAt(i))) {
                startIndex = i;
                break;
            }
        }
        if (startIndex < 0) {
            throw new IndexOutOfBoundsException("Could not find date!");
        }
        if (Character.isDigit(filename.charAt(startIndex + 1))
                && Character.isDigit(filename.charAt(startIndex + 2))
                && Character.isDigit(filename.charAt(startIndex + 3))
                && filename.charAt(startIndex + 4) == '-'
                && Character.isDigit(filename.charAt(startIndex + 5))
                && Character.isDigit(filename.charAt(startIndex + 6))
                && filename.charAt(startIndex + 7) == '-'
                && Character.isDigit(filename.charAt(startIndex + 8))
                && Character.isDigit(filename.charAt(startIndex + 9))) {
            return filename.substring(startIndex, startIndex + 10);
        }
        throw new NumberFormatException("Bad date format: " + filename.substring(startIndex, startIndex + 10));
    }
}

