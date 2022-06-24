package ke.co.skyworld.repository.beans;

public class Column {
    private String columnName;
    private String columnAlias;
    private SQLEnums.Aggregate aggregate;

    public Column(String columnName, String columnAlias, SQLEnums.Aggregate aggregate) {
        this.columnName = columnName;
        this.columnAlias = columnAlias;
        this.aggregate = aggregate;
    }

    public Column(String columnName, SQLEnums.Aggregate aggregate) {
        this.columnName = columnName;
        this.aggregate = aggregate;
    }

    public Column(String columnName, String columnAlias) {
        this.columnName = columnName;
        this.columnAlias = columnAlias;
    }

    public Column(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getColumnAlias() {
        return columnAlias;
    }

    public SQLEnums.Aggregate getAggregate() {
        return aggregate;
    }
}
