package org.joelson.turf.util;

import java.util.Arrays;
import java.util.Objects;

public final class StringUtil {

    private StringUtil() throws InstantiationException {
        throw new InstantiationException("Should not be instantiated!");
    }

    public static String requireNullOrNonEmpty(String s) throws IllegalArgumentException {
        if (s != null && s.isEmpty()) {
            throw new IllegalArgumentException();
        }
        return s;
    }

    public static String requireNullOrNonEmpty(String s, String message) throws IllegalArgumentException {
        if (s != null && s.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        return s;
    }

    public static String requireNotNullAndNotEmpty(String s) throws NullPointerException, IllegalArgumentException {
        if (Objects.requireNonNull(s).isEmpty()) {
            throw new IllegalArgumentException();
        }
        return s;
    }

    public static String requireNotNullAndNotEmpty(String s, String nullMessage, String emptyMessage)
            throws NullPointerException, IllegalArgumentException {
        if (Objects.requireNonNull(s, nullMessage).isEmpty()) {
            throw new IllegalArgumentException(emptyMessage);
        }
        return s;
    }

    public static String requireNotNullAndNotTrimmedEmpty(String s)
            throws NullPointerException, IllegalArgumentException {
        if (Objects.requireNonNull(s).trim().isEmpty()) {
            throw new IllegalArgumentException();
        }
        return s;
    }

    public static String requireNotNullAndNotTrimmedEmpty(String s, String nullMessage, String emptyTrimmedMessage)
            throws NullPointerException, IllegalArgumentException {
        if (Objects.requireNonNull(s, nullMessage).trim().isEmpty()) {
            throw new IllegalArgumentException(emptyTrimmedMessage);
        }
        return s;
    }

    public static String printable(String s) {
        return (s == null) ? "null" : '\'' + s + '\'';
    }

    public static String printable(Object value, String possiblePrefix) {
        return switch (value) {
            case null -> "";
            case String s -> possiblePrefix + printable(s);
            default -> possiblePrefix + value;
        };
    }

    public static String printable(Object[] value, String possiblePrefix) {
        return (value == null) ? "" : possiblePrefix + Arrays.toString(value);
    }
}
