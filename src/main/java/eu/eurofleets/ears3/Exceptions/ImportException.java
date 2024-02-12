package eu.eurofleets.ears3.Exceptions;

import java.util.Comparator;

public class ImportException extends Exception {

    public String sheetName;
    /**
    * The row of the exception, header included. Identifies a cell together with
    * the column.
    */
    public int lineNb;
    public String message;

    public ImportException(String sheetName, int lineNb, String message, Exception innerException) {
        super(message, innerException);
        this.lineNb = lineNb;
        this.message = message;
        this.sheetName = sheetName;
    }

}
