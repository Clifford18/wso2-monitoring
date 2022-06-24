package ke.co.skyworld.repository.exceptions;

import java.sql.SQLException;

public class QueryBuilderException extends SQLException {
    public QueryBuilderException(String s, String s1, int i) {
        super(s, s1, i);
    }

    public QueryBuilderException(String s, String s1) {
        super(s, s1);
    }

    public QueryBuilderException(String s) {
        super(s);
    }

    public QueryBuilderException() {
    }

    public QueryBuilderException(Throwable throwable) {
        super(throwable);
    }

    public QueryBuilderException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public QueryBuilderException(String s, String s1, Throwable throwable) {
        super(s, s1, throwable);
    }

    public QueryBuilderException(String s, String s1, int i, Throwable throwable) {
        super(s, s1, i, throwable);
    }
}
