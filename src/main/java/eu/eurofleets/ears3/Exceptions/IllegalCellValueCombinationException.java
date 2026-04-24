package eu.eurofleets.ears3.Exceptions;

public class IllegalCellValueCombinationException extends Exception {
    public IllegalCellValueCombinationException(String message, Object... formatArgs) {
        super(String.format(message, formatArgs));
    }

}
