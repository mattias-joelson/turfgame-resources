package org.joelson.turf.zundin;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;

final class Parser {

    static final String ZONE_NAME_LINK_TAG = "<a href='zone.php?zoneid=";
    static final String TABLE_CELL_TAG = "<td>";
    static final String RIGHT_TABLE_CELL_TAG = "<td align='right'>";
    static final String LEFT_TABLE_CELL_TAG = "<td align='left'>";
    static final String LEFT_PADDED_TABLE_CELL_TAG = "<td style='padding-left:10px' align='left'>";
    private static final String DATE_TABLE_CELL_TAG =
            "<td><script language='javascript' type='text/javascript'>document.write(getLocalDate('";
    private static final String TWODAY_DATE_TABLE_CELL_TAG =
            "<td><script language='javascript' type='text/javascript'>document.write(moment(getLocalDate('";

    private Parser() throws InstantiationException {
        throw new InstantiationException("Should not be instantiated!");
    }

    public static String validString(String s) {
        if (s == null || s.isEmpty()) {
            throw new IllegalArgumentException("Null or empty string!");
        }
        return s;
    }

    public static int validNumber(int i, int min) {
        if (i < min) {
            throw new IllegalArgumentException("Integer " + i + " is less than " + min + '!');
        }
        return i;
    }

    public static StringPosition getString(String html, String afterPattern, StringPosition lastPosition) {
        int start = html.indexOf('>', html.indexOf(afterPattern, lastPosition.getPosition())) + 1;
        int end = html.indexOf('<', start);
        return new StringPosition(html.substring(start, end), end);
    }

    public static StringPosition getString(
            String html, String afterPattern, String endPattern, StringPosition lastPosition) {
        int start = html.indexOf(afterPattern, lastPosition.getPosition()) + afterPattern.length();
        int end = html.indexOf(endPattern, start);
        return new StringPosition(html.substring(start, end), end);
    }

    public static StringPosition getDateString(String html, StringPosition lastPosition) {
        int start = html.indexOf(DATE_TABLE_CELL_TAG, lastPosition.getPosition()) + DATE_TABLE_CELL_TAG.length();
        int end = html.indexOf('\'', start);
        return new StringPosition(html.substring(start, end), end);
    }

    public static StringPosition getTwodayDateString(String html, StringPosition lastPosition) {
        return getString(html, TWODAY_DATE_TABLE_CELL_TAG, "'", lastPosition);
    }

    static class StringPosition {
        private final String str;
        private final int position;

        StringPosition(String str, int position) {
            this.str = str;
            this.position = position;
        }

        int integerValue() {
            return Integer.valueOf(str.trim());
        }

        int integerValue(int valueIfEmpty) {
            if (str.trim().isEmpty()) {
                return valueIfEmpty;
            }
            return integerValue();
        }

        String stringValue() {
            return str;
        }

        int getPosition() {
            return position;
        }

        Duration durationValue() {
            int dayIndex = str.indexOf(" days ");
            int days = 0;
            if (dayIndex > 0) {
                days = Integer.valueOf(str.substring(0, dayIndex));
                dayIndex += 5;
            }
            // assists have no time
            if (str.substring(dayIndex + 1).isEmpty()) {
                return Duration.ofSeconds(0);
            }
            TemporalAccessor time = DateTimeFormatter.ISO_TIME.parse(str.substring(dayIndex + 1));
            return Duration.ofSeconds(time.getLong(ChronoField.SECOND_OF_DAY)).plusDays(days);
        }
    }
}
