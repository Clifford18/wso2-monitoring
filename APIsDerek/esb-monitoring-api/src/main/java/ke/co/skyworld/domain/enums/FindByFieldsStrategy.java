package ke.co.skyworld.domain.enums;

/**
 * sls-api (ke.co.scedar.domain.beans)
 * Created by: elon
 * On: 06 Jul, 2018 7/6/18 7:11 PM
 **/
public enum FindByFieldsStrategy {

    AND(" and "), OR(" or ");

    private String value;

    FindByFieldsStrategy(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }

}
