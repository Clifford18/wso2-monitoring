package ke.co.skyworld.repository.beans;

import java.util.ArrayList;
import java.util.List;

public class TransactionWrapper<T> {

    private boolean hasErrors;
    private List<String> errors;
    private List<String> errorsStackTrace;
    private T data;

    public TransactionWrapper() {
        this.hasErrors = false;
        this.errors = new ArrayList<>();
        this.errorsStackTrace = new ArrayList<>();
    }

    public TransactionWrapper(T boolData)
    {
        this();
        this.data = boolData;
    }

    public boolean hasErrors() {
        return hasErrors;
    }

    public void setHasErrors(boolean hasErrors) {
        this.hasErrors = hasErrors;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void addError(String error) {
        this.errors.add(error);
    }
    public void addErrorStackTrace(String errorStackTrace) {
        this.errorsStackTrace.add(errorStackTrace);
    }

    public String getErrors() {
        return errors.toString();
    }
    public String getErrorsStackTrace() {
        return errorsStackTrace.toString();
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public FlexicoreHashMap getSingleRecord() {
        if (data instanceof FlexicoreArrayList) {
            return ((FlexicoreArrayList) data).getRecord(0);
        } else if (data instanceof FlexicoreHashMap)
            return (FlexicoreHashMap) data;
        else if (data instanceof PageableWrapper)
            return ((PageableWrapper) data).getSingleRecord();
        return null;
    }

    @Override
    public String toString() {
        return "TransactionWrapper{" +
                "hasErrors=" + hasErrors +
                ", errors=" + errors +
                ", data=" + data +
                '}';
    }
}