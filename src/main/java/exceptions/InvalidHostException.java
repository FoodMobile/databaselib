package exceptions;

public class InvalidHostException extends Exception {
    @Override
    public String toString() {
        return "The host was either not provided or contained invalid information.";
    }
}
