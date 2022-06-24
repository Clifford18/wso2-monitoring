package ke.co.skyworld.repository.query;

import ke.co.skyworld.repository.Q;
import ke.co.skyworld.repository.beans.Column;
import ke.co.skyworld.repository.beans.ColumnOrderBy;
import ke.co.skyworld.repository.exceptions.QueryBuilderException;

import java.util.*;

@SuppressWarnings({"UnusedReturnValue", "WeakerAccess"})
public class QueryBuilder {
    private String query = "";
    private String joinStatement = "";
    private String whereClause = "";
    private String databaseName = "";
    private String tableName = "";
    private String primaryKeyColumn = "";
    private List<String> primaryKeyColumns = new ArrayList<>();
    private String[] fetchedColumns = {"*"};
    private String[] insertedValues = {""};
    private List<Column> fetchColumnsList = new ArrayList<>();
    //private List<String[]> insertValuesNamedVarsList = new ArrayList<>();
    private String[] insertColumns;

    /*--------------------------------START OF INSERT QUERIES---------------------------*/
    public QueryBuilder insert() {
        query += "INSERT INTO ";
        return this;
    }

    public QueryBuilder into(String tableName) {
        try {
            validateTableName(tableName, "INSERT: Table name is empty");
            this.databaseName = Q.getParentDatabase(tableName);
            this.tableName = tableName;
            query += databaseName+"."+tableName + " ";
        } catch (QueryBuilderException e) {
            e.printStackTrace();
        }
        return this;
    }

    public QueryBuilder columns(String[] columns) {
        try {
            validateNonEmptyStringArray(columns, "INSERT: insert column must not be empty if provided");
            insertColumns = columns;
            query += concatenateColumnNames(columns) + " ";
        } catch (QueryBuilderException e) {
            e.printStackTrace();
        }
        return this;
    }

    public QueryBuilder values(String[] values) {
        try {
            if (values.length < 1) throw new QueryBuilderException("INSERT: No insert values provided");
            for (String value : values) {
                if (!value.startsWith(":"))
                    throw new QueryBuilderException("INSERT: '" + value + "' named variable must start with full colon[:]");
            }

            if (insertColumns != null) {
                if (values.length != insertColumns.length)
                    throw new QueryBuilderException("INSERT: Columns and values do not match");
            }
            //insertValuesNamedVarsList.add(values);
            query += " VALUES " + concatenateColumnNames(values);
        } catch (QueryBuilderException e) {
            e.printStackTrace();
        }
        return this;
    }

    public QueryBuilder valuesFromSelect(String selectSubQuery) {
        query += "(" + selectSubQuery + ") ";
        return this;
    }

 /*   public Query insertValuesTermination() throws QueryBuilderException {
        if (insertedValues[0].isEmpty()) throw new QueryBuilderException("INSERT: Insert values missing in query: " + query);
        query += " VALUES " + insertedValues[0];
        return this;
    }*/

    /*--------------------------------START OF UPDATE QUERIES-------------------------------------*/
    public QueryBuilder update(String tableName) {
        try {
            validateTableName(tableName, "UPDATE: Table name is empty");
            this.databaseName = Q.getParentDatabase(tableName);
            this.tableName = tableName;
            query += "UPDATE " + databaseName+"."+tableName + " SET ";
        } catch (QueryBuilderException e) {
            e.printStackTrace();
        }
        return this;
    }

    public QueryBuilder set(HashMap<String, String> hashMap) {
        try {
            validateUpdateSet(hashMap);
            query += concatenateUpdateSet(hashMap);
        } catch (QueryBuilderException e) {
            e.printStackTrace();
        }

        return this;
    }

    /*public Query setTermination() throws QueryBuilderException {
        if (updatedValues[0].isEmpty()) throw new QueryBuilderException("UPDATE: Update set values missing in query: " + query);
        query += updatedValues[0];
        return this;
    }
*/
    /*--------------------------------START OF DELETE QUERIES-------------------------------------*/
    public QueryBuilder deleteFrom(String tableName) {
        try {
            validateTableName(tableName, "DELETE: Table name is empty");
            this.databaseName = Q.getParentDatabase(tableName);
            this.tableName = tableName;
            query += "DELETE FROM " + databaseName+"."+tableName + " ";
        } catch (QueryBuilderException e) {
            e.printStackTrace();
        }
        return this;
    }

    /*--------------------------------START OF SELECT QUERIES-------------------------------------*/
    public QueryBuilder select() {
        query += "SELECT ";
        return this;
    }

    public QueryBuilder rawQuery(String rawQuery) {
        query += rawQuery + " ";
        return this;
    }

    public QueryBuilder selectColumn(Column column) {
        fetchColumnsList.add(column);
        fetchedColumns[0] = concatenateColumnNames();
        return this;
    }

    public QueryBuilder selectColumns(Column... columns) {
        fetchColumnsList.addAll(Arrays.asList(columns));
        fetchedColumns[0] = concatenateColumnNames();
        return this;
    }

    public QueryBuilder selectColumns(List<Column> columns) {
        fetchColumnsList.addAll(columns);
        fetchedColumns[0] = concatenateColumnNames();
        return this;
    }

    public QueryBuilder selectColumns(String... columns) {

        for(String column : columns){
            fetchColumnsList.add(new Column(column));
        }
        fetchedColumns[0] = concatenateColumnNames();
        return this;
    }

    public QueryBuilder selectColumns(String columns) {
        String[] columnz = columns.split(",");
        for(String column : columnz){
            fetchColumnsList.add(new Column(column));
        }
        fetchedColumns[0] = concatenateColumnNames();
        return this;
    }

    public QueryBuilder selectColumn(String column) {
        fetchColumnsList.add(new Column(column));
        fetchedColumns[0] = concatenateColumnNames();
        return this;
    }


    public QueryBuilder from(String tableName) {
        try {
            validateTableName(tableName, "SELECT: Table name is empty");
            this.databaseName = Q.getParentDatabase(tableName);
            this.tableName = tableName;
            query += fetchedColumns[0] + " FROM " + databaseName+"."+tableName + " ";
        } catch (QueryBuilderException e) {
            e.printStackTrace();
        }
        return this;
    }

    public QueryBuilder from() {
        query += fetchedColumns[0] + " FROM ";
        return this;
    }

    public QueryBuilder where(FilterPredicate filterPredicate) {
        String whereClause = filterPredicate.getClause();
        where(whereClause);
        return this;
    }

    public QueryBuilder where(String whereClause) {
        try {
            validateSelection(whereClause);
            this.whereClause = whereClause;
            query += " WHERE " + whereClause + " ";
        } catch (QueryBuilderException e) {
            e.printStackTrace();
        }
        return this;
    }

    public QueryBuilder groupBy(String columns) {
        try {
            if(columns==null || columns.isEmpty())
                throw  new QueryBuilderException("GROUP: group by columns must not be empty");
            query += " GROUP BY " + (columns) + " ";
        } catch (QueryBuilderException e) {
            e.printStackTrace();
        }
        return this;
    }

    public QueryBuilder having(FilterPredicate filterPredicate) {
        String havingClause = filterPredicate.getClause();
        having(havingClause);
        return this;
    }

    public QueryBuilder having(String havingClause) {
        try {
            if (havingClause.isEmpty()) throw new QueryBuilderException("HAVING: Having clause is empty");
            query += " HAVING " + havingClause;
        } catch (QueryBuilderException e) {
            e.printStackTrace();
        }
        return this;
    }

    public QueryBuilder orderBy(ColumnOrderBy[] columns) {
        try {
            validateOrderBy(columns);
            query += " ORDER BY " + concatenateOrderByColumnNames(columns);
        } catch (QueryBuilderException e) {
            e.printStackTrace();
        }
        return this;
    }

    public QueryBuilder orderBy(String orderBy) {
        try {
            if (orderBy.isEmpty()) throw new QueryBuilderException("Order By clause cannot be empty");
            query += " ORDER BY " + orderBy;
        } catch (QueryBuilderException e) {
            e.printStackTrace();
        }
        return this;
    }

    /*********************************************************/
    public QueryBuilder _case() {
        query += "(CASE ";
        return this;
    }

    /*public Query _case(String precedenceComma)
    {
        if(precedenceComma!=null && !precedenceComma.isEmpty()) query+=",";
        query+="CASE ";
        return this;
    }*/

    public QueryBuilder _end(String succeedingComma) {
        query += "END)";
        if (succeedingComma != null && !succeedingComma.isEmpty()) query += ",";
        else query += " ";
        return this;
    }

    public QueryBuilder _endAs(String alias, String succeedingComma) {
        query += "END AS " + alias;
        if (succeedingComma != null && !succeedingComma.isEmpty()) query += "), ";
        else query += ") ";
        return this;
    }

    public QueryBuilder _when(String whenClause) {
        query += "WHEN " + whenClause + " ";
        return this;
    }

    public QueryBuilder _then(String namedVariable) {
        try {
            if (!namedVariable.startsWith(":"))
                throw new QueryBuilderException("THEN CLAUSE IN CASE: namedVariable must start with full colon[:]");
            if (namedVariable.length() < 2) throw new QueryBuilderException("THEN CLAUSE IN CASE: namedVariable is empty");
            if (namedVariable.chars().filter(ch -> ch == ':').count() > 1)
                throw new QueryBuilderException("THEN CLAUSE IN CASE: namedVariable can only have one full colon[:]");
            query += "THEN " + namedVariable + " ";
        } catch (QueryBuilderException e) {
            e.printStackTrace();
        }
        return this;
    }

    public QueryBuilder _else(String namedVariable) {
        try {
            if (!namedVariable.startsWith(":"))
                throw new QueryBuilderException("ELSE CLAUSE IN CASE: namedVariable must start with full colon[:]");
            if (namedVariable.length() < 2) throw new QueryBuilderException("ELSE CLAUSE IN CASE: namedVariable is empty");
            if (namedVariable.chars().filter(ch -> ch == ':').count() > 1)
                throw new QueryBuilderException("ELSE CLAUSE IN CASE: namedVariable can only have one full colon[:]");
            query += "ELSE " + namedVariable + " ";
        } catch (QueryBuilderException e) {
            e.printStackTrace();
        }
        return this;
    }

    public QueryBuilder _else() {
        query += " ELSE ";
        return this;
    }

    public QueryBuilder limit() {
        query += " LIMIT :num_of_records OFFSET :offset ";
        return this;
    }

    /*********************************************************/
    public QueryBuilder union(String preceedingQuery, String followingQuery) {
        query += preceedingQuery + " UNION " + followingQuery + " ";
        return this;
    }

    public QueryBuilder unionAll(String preceedingQuery, String followingQuery) {
        query += preceedingQuery + " UNION ALL " + followingQuery + " ";
        return this;
    }

    /******************************************************/
    public QueryBuilder joinPhrase(String joinPhrase) {
        try {
            if(joinPhrase.isEmpty()) throw new QueryBuilderException("JOIN STATEMENT: join statement should not be empty");
            joinStatement += " "+joinPhrase+" ";
            query += " " +joinPhrase;
        } catch (QueryBuilderException e) {
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public String toString() {
        return query.trim().replaceAll("\\s{2,}", " ");
    }

    /*******************GETTERS AND SETTERS *************************/

    public String getJoinStatement() {
        return joinStatement;
    }

    public String getWhereClause() {
        return whereClause;
    }

    public String getTableName() {
        return tableName;
    }

    public QueryBuilder setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public List<String> getPrimaryKeyColumns() {
        return primaryKeyColumns;
    }

    public String getSelectColumns(){
        return fetchedColumns[0];
    }

    public String getPrimaryKeyColumn() {
        return primaryKeyColumn;
    }

    public QueryBuilder setPrimaryKeyColumn(String column)
    {
        try {
            if (column.isEmpty()) throw new QueryBuilderException("SetPrimaryKeyColumn: column cannot be empty");
            primaryKeyColumn = column;
        } catch (QueryBuilderException e) {
            e.printStackTrace();
        }
        return this;
    }

    public QueryBuilder addPrimaryKeyColumns(String column)
    {
        try {
            if (column.isEmpty()) throw new QueryBuilderException("AddPrimaryKeyColumns: column cannot be empty");
            primaryKeyColumns.add(column);
        } catch (QueryBuilderException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**************** UTILITY FUNCTIONS ****************/
    private String concatenateUpdateSet(HashMap<String, String> hashMap) {
        StringBuilder stringBuilder = new StringBuilder();
        int listlength = hashMap.size();
        int i = 0;
        for (Map.Entry<String, String> entry : hashMap.entrySet()) {
            String column = entry.getKey();
            String namedVar = entry.getValue();
            stringBuilder.append(column).append(" = ");
            stringBuilder.append(namedVar);
            if (i != listlength - 1) stringBuilder.append(", ");
            i++;
        }
        return stringBuilder.toString();
    }

    private String concatenateColumnNames() {
        StringBuilder stringBuilder = new StringBuilder();
        int length = fetchColumnsList.size();

        for (int i = 0; i < length; i++) {
            Column column = fetchColumnsList.get(i);
            if (column.getAggregate() != null) {
                stringBuilder.append(column.getAggregate()).append("(").append(column.getColumnName()).append(")");
            } else {
                stringBuilder.append(column.getColumnName());
            }
            if (column.getColumnAlias() != null && !column.getColumnAlias().isEmpty()) {
                stringBuilder.append(" AS ").append(column.getColumnAlias());
            }
            if (i != length - 1) stringBuilder.append(", ");
        }
        return stringBuilder.toString();
    }

    private String concatenateColumnNames(String[] columns) {
        StringBuilder stringBuilder = new StringBuilder();
        int length = columns.length;
        stringBuilder.append("(");
        for (int i = 0; i < length; i++) {
            stringBuilder.append(columns[i]);
            if (i != length - 1) stringBuilder.append(",");
        }
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    private String concatenateOrderByColumnNames(ColumnOrderBy[] columns) {
        StringBuilder stringBuilder = new StringBuilder();
        int length = columns.length;
        for (int i = 0; i < length; i++) {
            ColumnOrderBy columnOrderBy = columns[i];
            if (columnOrderBy.getAggregate() != null) {
                stringBuilder.append(columnOrderBy.getAggregate()).append("(").append(columnOrderBy.getColumnName()).append(")");
            } else {
                stringBuilder.append(columnOrderBy.getColumnName());
            }
            stringBuilder.append(" ").append(columnOrderBy.getOrderBy());
            if (i != length - 1) stringBuilder.append(", ");
        }
        return stringBuilder.toString();
    }

    private void validateOrderBy(ColumnOrderBy[] columnOrderByList) throws QueryBuilderException {
        for (ColumnOrderBy columnOrderBy : columnOrderByList) {
            if (columnOrderBy.getColumnName().isEmpty())
                throw new QueryBuilderException("ORDER BY: order by column cannot be empty");
        }
    }

    private void validateNonEmptyStringArray(String[] columns, String message) throws QueryBuilderException {
        for (String column : columns) {
            if (column.isEmpty()) throw new QueryBuilderException(message);
        }
    }

    private void validateSelection(String whereClause) throws QueryBuilderException {
        if (whereClause == null || whereClause.isEmpty()) throw new QueryBuilderException("WHERE: where clause is empty");
        if (whereClause.chars().filter(ch -> ch == '(').count() != whereClause.chars().filter(ch -> ch == ')').count())
            throw new QueryBuilderException("WHERE CLAUSE: Number of '(' do not match number of ')' in: " + whereClause);

    }

    private void validateTableName(String tableName, String message) throws QueryBuilderException {
        if (tableName == null || tableName.isEmpty()) throw new QueryBuilderException(message);
    }

    private void validateUpdateSet(HashMap<String, String> hashMap) throws QueryBuilderException {
        if (hashMap.isEmpty()) throw new QueryBuilderException("UPDATE: Update set columns missing");
        for (Map.Entry<String, String> entry : hashMap.entrySet()) {
            String column = entry.getKey();
            String namedVar = entry.getValue();
            if (column.isEmpty()) throw new QueryBuilderException("UPDATE: Update column is empty");
            if (namedVar.length() < 2) throw new QueryBuilderException("UPDATE: named variable for update is empty");
            if (!namedVar.startsWith(":"))
                throw new QueryBuilderException("UPDATE: '" + namedVar + "' named variable must start with full colon[:] in SET " +
                        "hashmap e.g. hashmap.put('name',':nameVar')");
        }
    }

    private void validateColumnArg(String columnArg, String message) throws QueryBuilderException {
        if (!columnArg.startsWith(":"))
            throw new QueryBuilderException("'" + columnArg + "' named variable must start with full colon[:]");
        if (columnArg.length() < 2) throw new QueryBuilderException("ColumnArg is empty");
    }
}
