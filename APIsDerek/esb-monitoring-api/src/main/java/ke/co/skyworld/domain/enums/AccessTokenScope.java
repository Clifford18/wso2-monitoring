package ke.co.skyworld.domain.enums;

public enum AccessTokenScope {
    PRE_ACCESS("PRE_ACCESS"),
    GLOBAL("GLOBAL");

    private String value;

    AccessTokenScope(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
