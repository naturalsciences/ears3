package eu.eurofleets.ears3.excel.converters;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import io.github.rushuat.ocell.field.ValueConverter;

/**
 * Converts an Excel time-of-day value to a "HH:mm:ss" string, regardless of
 * which raw form POI/ocell hands us for that cell:
 *  - a Date sitting on Excel's fake 1899-12-30/31 epoch (cell was formatted
 *    as a time in the workbook), or
 *  - a raw fractional Double serial (0 <= x < 1, fraction of a 24h day) when
 *    the cell wasn't recognized as a formatted date/time by POI.
 *
 * Scoped narrowly to time-only columns - do NOT reuse for generic numeric
 * fields, since a fractional double is only unambiguously "a time" in that
 * specific column context, not in general.
 */
public class ExcelTimeSerialConverter implements ValueConverter<String, Object> {

    // Excel's "day zero" for time-only serial values (fraction-of-a-day, no real date component)
    private static final LocalDate EXCEL_TIME_EPOCH = LocalDate.of(1899, 12, 31);
    // Some POI/timezone paths land one day earlier — treat that as the same sentinel
    private static final LocalDate EXCEL_TIME_EPOCH_ALT = LocalDate.of(1899, 12, 30);

    private static final SimpleDateFormat TIME_ONLY_FORMAT = new SimpleDateFormat("HH:mm:ss");

    @Override
    public String toModel(Object value) throws Exception {
        if (value == null) {
            return null;
        }
        try {
            if (value instanceof Date date) {
                LocalDateTime localDateTime = date.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();
                LocalDate localDate = localDateTime.toLocalDate();
                if (localDate.equals(EXCEL_TIME_EPOCH) || localDate.equals(EXCEL_TIME_EPOCH_ALT)) {
                    // genuine time-only value on Excel's fake epoch date
                    return TIME_ONLY_FORMAT.format(date);
                } else {
                    // Unexpected: a real calendar date landed in a Time column.
                    // Format the time-of-day portion only, rather than silently
                    // dropping the anomaly - lets validation/review catch it upstream.
                    return TIME_ONLY_FORMAT.format(date);
                }
            } else if (value instanceof Double dblValue) {
                if (dblValue >= 0 && dblValue < 1) {
                    long totalSeconds = Math.round(dblValue * 86400);
                    long hours = totalSeconds / 3600;
                    long minutes = (totalSeconds % 3600) / 60;
                    long seconds = totalSeconds % 60;
                    return String.format("%02d:%02d:%02d", hours, minutes, seconds);
                }
                // >= 1: a real date-plus-time serial, not a pure time-of-day value.
                // Not expected in a Time column, but don't silently mangle it -
                // pass through as-is so validation/review can flag the anomaly.
                return dblValue.toString();
            } else if (value instanceof String s) {
                String trimmed = s.trim();
                return trimmed.isEmpty() ? null : trimmed;
            } else {
                return value.toString();
            }
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Object toDocument(String value) throws Exception {
        throw new UnsupportedOperationException("Unimplemented method 'toDocument'");
    }
}