 package eu.eurofleets.ears3.utilities;
 
 import java.text.DateFormat;
 import java.text.ParseException;
 import java.text.SimpleDateFormat;
 import java.util.Date;
 
 
 public class DateUtilities
 {
   public static final String DATE_FORMAT_AS_STRING = "yyyy-MM-dd'T'HH:mm:ss";
   public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
   
   public static Date parseDate(String dateAsString) throws ParseException {
     return DATE_FORMAT.parse(dateAsString);
   }
   
   public static String formatDate(Date date) {
     return DATE_FORMAT.format(date);
   }
 }