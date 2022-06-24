package ke.co.skyworld.repository.exceptions;

public class FilterException extends Exception {
    public FilterException() {
    }

    public FilterException(String s) {
        super(s);
    }

    public FilterException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public FilterException(Throwable throwable) {
        super(throwable);
    }

    public FilterException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
