package ke.co.skyworld.domain.enums;

public enum ActionType {
    NONE("NONE"),
    VIEW("VIEW"),
    CREATE("CREATE"),
    MODIFY("MODIFY"),
    DELETE("DELETE"),
    ROLLBACK_MODIFY("ROLLBACK_MODIFY"),
    ROLLBACK_DELETE("ROLLBACK_DELETE");
    private String value;

    ActionType(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
