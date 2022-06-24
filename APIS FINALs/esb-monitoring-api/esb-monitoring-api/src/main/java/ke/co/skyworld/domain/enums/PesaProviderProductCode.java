package ke.co.skyworld.domain.enums;

/**
 * skyworld-api (ke.co.skyworld.domain.enums)
 * Created by: elon
 * On: 15 Feb, 2021. 10:23
 **/
public enum PesaProviderProductCode {

    MPESA_C2B_PAYBILL("10101"),
    MPESA_B2C_PAYBILL("10102"),
    MPESA_B2B_PAYBILL("10103"),
    E_TOP_UP("10132");

    private String value;

    PesaProviderProductCode(String value){
        this.value = value;
    }

    public String value() {
        return value;
    }

}
