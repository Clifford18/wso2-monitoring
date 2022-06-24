package ke.co.skyworld.domain.enums.db;

/**
 * skyworld-api (ke.co.skyworld.domain.enums.db)
 * Created By: elon
 * On: 24 Jan, 2019 10:11 PM
 **/

public enum AllowedAccessSourcesMatchType {

    STRING("STRING"),
    REGEX("REGEX");

    private String value;

    AllowedAccessSourcesMatchType(String value){
        this.value = value;
    }

    public String value(){
        return value;
    }

}
