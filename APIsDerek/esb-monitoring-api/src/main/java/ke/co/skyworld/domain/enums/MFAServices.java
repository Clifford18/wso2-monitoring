package ke.co.skyworld.domain.enums;

public enum MFAServices {
    LOGIN("LOGIN"),
    GENERAL("GENERAL");

    private String value;

    MFAServices(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
