package eu.eurofleets.ears3.Exceptions;

public class IllegalCellValueCombinationException extends ImportException {
    public IllegalCellValueCombinationException(String message, Object... formatArgs) {
        super(String.format(message, formatArgs));
    }

    @Override
    public void print() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'print'");
    }
}
