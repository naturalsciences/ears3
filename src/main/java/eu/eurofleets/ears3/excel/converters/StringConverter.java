package eu.eurofleets.ears3.excel.converters;

import java.util.Date;

import io.github.rushuat.ocell.field.ValueConverter;

/**
 * OCell tries to convert everything to the format it thinks is native. We return everything as Strings, AS DISPLAYED IN EXCEL. 
 * Further processing (and validation) is deferred to the convert methods of ImportModelConverter.
 */
public class StringConverter implements ValueConverter<String, Object> {

    @Override
    public String convertInput(Object value) {
        String result;
        if (value == null) {
            return null;
        }
        try {
            if (value instanceof Date) {
                Date date = (Date) value;
                result = DateHelper.ISO_FORMATTER.format(date);
            } else if (value instanceof String) {
                String stringValue = ((String) value).trim(); //trim everything
                if (stringValue.isEmpty() || stringValue.isBlank()) {
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
    public Object convertOutput(String value) {
        return value.toString();
    }
}
