package ke.co.skyworld.domain.beans;


import ke.co.skyworld.domain.enums.RequestStatus;
import ke.co.skyworld.utils.http.exceptions.ExceptionRepresentation;

/**
 * my-airtime-api (ke.co.skyworld.api.rest.beans)
 * Created by: elon
 * On: 27 Apr, 2020. 03:58
 **/
public class WorkflowFormValidation {

    private boolean formIsValid;
    private ExceptionRepresentation exceptionRepresentation;
    private int statusCode;
    private RequestStatus requestStatus;

    public WorkflowFormValidation() {
        formIsValid = false;
    }

    public WorkflowFormValidation(ExceptionRepresentation exceptionRepresentation,
                                  int statusCode, RequestStatus requestStatus, boolean formIsValid){
        this.exceptionRepresentation = exceptionRepresentation;
        this.statusCode = statusCode;
        this.requestStatus = requestStatus;
        this.formIsValid = formIsValid;
    }

    public boolean isFormIsValid() {
        return formIsValid;
    }

    public void setFormIsValid(boolean formIsValid) {
        this.formIsValid = formIsValid;
    }

    public ExceptionRepresentation getExceptionRepresentation() {
        return exceptionRepresentation;
    }

    public void setExceptionRepresentation(ExceptionRepresentation exceptionRepresentation) {
        this.exceptionRepresentation = exceptionRepresentation;
    }

    public RequestStatus getRequestStatus() {
        return requestStatus;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setRequestStatus(RequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }
}
