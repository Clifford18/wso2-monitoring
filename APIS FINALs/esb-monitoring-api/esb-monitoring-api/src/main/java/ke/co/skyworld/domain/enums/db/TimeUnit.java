package ke.co.skyworld.domain.enums.db;

/**
 * skyworld-api (ke.co.skyworld.domain.enums.db)
 * Created By: elon
 * On: 24 Jan, 2019 10:03 PM
 **/

public enum TimeUnit {

    SECONDS("s"),
    MINUTES("m"),
    HOURS("h"),
    DAYS("d");

    private String value;

    TimeUnit(String value){
        this.value = value;
    }

    public String value() {
        return value;
    }

}
