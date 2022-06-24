package ke.co.skyworld.domain.enums;

/**
 * flexicore (ke.co.skyworld.utilities.enums)
 * Created by: elon
 * On: 18 Jul, 2019 18/07/19 13:50
 **/
public enum LoggingLevel {

    DEBUG("DEBUG"),
    INFO("INFO"),
    WARNING("WARNING"),
    ERROR("ERROR");

    private String value;

    LoggingLevel(String value){
        this.value = value;
    }

    public String value(){
        return value;
    }

    public static LoggingLevel parse(String value) {
        for (LoggingLevel loggingLevel : LoggingLevel.values()) {
            if (loggingLevel.value().equalsIgnoreCase(value)) {
                return loggingLevel;
            }
        }

        //Return INFO as default
        return LoggingLevel.INFO;
    }

}