
package eu.eurofleets.ears3.Exceptions;

public class IllegalDateConversionException extends Exception {

    public IllegalDateConversionException(Object value, String message, Object... formatArgs) {
        super(String.format(message, formatArgs));
    }

}
