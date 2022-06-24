package ke.co.skyworld.domain.enums;

/**
 * misc-8 (ke.co.scedar.jdbc_introspection)
 * Created by: elon
 * On: 20 Aug, 2019 8/20/19 12:13 PM
 **/
public enum DataType {

    TEXT("TEXT"),
    LONG_TEXT("LONG_TEXT"),
    INT("INT"),
    BIGINT("BIGINT"),
    DOUBLE("DOUBLE"),
    ENUM("ENUM"),
    DATETIME("DATETIME"),
    BLOB("BLOB");

    private String value;

    DataType(String value){
        this.value = value;
    }

    public String value() {
        return value;
    }

    public static DataType parse(String value) {
        for (DataType dataType : DataType.values()) {
            if (dataType.value().equalsIgnoreCase(value)) {
                return dataType;
            }
        }

        //Return MySQL as default
        return DataType.TEXT;
    }

}
