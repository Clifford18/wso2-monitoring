package ke.co.skyworld.domain.enums;

/**
 * sls-api (ke.co.scedar.domain.enums)
 * Created by: elon
 * On: 06 Jul, 2018 7/6/18 7:19 PM
 **/
public enum Relations {

    EQ("eq"),
    LT("lt"),
    LTE("lte"),
    GT("gt"),
    GTE("gte"),
    LIKE("like"),
    CONTAINS("contains"),
    SW("sw"),
    EW("ew"),
    IN("in"),
    NEQ("!eq"),
    NNEQ("!neq"),
    NLT("!lt"),
    NLTE("!lte"),
    NGT("!gt"),
    NGTE("!gte"),
    NLIKE("!like"),
    NCONTAINS("!contains"),
    NSW("!sw"),
    NEW("!ew"),
    NIN("!in");


    private final String value;

    Relations(String value){
        this.value = value;
    }

    public String value() {
        return value;
    }

    public String all(){
        return "eq,lt,lte,gt,gte,like,contains,sw,ew,in,!eq,!neq,!lt,!lte,!gt,!gte,!like,!contains,!sw,!ew,!in";
    }
}
