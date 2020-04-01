 package eu.eurofleets.ears2.utilities;
 
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


/* Location:              /home/thomas/Documents/Project-Eurofleets2/meetings/2016-11-03-04-workshop/VM/shared/ef_workshop/ears2.war!/WEB-INF/classes/eu/eurofleets/ears2/utilities/DateUtilities.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */