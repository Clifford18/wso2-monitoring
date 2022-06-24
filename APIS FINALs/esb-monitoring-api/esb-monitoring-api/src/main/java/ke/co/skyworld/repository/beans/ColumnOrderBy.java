package ke.co.skyworld.repository.beans;

public class ColumnOrderBy extends Column {
    private SQLEnums.OrderBy orderBy;
    public ColumnOrderBy(String columnName, SQLEnums.Aggregate aggregate, SQLEnums.OrderBy orderBy) {
        super(columnName, aggregate);
        this.orderBy = orderBy;
    }

    public ColumnOrderBy(String columnName, SQLEnums.OrderBy orderBy) {
        super(columnName);
        this.orderBy = orderBy;
    }

    public SQLEnums.OrderBy getOrderBy() {
        return orderBy;
    }
}
