package org.joelson.turf.util;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public final class TimeUtil {

    private static final int TIMESTAMP_END_INDEX = 19;
    private static final DateTimeFormatter TURF_API_FORMATTER
            = DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(ZoneId.from(ZoneOffset.UTC));
    private static final DateTimeFormatter ZUNDIN_GMT_DATETIME_FORMAT
            = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private TimeUtil() throws InstantiationException {
        throw new InstantiationException("Should not be instantiated!");
    }

    /**
     * Converts a Turf API timestamp to a Java Instant.
     *
     * @param timestamp on format "2016-02-10T20:47:08+0000", "+0000" can be omitted
     * @return instant of timestamp
     */
    public static Instant turfTimestampToInstant(String timestamp) {
        TemporalAccessor temporalAccessor
                = DateTimeFormatter.ISO_DATE_TIME.parse(timestamp.substring(0, TIMESTAMP_END_INDEX));
        LocalDateTime localDateTime = LocalDateTime.from(temporalAccessor);
        ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.systemDefault());
        return Instant.from(zonedDateTime);
    }

    public static Instant turfAPITimestampToInstant(String timestamp) {
        TemporalAccessor temporalAccessor
                = DateTimeFormatter.ISO_DATE_TIME.parse(timestamp.substring(0, TIMESTAMP_END_INDEX));
        LocalDateTime localDateTime = LocalDateTime.from(temporalAccessor);
        ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, ZoneOffset.UTC);
        return Instant.from(zonedDateTime);
    }

    public static String turfAPITimestampFormatter(Instant instant) throws IOException {
        return TURF_API_FORMATTER.format(instant).replace(":", "%3A") + "%2B0000";
    }

    /**
     * Converts a Zundin GMT timestamp to a Java Instant.
     *
     * @param timestamp on format "2016-02-10 20:47:58#
     * @return instant of timestamp
     */
    public static Instant zundinTimestampToInstant(String timestamp) {
        LocalDateTime dateTime = LocalDateTime.from(ZUNDIN_GMT_DATETIME_FORMAT.parse(timestamp));
        ZonedDateTime zonedDateTime = ZonedDateTime.of(dateTime, ZoneId.of("GMT"));
        return Instant.from(zonedDateTime);
    }
}
