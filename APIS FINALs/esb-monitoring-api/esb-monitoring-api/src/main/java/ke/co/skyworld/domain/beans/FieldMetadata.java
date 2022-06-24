package ke.co.skyworld.domain.beans;

import ke.co.skyworld.domain.enums.DataType;

/**
 * flexicore (ke.co.skyworld.utilities.beans)
 * Created by: elon
 * On: 26 Aug, 2019 8/26/19 10:17 AM
 **/
public class FieldMetadata implements Comparable<FieldMetadata> {

    private String name;
    private String newName;
    private DataType dataType;
    private String size;
    private boolean primaryKey;
    private boolean unique;
    private boolean indexed;
    private boolean required;
    private String defaultValue;
    private boolean autoIncrement;
    private boolean signed;
    private String enumValues;
    private String indexName;
    private String foreignKey;
    private String foreignKeyName;
    private boolean immutable;

    public FieldMetadata() {
        primaryKey = false;
        unique = false;
        indexed = false;
        required = false;
        defaultValue = "";
        autoIncrement = false;
        signed = false;
        immutable = false;
        name = "";
        newName = "";
        size = "";
        enumValues = "";
        indexName = "";
        foreignKey = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public String getSize() {

        if(dataType.equals(DataType.ENUM)) return "70";
        if(dataType.equals(DataType.BIGINT)) return "50";
        if(dataType.equals(DataType.INT)) return "40";
        if(dataType.equals(DataType.DATETIME)) return "40";

        //return size;
        return "70";
    }

    public void setSize(String size) {
        this.size = size;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    public boolean isIndexed() {
        return indexed;
    }

    public void setIndexed(boolean indexed) {
        this.indexed = indexed;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    public void setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

    public boolean isSigned() {
        return signed;
    }

    public void setSigned(boolean signed) {
        this.signed = signed;
    }

    public String getEnumValues() {
        return enumValues;
    }

    public void setEnumValues(String enumValues) {
        this.enumValues = enumValues;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public String getForeignKey() {
        return foreignKey;
    }

    public void setForeignKey(String foreignKey) {
        this.foreignKey = foreignKey;
    }

    public String getForeignKeyName() {
        return foreignKeyName;
    }

    public void setForeignKeyName(String foreignKeyName) {
        this.foreignKeyName = foreignKeyName;
    }

    public boolean isImmutable() {
        return immutable;
    }

    public void setImmutable(boolean immutable) {
        this.immutable = immutable;
    }

    @Override
    public String toString() {
        return "FieldMetadata {\n" +
                "\t name='" + name + '\'' +
                "\t newName='" + newName + '\'' +
                ",\n\t dataType=" + dataType +
                ",\n\t size=" + size +
                ",\n\t unique=" + unique +
                ",\n\t indexed=" + indexed +
                ",\n\t required=" + required +
                ",\n\t primaryKey=" + primaryKey +
                ",\n\t defaultValue='" + defaultValue + '\'' +
                ",\n\t autoIncrement=" + autoIncrement +
                ",\n\t signed=" + signed +
                ",\n\t enumValues='" + enumValues + '\'' +
                ",\n\t indexName='" + indexName + '\'' +
                ",\n\t foreignKey='" + foreignKey + '\'' +
                ",\n\t foreignKeyName='" + foreignKeyName + '\'' +
                ",\n\t immutable='" + immutable + '\'' +
                "\n}\n\n";
    }

    @Override
    public int compareTo(FieldMetadata fieldMetadata) {
        if(name.equalsIgnoreCase(fieldMetadata.getName()))
            return 0;
        else if(!name.equalsIgnoreCase(fieldMetadata.getName()))
            return 1;
        else return -1;
    }
}
