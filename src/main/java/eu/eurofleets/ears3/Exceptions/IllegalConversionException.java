
package eu.eurofleets.ears3.Exceptions;


public class IllegalConversionException extends Exception {

    public IllegalConversionException(Object value, String message, Object... formatArgs) {
        super(String.format(message, formatArgs));
    }

    
}
