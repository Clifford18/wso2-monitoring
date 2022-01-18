public class RequestLog {
    private int request_id;
    private String request_reference;
    private String request_method;
    private String request_resource;
    private String request_parameters;
    private String request_headers;
    private String request_body;
    private String request_origin_ip;
    private String response_headers;
    private String response_body;
    private String error_code;

    public int getRequest_id() {
        return request_id;
    }

    public void setRequest_id(int request_id) {
        this.request_id = request_id;
    }

    public String getRequest_reference() {
        return request_reference;
    }

    public void setRequest_reference(String request_reference) {
        this.request_reference = request_reference;
    }

    public String getRequest_method() {
        return request_method;
    }

    public void setRequest_method(String request_method) {
        this.request_method = request_method;
    }

    public String getRequest_resource() {
        return request_resource;
    }

    public void setRequest_resource(String request_resource) {
        this.request_resource = request_resource;
    }

    public String getRequest_parameters() {
        return request_parameters;
    }

    public void setRequest_parameters(String request_parameters) {
        this.request_parameters = request_parameters;
    }

    public String getRequest_headers() {
        return request_headers;
    }

    public void setRequest_headers(String request_headers) {
        this.request_headers = request_headers;
    }

    public String getRequest_body() {
        return request_body;
    }

    public void setRequest_body(String request_body) {
        this.request_body = request_body;
    }

    public String getRequest_origin_ip() {
        return request_origin_ip;
    }

    public void setRequest_origin_ip(String request_origin_ip) {
        this.request_origin_ip = request_origin_ip;
    }

    public String getResponse_headers() {
        return response_headers;
    }

    public void setResponse_headers(String response_headers) {
        this.response_headers = response_headers;
    }

    public String getResponse_body() {
        return response_body;
    }

    public void setResponse_body(String response_body) {
        this.response_body = response_body;
    }

    public String getError_code() {
        return error_code;
    }

    public void setError_code(String error_code) {
        this.error_code = error_code;
    }

    public String getError_message() {
        return error_message;
    }

    public void setError_message(String error_message) {
        this.error_message = error_message;
    }

    public String getError_stacktrace() {
        return error_stacktrace;
    }

    public void setError_stacktrace(String error_stacktrace) {
        this.error_stacktrace = error_stacktrace;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public String getDate_modified() {
        return date_modified;
    }

    public void setDate_modified(String date_modified) {
        this.date_modified = date_modified;
    }

    private String error_message;
    private String error_stacktrace;
    private String date_created;
    private String date_modified;






}
