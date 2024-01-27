package eu.eurofleets.ears3.excel.converters;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.transaction.Transactional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import eu.eurofleets.ears3.Exceptions.IllegalCellValueCombinationException;
import eu.eurofleets.ears3.Exceptions.IllegalConversionException;
import eu.eurofleets.ears3.Exceptions.IllegalDateConversionException;

@Component
@Transactional
@Slf4j
public class DateHelper implements Serializable {

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
            log.info("Date not parseable with ISO_FORMATTER. Continuing with other approaches");
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
     * Convert a string that contains both the date and the time to a ZonedDateTime. ISO formatted dates can be used.
     * @param datetime
     * @return
     * @throws IllegalDateConversionException
     */
    public static ZonedDateTime dateTimeStringToZonedDateTime(String datetime, boolean acceptJustDates)
            throws IllegalDateConversionException {
        if (datetime == null) {
            return null;
        }
        datetime = datetime.trim();
        try {
            LocalDateTime localDateTime = LocalDateTime.ofInstant(ISO_FORMATTER.parse(datetime).toInstant(), BRUSSELS);
            return localDateTime.atZone(BRUSSELS);
        } catch (ParseException e) {
            log.info("Date not parseable with ISO_FORMATTER. Continuing with other approaches");
        }

        String[] split = datetime.split(" ");
        if (!acceptJustDates && split.length == 1) { //if we only accept datetimes, not just dates but only a date is provided
            throw new IllegalDateConversionException(datetime,
                    "Date %s not consisting of two parts dd/MM/YYYY and hh:mm:ss", datetime);
        } else if (acceptJustDates) { //if just dates are ok and 
            return dateTimeStringToZonedDateTime(split[0], "00:00:00"); //just pick midnight
        } else {
            return dateTimeStringToZonedDateTime(split[0], split[1]);
        }
    }

    /**
    * Combine two strings (one date, one time) to a ZonedDateTime (used as a timestamp). ISO formatted dates can be used.
    * @param date
    * @param time
    * @return
    * @throws IllegalDateConversionException
    */
    public static ZonedDateTime dateTimeStringToZonedDateTime(String date, String time)
            throws IllegalDateConversionException {
        if (date == null) {
            return null;
        }
        date = date.trim();
        time = time.trim();
        LocalDate localDate = dateStringToLocalDate(date);
        LocalDateTime localDateTime = timeStringToLocalDateTime(time);
        localDate.atTime(localDateTime.toLocalTime());
        if (time != null) {
            LocalDateTime localTime = timeStringToLocalDateTime(time); //convert the time to a LocalDateTime with arbitrary date 2000-1-1
            localDateTime = localDate.atTime(localTime.toLocalTime());
        } else {
            LocalTime midnight = LocalTime.MIDNIGHT;
            localDateTime = localDate.atTime(midnight);
        }

        return localDateTime.atZone(BRUSSELS);
    }

    /**
     * Convert a String denoting the time to a ZonedDateTime (used as a timestamp). ISO formatted dates can be used, in that case the date part is ignored! hh:mm or hh:mm:ss can be used as well.
     * @param time
     * @return
     */
    private static LocalDateTime timeStringToLocalDateTime(String time) {
        if (time == null) {
            return null;
        }
        try {
            return LocalDateTime.ofInstant(ISO_FORMATTER.parse(time).toInstant(), BRUSSELS);
        } catch (ParseException e) {
            log.info("Date not parseable with ISO_FORMATTER. Continuing with other approaches");
        }
        LocalDate localDate = LocalDate.of(2000, 1, 1); //arbitrary time in the past
        LocalTime localDateTime = hhmmTimeStringToLocalTime(time);
        return localDate.atTime(localDateTime);
    }

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
