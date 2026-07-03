
package eu.eurofleets.ears3.excel.converters;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import io.github.rushuat.ocell.field.ValueConverter;

/**
 * Class taken from Thomas Dasa-import
 * OCell tries to convert everything to the format it thinks is native. We return everything as Strings, AS DISPLAYED IN EXCEL.
 * Further processing (and validation) is deferred to the convert methods of ImportModelConverter.
 */

public class StringConverter implements ValueConverter<String, Object> {

    // Excel's "day zero" for time-only serial values (fraction-of-a-day, no real date component)
    private static final LocalDate EXCEL_TIME_EPOCH = LocalDate.of(1899, 12, 31);
    // Some POI/timezone paths land one day earlier — treat that as the same sentinel
    private static final LocalDate EXCEL_TIME_EPOCH_ALT = LocalDate.of(1899, 12, 30);

    private static final DateTimeFormatter TIME_ONLY_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm:ss");

    private static final SimpleDateFormat TIME_ONLY_FORMAT = new SimpleDateFormat("HH:mm:ss");

    public String convertInput(Object value) {
        String result;
        if (value == null) {
            return null;
        }
        try {
            if (value instanceof Date) {
                Date date = (Date) value;
                LocalDateTime localDateTime = date.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();
                LocalDate localDate = localDateTime.toLocalDate();
                if (localDate.equals(EXCEL_TIME_EPOCH) || localDate.equals(EXCEL_TIME_EPOCH_ALT)) {
                    // this is really a time-only Excel value; ignore the fake epoch date entirely
                    result = TIME_ONLY_FORMAT.format(date);
                } else {
                    // genuine date (or date+time) value
                    result = DateHelper.ISO_FORMATTER.format(date);
                }
            } else if (value instanceof String) {
                String stringValue = ((String) value).trim();
                if (stringValue.isEmpty()) {
                    return null;
                }
                result = stringValue;
            } else if (value instanceof Double) {
                Double dblValue = (double) value;
                if ((dblValue % 1) == 0) { //doubles that are effectively eg 5463.0000 should be set as an int.
                    result = Long.valueOf((long) dblValue.doubleValue()).toString(); //long as int can't do large values
                } else {
                    result = value.toString();
                }
            } else {
                result = value.toString();
            }
        } catch (Exception e) {
            result = null;
        }
        return result;
    }

    @Override
    public String toModel(Object value) throws Exception {
        return convertInput(value);
    }

    @Override
    public Object toDocument(String value) throws Exception {
        throw new UnsupportedOperationException("Unimplemented method 'toDocument'");
    }
}
