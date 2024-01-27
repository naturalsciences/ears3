package eu.eurofleets.ears3.Exceptions;

import java.util.Comparator;

public abstract class ImportException extends Exception {

    public String sheetName;
    /**
    * The row of the exception, header included. Identifies a cell together with
    * the column.
    */
    public int lineNb;
    public String message;

    public ImportException(String message) {
        super(message);
        this.message = message;
    }

    public ImportException(String message, Exception innerException) {
        super(message, innerException);
        this.message = message;
    }

    public abstract void print();

    public int compareTo(ImportException o) {
        return Comparator.comparing(ImportException::getSheetName)
                .thenComparing(ImportException::getMessage)
                .thenComparingInt(ImportException::getLineNb)
                .compare(this, o);
    }

    /**
     * @return the sheetName
     */
    public String getSheetName() {
        return sheetName;
    }

    /**
     * @return the lineNb
     */
    public int getLineNb() {
        return lineNb;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

}
