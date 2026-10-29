package util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * DateUtil — Date/time formatting helpers.
 */
public class DateUtil {

    private static final DateTimeFormatter DATE_FMT     = DateTimeFormatter.ofPattern("dd MMM yyyy");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
    private static final DateTimeFormatter SHORT_FMT    = DateTimeFormatter.ofPattern("dd MMM");

    private DateUtil() {}

    public static String formatDate(LocalDate date) {
        if (date == null) return "—";
        return date.format(DATE_FMT);
    }

    public static String formatDateTime(LocalDateTime dt) {
        if (dt == null) return "—";
        return dt.format(DATETIME_FMT);
    }

    public static String formatShort(LocalDate date) {
        if (date == null) return "—";
        return date.format(SHORT_FMT);
    }

    public static boolean isUpcoming(LocalDate date) {
        return date != null && !date.isBefore(LocalDate.now());
    }

    public static boolean isPast(LocalDate date) {
        return date != null && date.isBefore(LocalDate.now());
    }
}
