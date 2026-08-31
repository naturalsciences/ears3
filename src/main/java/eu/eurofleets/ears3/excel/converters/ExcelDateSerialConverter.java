package eu.eurofleets.ears3.excel.converters;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

import io.github.rushuat.ocell.field.ValueConverter;

/**
 * Converts an Excel date value to a "yyyy-MM-dd" string, regardless of
 * which raw form POI/ocell hands us for that cell:
 *  - a Date whose time-of-day is incidental (POI always attaches a
 *    time-of-day to a date cell, even when the workbook only shows a date -
 *    usually midnight, but not guaranteed), or
 *  - a raw Double serial (whole or fractional number of days since Excel's
 *    epoch) when the cell wasn't recognized as a formatted date by POI.
 *
 * Only the calendar date is kept here; any time-of-day riding along with it
 * is dropped, since date columns are never the source of truth for time -
 * that always comes from a separate Time column/field.
 *
 * Scoped narrowly to date-only columns - do NOT reuse for generic numeric
 * fields, since a Double serial is only unambiguously "a date" in that
 * specific column context, not in general.
 */
public class ExcelDateSerialConverter implements ValueConverter<String, Object> {

    // Excel's epoch for date serials (day 0 == 1899-12-30, with day 60 landing on
    // Excel's fake 1900-02-29 - this is the standard offset used to reproduce
    // Excel's own leap-year bug rather than a "correct" epoch).
    private static final LocalDate EXCEL_DATE_EPOCH = LocalDate.of(1899, 12, 30);

    private static final SimpleDateFormat DATE_ONLY_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public String toModel(Object value) throws Exception {
        if (value == null) {
            return null;
        }
        try {
            if (value instanceof Date date) {
                // Keep the calendar date only; any time-of-day is incidental for a date column.
                return DATE_ONLY_FORMAT.format(date);
            }
            if (value instanceof Double dblValue) {
                if (dblValue < 1) {
                    // A bare fraction-of-a-day serial looks like a Time value, not a Date.
                    // Not expected in a Date column, but don't silently mangle it -
                    // pass through as-is so validation/review can flag the anomaly.
                    return dblValue.toString();
                }
                // Whole-or-fractional days since the Excel epoch - keep the day,
                // drop any fractional part (that fraction would be time-of-day).
                long dayCount = (long) Math.floor(dblValue);
                return EXCEL_DATE_EPOCH.plusDays(dayCount).toString();
            }
            if (value instanceof String s) {
                String trimmed = s.trim();
                return trimmed.isEmpty() ? null : trimmed;
            }
            return value.toString();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Object toDocument(String value) throws Exception {
        throw new UnsupportedOperationException("Unimplemented method 'toDocument'");
    }
}
