package eu.eurofleets.ears3.utilities;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtilities {

    public static final String DATE_FORMAT_AS_STRING = "yyyy-MM-dd'T'HH:mm:ss";
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    public static Date parseDate(String dateAsString) throws ParseException {
        return DATE_FORMAT.parse(dateAsString);
    }

    public static String formatDate(Date date) {
        return DATE_FORMAT.format(date);
    }

    /*** 
     * Converts a yyyy-MM-dd HH:mm:ss date String to an offsetDateTime
     * @return 
     */
    public static OffsetDateTime toOffsetDate(String date) {
        ZoneId zoneId = ZoneId.of("UTC");  
        ZoneId defaultZone = ZoneId.systemDefault();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(date, formatter);

        ZoneOffset offset = zoneId.getRules().getOffset(dateTime);

        OffsetDateTime offsetDateTime = OffsetDateTime.of(dateTime, offset);
        return offsetDateTime;
    }
}
