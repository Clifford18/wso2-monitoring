package ke.co.skyworld.domain.enums;

public enum RequestStatus {
    NONE("NONE"),
    RECEIVED("RECEIVED"),
    AUTHENTICATION_FAILED("FALSE"),
    ACCEPTED("ACCEPTED"),
    ACCESS_DENIED("ACCESS_DENIED"),
    INSUFFICIENT_RIGHTS("INSUFFICIENT_RIGHTS"),
    PROCESSING("PROCESSING"),
    ERROR("ERROR"),
    CANCELLED("CANCELLED"),
    SUCCESS("SUCCESS"),
    FAILED("FAILED");

    private String value;

    RequestStatus(String value){
        this.value = value;
    }

    public String value(){
        return value;
    }
}