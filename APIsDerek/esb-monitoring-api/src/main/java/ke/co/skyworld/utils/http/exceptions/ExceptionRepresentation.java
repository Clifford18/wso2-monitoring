package ke.co.skyworld.utils.http.exceptions;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import io.undertow.util.HttpString;
import ke.co.skyworld.utils.DateTime;
import ke.co.skyworld.utils.security.UID;

import java.util.Date;
import java.util.List;

@JacksonXmlRootElement(
        namespace = "ke.co.scedar.utils.http.exceptions.ExceptionRepresentation",
        localName = "Exception"
)
public class ExceptionRepresentation {

    private String id;
    private Date timestamp;
    private Integer status;
    private String error;
    private String errorsStackTrace;
    private List<String> errors;
    private String message;
    private String path;
    private String method;

    public ExceptionRepresentation() {
        this.id = new UID().getScedarUUID();
        this.timestamp = DateTime.getCurrentJavaUtilDateTime();
        this.status = 500;
        this.error = "Unknown Server Error";
        this.message = "Internal Server Error";
        this.path = "/";
    }

    public ExceptionRepresentation(String message, String path){
        this.id = new UID().getScedarUUID();
        this.timestamp = DateTime.getCurrentJavaUtilDateTime();
        this.status = 500;
        this.error = "Unknown Server Error";
        this.message = message;
        this.path = path;
    }

    public ExceptionRepresentation(String message, String path, String error){
        this.id = new UID().getScedarUUID();
        this.timestamp = DateTime.getCurrentJavaUtilDateTime();
        this.status = 500;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public ExceptionRepresentation(String message, String path, String error, Integer status, HttpString method){
        this.id = new UID().getScedarUUID();
        this.timestamp = DateTime.getCurrentJavaUtilDateTime();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.method = method.toString();
    }

    public ExceptionRepresentation(Date timestamp,
                                   Integer status,
                                   String error,
                                   String message,
                                   String path) {
        this.id = new UID().getScedarUUID();
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public ExceptionRepresentation(String message, String path, String error,String errorsStackTrace, Integer status, HttpString method){
        this.id = new UID().getScedarUUID();
        this.timestamp = DateTime.getCurrentJavaUtilDateTime();
        this.status = status;
        this.error = error;
        this.errorsStackTrace = errorsStackTrace;
        this.message = message;
        this.path = path;
        this.method = method.toString();
    }

    public List<String> getErrors() {
        return errors;
    }

    public ExceptionRepresentation setErrors(List<String> errors) {
        this.errors = errors;
        return this;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getErrorsStackTrace() {
        return errorsStackTrace;
    }

    public void setErrorsStackTrace(String errorsStackTrace) {
        this.errorsStackTrace = errorsStackTrace;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public String toString() {
        return "ExceptionRepresentation{" +
                "timestamp=" + timestamp +
                ", status=" + status +
                ", error='" + error + '\'' +
                ", message='" + message + '\'' +
                ", path='" + path + '\'' +
                ", method='" + method + '\'' +
                '}';
    }
}
