package ke.co.skyworld.domain.enums;

/**
 * kukuza-api (ke.co.scedar.domain.db.enums)
 * Created by: elon
 * On: 16 Aug, 2018 8/16/18 8:00 PM
 **/
public enum UserTypes {

    ROOT("ROOT"),
    ADMIN("ADMIN"),
    PARTNER("PARTNER"),
    MEMBER("MEMBER"),
    DEFAULT("DEFAULT"),
    STANDARD_USER("STANDARD_USER"),
    NONE("NONE");

    private String value;

    UserTypes(String value){
        this.value = value;
    }

    public String value() {
        return value;
    }

}
