package ke.co.skyworld.domain.enums;

public enum ReturnValue {

    //Status
    SUCCESS("SUCCESS"),
    FAILED("FAILED"),
    ERROR("ERROR"),
    UNKNOWN("UNKNOWN"),

    //Booleans
    TRUE("TRUE"),
    FALSE("FALSE");

    private String value;

    ReturnValue(String value){
        this.value = value;
    }

    public String value(){
        return value;
    }

    public static ReturnValue parse(String value) {
        for (ReturnValue returnValue : ReturnValue.values()) {
            if (returnValue.value().equalsIgnoreCase(value)) {
                return returnValue;
            }
        }

        //Return UNKNOWN as default
        return ReturnValue.UNKNOWN;
    }
}
