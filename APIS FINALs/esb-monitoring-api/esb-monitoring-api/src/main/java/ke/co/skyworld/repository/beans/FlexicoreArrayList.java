package ke.co.skyworld.repository.beans;



import ke.co.skyworld.utils.Misc;

import java.util.ArrayList;

public class FlexicoreArrayList extends ArrayList<FlexicoreHashMap> {
    private String tableName = "[Undeclared]";

    public FlexicoreArrayList(String tableName) {
        this.tableName = tableName;
    }

    public FlexicoreArrayList() {
    }

    public String getTableName() {
        return tableName;
    }

    public void addNewRecord(FlexicoreHashMap flexicoreHashMap) {
        super.add(flexicoreHashMap);
    }

    public FlexicoreHashMap getRecord(int i) {
        if (isEmpty()) return null;
        return super.get(i);
    }

    public void printRecordsTabular() {
        if (!isEmpty()) {
            System.out.println("\nTABLE: " + tableName);
            System.out.println("********************************");
            get(0).forEach((field, value) -> {
                field = field.toUpperCase();
               // String parsedStr = field.replaceAll("(.{20})", "$1\n");
                System.out.printf("%-20s", Misc.abbreviateString(field,20));
            });
            this.forEach(flexicoreHashMap -> {
                System.out.println();
                flexicoreHashMap.forEach((field, value) -> {
                    if (value == null) {
                        value = "";
                        for (int i = 0; i < 20; i++) {
                            value += " ";
                        }
                    }
                    //String parsedStr = value.toString().replaceAll("(.{20})", "$1\n");
                    System.out.printf("%-20s", Misc.abbreviateString(value.toString(),20));
                });
            });
            System.out.println("\n********************************");
            System.out.println("TOTAL RECORDS = " + size() + "\n");

        } else {
            System.out.println("No records present");
        }
    }

    public void printRecordsVerticallyLabelled() {
        if (!isEmpty()) {
            System.out.println("\nTABLE: " + tableName);
            System.out.println("********************************");
            this.forEach(flexicoreHashMap -> {
                int longestField = flexicoreHashMap.getLongestField();
                flexicoreHashMap.forEach((field, value) -> {
                    if (value == null)value = " ";
                    System.out.printf("%" + longestField + "s: %s", field, value);
                    System.out.println();
                });
                System.out.println("--------------------------------------------------------");
            });
            System.out.println("********************************");
            System.out.println("TOTAL RECORDS = " + size() + "\n");
        } else {
            System.out.println("No records present");
        }
    }

    /*public int getLongestField() {
        AtomicInteger max = new AtomicInteger(0);
        forEach(flexicoreHashMap -> {
            int ll= flexicoreHashMap.getLongestField();
            if (ll> max.get()) max.set(ll);
        });
        return max.get();
    }*/


    public void printColumnsOnly() {
        if (!isEmpty()) {
            System.out.println("Table: " + tableName + "\n");
            get(0).forEach((field, value) -> System.out.println("field"));
        } else {
            System.out.println("No columns present");
        }
    }
}
