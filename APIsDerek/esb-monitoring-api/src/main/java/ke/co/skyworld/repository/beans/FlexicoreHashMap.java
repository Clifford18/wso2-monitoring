package ke.co.skyworld.repository.beans;


import ke.co.skyworld.utils.Misc;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("unchecked")

public class FlexicoreHashMap extends LinkedHashMap<String, Object> {
    public FlexicoreHashMap() { }
    public FlexicoreHashMap(String tableName) {
        if (tableName != null) this.tableName = tableName;
    }
    public FlexicoreHashMap putValue(String columnname, Object value) {
        super.put(columnname, value);
        return this;
    }

    public FlexicoreHashMap addQueryArgument(String namedVariable, Object value) {
        super.put(namedVariable, value);
        return this;
    }

    public FlexicoreHashMap addQueryArguments(FlexicoreHashMap queryArguments) {
        super.putAll(queryArguments);
        return this;
    }

    private String tableName = "[Undeclared]";

    public String getTableName() {
        return tableName;
    }

    public FlexicoreHashMap setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public <T> T getValue(String columnName) {
        return (T) super.get(columnName);
    }

    public String getStringValue(String columnName) {
        if(super.get(columnName) == null) return null;
        return super.get(columnName).toString();
    }

    public String getStringOrEmptyValue(String columnName) {
        if(super.get(columnName) == null) return "";
        return super.get(columnName).toString();
    }

    public void removeColumn(String columnName) {
        super.remove(columnName);
    }

    public void printRecordTabular() {
        if (!isEmpty()) {
            System.out.println("\nTABLE: " + tableName);
            System.out.println("********************************");
            keySet().forEach(field -> {
                field = field.toUpperCase();
                //String parsedStr = field.replaceAll("(.{20})", "$1\n");
                System.out.printf("%-20s", Misc.abbreviateString(field, 20));
            });
            System.out.println();
            forEach((field, value) -> {
                if (value == null) {
                    value = "";
                    for (int i = 0; i < 19; i++) value += " ";
                }
                //String parsedStr = value.toString().replaceAll("(.{20})", "$1\n");
                System.out.printf("%-20s", Misc.abbreviateString(value.toString(), 20));
            });
            System.out.println("\n*******************************");
            System.out.println("TOTAL FIELDS = " + keySet().size() + "\n");
        } else {
            System.out.println("No record present");
        }
    }

    public void printRecordVerticalLabelled() {
        if (!isEmpty()) {
            System.out.println("\nTABLE: " + tableName);
            System.out.println("********************************");
            int longestField = getLongestField();
            forEach((field, value) -> {
                if (value == null)value = " ";
                System.out.printf("%" + longestField + "s: %s", field, value);
                System.out.println();
            });
            System.out.println("\n*******************************");
            System.out.println("TOTAL FIELDS = " + keySet().size() + "\n");
        } else {
            System.out.println("No record present");
        }
    }


    public int getLongestField() {
        AtomicInteger max = new AtomicInteger(0);
        forEach((field, value) -> {
            if (field.length() > max.get()) max.set(field.length());
        });
        return max.get();
    }

    public List<String> getAllColumns() {
        return new ArrayList<>(keySet());
    }

    public List<Object> getAllValues() {
        return new ArrayList(values());
    }

    public FlexicoreHashMap getSpecifiedColumnsCellValues(String[] columns) {
        FlexicoreHashMap flexicoreHashMap = new FlexicoreHashMap();
        for (String cname : columns) {
            flexicoreHashMap.put(cname, getValue(cname));
        }
        return flexicoreHashMap;
    }

    public void copyFrom(FlexicoreHashMap flexicoreHashMap)
    {
        for (Map.Entry<String, Object> entry : flexicoreHashMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            putValue(key,value);
        }
    }
}
