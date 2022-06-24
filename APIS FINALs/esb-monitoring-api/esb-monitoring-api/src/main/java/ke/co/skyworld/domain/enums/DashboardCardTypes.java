package ke.co.skyworld.domain.enums;

/**
 * misc-8 (ke.co.scedar.jdbc_introspection)
 * Created by: elon
 * On: 20 Aug, 2019 8/20/19 12:13 PM
 **/
public enum DashboardCardTypes {

    CREDIT("CREDIT"),
    PESA_PRODUCT("PESA_PRODUCT"),
    PESA_STATISTIC("PESA_STATISTIC"),
    MSG_STATISTIC("MSG_STATISTIC"),
    STATISTIC("STATISTIC");

    private String value;

    DashboardCardTypes(String value){
        this.value = value;
    }

    public String value() {
        return value;
    }
}
