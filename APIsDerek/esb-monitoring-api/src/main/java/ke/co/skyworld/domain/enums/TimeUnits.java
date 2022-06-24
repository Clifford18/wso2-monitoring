package ke.co.skyworld.domain.enums;

public enum TimeUnits {
    MILLISECONDS("MILLISECONDS"),
    SECONDS("SECONDS"),
    MINUTES("MINUTES"),
    HOURS("HOURS");

    private String value;

    private TimeUnits(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }

    public static TimeUnits parse(String value) throws Exception {
        TimeUnits[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            TimeUnits timeUnit = var1[var3];
            if (timeUnit.value().equalsIgnoreCase(value)) {
                return timeUnit;
            }
        }

        throw new Exception("Time Unit '" + value + "' is NOT supported");
    }
}