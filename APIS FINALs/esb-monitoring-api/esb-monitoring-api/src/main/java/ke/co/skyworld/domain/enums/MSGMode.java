package ke.co.skyworld.domain.enums;

public enum MSGMode {
    SAF("SAF"),
    EXPRESS("EXPRESS");

    private String value;

    MSGMode(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
