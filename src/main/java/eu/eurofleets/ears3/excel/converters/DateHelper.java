package eu.eurofleets.ears3.excel.converters;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import jakarta.transaction.Transactional;

//import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import eu.eurofleets.ears3.Exceptions.IllegalCellValueCombinationException;
import eu.eurofleets.ears3.Exceptions.IllegalConversionException;
import eu.eurofleets.ears3.Exceptions.IllegalDateConversionException;

/**
 * Entire Class taken from Dasa-import from Thomas
 */

@Component
@Transactional
//@Slf4j
public class DateHelper implements Serializable {

    private static Logger logger = Logger.getLogger(DateHelper.class.getName());

    public static DateFormat ISO_FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    public static final ZoneId BRUSSELS = ZoneId.of("Europe/Brussels"); // Zone information

    /**
     * Provided a date in a String format and a List of formatters, looping over
     * those formatters, return the first LocalDate that those formatters can
     * legally parse.
     *
     * @param date
     * @param formatters
     * @return
     */
    private static LocalDate iterateParseDate(String date, List<DateTimeFormatter> formatters) {
        for (DateTimeFormatter formatter : formatters) {
            try {
                LocalDate localDate = LocalDate.parse(date, formatter);
                return localDate;
            } catch (Exception e) {
                continue;
            }
        }
        return null;
    }

    /**
     * Provided a date in a String format and a List of formatters, looping over
     * those formatters, return the first LocalTime that those formatters can
     * legally parse.
     *
     * @param time
     * @param formatters
     * @return
     */
    private static LocalTime iterateParseTime(String time, List<DateTimeFormatter> formatters) {
        for (DateTimeFormatter formatter : formatters) {
            try {
                LocalTime localTime = LocalTime.parse(time, formatter);
                return localTime;
            } catch (Exception e) {
                continue;
            }
        }
        return null;
    }

    /**
     * Convert a given date String to a LocalDate. Different formats are legal.
     *
     * @param date
     * @return
     * @throws IllegalConversionException
     */
    public static LocalDate dateStringToLocalDate(String date) throws IllegalDateConversionException {
        if (date == null) {
            return null;
        }
        date = date.trim();
        try {
            return LocalDate.ofInstant(ISO_FORMATTER.parse(date).toInstant(), BRUSSELS);
        } catch (ParseException e) {
            logger.info("Date not parseable with ISO_FORMATTER. Continuing with other approaches");
        }

        date = date.replaceAll("\\.0$", ""); //integers formatted as doubles eg. 2019.0
        Pattern p = Pattern.compile("\\d{4}");

        if (date.length() == 4 && p.matcher(date).matches()) {// meaning it is just a year.
            date = date + "/01/01";
            return ddmmyyyDateStringToLocalDate(date);
        } else if (date.length() == 8) { // meaning it is just a month of a year
            date = date.replace("-", "/");
            date = date + "/01";
            return ddmmyyyDateStringToLocalDate(date);
        } else {
            return ddmmyyyDateStringToLocalDate(date);
        }
    }

    /**
     * Combine two strings (one date, one time) to a ZonedDateTime (used as a timestamp), zoned for Belgium.
     * The date and time are interpreted as being in {@code sourceZone}, then converted to Europe/Brussels time.
     * ISO formatted date (yyyy-MM-dd) and time (HH:mm:ss) strings are expected.
     *
     * @param date
     * @param time
     * @param sourceZone the zone the incoming date/time strings are expressed in (e.g. BRUSSELS or ZoneOffset.UTC)
     * @return
     * @throws IllegalDateConversionException
     */
    public static ZonedDateTime dateTimeStringToZonedDateTime(String date, String time, ZoneId sourceZone)
            throws IllegalDateConversionException {
        if (date == null) {
            return null;
        }
        date = date.trim();

        // validate/normalize the date part using the existing helper
        LocalDate localDate = dateStringToLocalDate(date);

        String timePart = (time != null) ? time.trim() : "00:00:00";

        LocalDateTime localDateTime;
        try {
            localDateTime = LocalDateTime.parse(localDate + "T" + timePart);
        } catch (DateTimeParseException e) {
            throw new IllegalDateConversionException(date + " " + time, "Could not parse date/time");
        }

        // Interpret the naive datetime in the given source zone, then convert to Brussels time
        return localDateTime.atZone(sourceZone);//.withZoneSameInstant(BRUSSELS);
    }

    /**
     * Convenience overload assuming the incoming date/time strings are already Belgian local time
     * (the common case). Use the {@link #dateTimeStringToZonedDateTime(String, String, ZoneId)}
     * overload directly for the rare case where the source is UTC (or another zone).
     */
//    public static ZonedDateTime dateTimeStringToZonedDateTime(String date, String time)
//            throws IllegalDateConversionException {
//        return dateTimeStringToZonedDateTime(date, time, BRUSSELS);
//    }


    private static LocalDate ddmmyyyDateStringToLocalDate(String date) throws IllegalDateConversionException {
        if (date == null) {
            return null;
        }
        date = date.trim();

        if (date.length() == 10) { // meaning it is a full date
            DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            DateTimeFormatter formatter3 = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            DateTimeFormatter formatter4 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            List<DateTimeFormatter> parsers = new ArrayList<>();
            parsers.add(formatter1);
            parsers.add(formatter2);
            parsers.add(formatter3);
            parsers.add(formatter4);
            return iterateParseDate(date, parsers);
        } else {
            throw new IllegalDateConversionException(date,
                    "Date value '%s' can't be conversed to a real date. It must contain 10 characters.", date);
        }
    }

    private static LocalTime hhmmTimeStringToLocalTime(String time) {
        if (time == null) {
            return null;
        }
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("HH:mm:ss");
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("HH:mm");
        List<DateTimeFormatter> parsers = new ArrayList<>();
        parsers.add(formatter1);
        parsers.add(formatter2);
        return iterateParseTime(time, parsers);
    }

    public static boolean validateDates(String startDate, String endDate, String oneOfBothNull, String endBeforeStart)
            throws IllegalCellValueCombinationException {
        if (startDate == null ^ endDate == null) {
            throw new IllegalCellValueCombinationException(
                    oneOfBothNull);
        }
        if (startDate != null && endDate != null
                && startDate.compareTo(endDate) > 0) {
            throw new IllegalCellValueCombinationException(
                    endBeforeStart);
        } else
            return true;
    }

}
