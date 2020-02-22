package exceptions;

public class InvalidQueryType extends Exception {
    @Override
    public String toString() {
        return "The provided query does not match the selected adapter!";
    }
}
