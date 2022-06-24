package ke.co.skyworld.domain.enums;

public enum OTPStatus {
    VALID("VALID"),
    INVALID("INVALID"),
    EXPIRED("EXPIRED");

    private String value;

    OTPStatus(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
