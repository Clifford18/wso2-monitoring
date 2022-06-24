package ke.co.skyworld.repository.query;

import ke.co.skyworld.repository.exceptions.QueryBuilderException;

@SuppressWarnings({"UnusedReturnValue", "WeakerAccess"})
public class FilterPredicate {
    private String predicateClause = "";

    public FilterPredicate() { }

    public FilterPredicate(String predicateClause) {
        this.predicateClause = predicateClause;
    }

    public FilterPredicate openPredicate() {
        predicateClause += "(";
        return this;
    }

    public FilterPredicate rawClause(String rawPredicateClause) {
        predicateClause += rawPredicateClause;
        return this;
    }

    public FilterPredicate closePredicate() {
        predicateClause += ")";
        return this;
    }

    public FilterPredicate or() {
        predicateClause += " OR ";
        return this;
    }

    public FilterPredicate and() {
        predicateClause += " AND ";
        return this;
    }

    public FilterPredicate not() {
        predicateClause += " NOT ";
        return this;
    }

    public FilterPredicate isNotNull(String column) {
        predicateClause += column + " IS NOT NULL ";
        return this;
    }

    public FilterPredicate isNull(String column) {
        predicateClause += column + " IS NULL ";
        return this;
    }

    public FilterPredicate like(String column, String namedVariable) {
        try {
            if (column.isEmpty()) throw new QueryBuilderException("LIKE: Column name is empty");
            validateColumnArg(namedVariable, "LIKE: ");
            predicateClause += " "+column+" LIKE " + namedVariable + " ";
        } catch (QueryBuilderException e) {
            e.printStackTrace();
        }
        return this;
    }

    public FilterPredicate ilike(String column, String namedVariable) {
        try {
            if (column.isEmpty()) throw new QueryBuilderException("ILIKE: Column name is empty");
            validateColumnArg(namedVariable, "ILIKE: ");
            predicateClause += " "+column+" ILIKE " + namedVariable + " ";
        } catch (QueryBuilderException e) {
            e.printStackTrace();
        }
        return this;
    }

    public FilterPredicate in(String column, String query) {
        try {
            if (column.isEmpty()) throw new QueryBuilderException("IN: Column name is empty");
            predicateClause += " "+column+" IN (" + query + ") ";
        } catch (QueryBuilderException e) {
            e.printStackTrace();
        }
        return this;
    }

    public FilterPredicate in(String column, String[] values) {
        try {
            if (column.isEmpty()) throw new QueryBuilderException("IN: Column name is empty");
            predicateClause += " "+column+" IN " + concatenateColumnNames(values) + " ";
        } catch (QueryBuilderException e) {
            e.printStackTrace();
        }
        return this;
    }

    public FilterPredicate any(String subQuery) {
        predicateClause += " ANY (" + subQuery + ") ";
        return this;
    }

    public FilterPredicate all(String subQuery) {
        predicateClause += " ALL (" + subQuery + ") ";
        return this;
    }

    public FilterPredicate exists(String subQuery) {
        predicateClause += " EXISTS (" + subQuery + ") ";
        return this;
    }

    public FilterPredicate some(String subQuery) {
        predicateClause += " SOME (" + subQuery + ") ";
        return this;
    }

    public FilterPredicate between(String column, String namedVariable1, String namedVariable2) {
        try {
            if (column.isEmpty()) throw new QueryBuilderException("BETWEEN: Column name is empty");
            validateColumnArg(namedVariable1, "BETWEEN: ");
            validateColumnArg(namedVariable2, "BETWEEN: ");
            predicateClause += " " + column + " BETWEEN " + namedVariable1 + " AND " + namedVariable2 + " ";
        } catch (QueryBuilderException e) {
            e.printStackTrace();
        }
        return this;
    }

    public FilterPredicate lessThan(String column, String namedVariable) {
        try {
            if (column.isEmpty()) throw new QueryBuilderException("LESS THAN: Column name is empty");
            validateColumnArg(namedVariable, "LESS THAN: ");
            predicateClause += " " + column + " < " + namedVariable;
        } catch (QueryBuilderException e) {
            e.printStackTrace();
        }
        return this;
    }

    public FilterPredicate lessThanSubquery(String column, String subQuery) {
        try {
            if (column.isEmpty()) throw new QueryBuilderException("LESS THAN: Column name is empty");
            if (subQuery.isEmpty()) throw new QueryBuilderException("LESS THAN: Sub-query is empty");
            predicateClause += " " + column + " < (" + subQuery + ") ";
        } catch (QueryBuilderException e) {
            e.printStackTrace();
        }
        return this;
    }

    public FilterPredicate lessThanOrEqualTo(String column, String namedVariable) {
        try {
            if (column.isEmpty()) throw new QueryBuilderException("LESS THAN OR EQUAL TO: Column name is empty");
            validateColumnArg(namedVariable, "LESS THAN OR EQUAL TO: ");
            predicateClause += " " + column + " <= " + namedVariable;
        } catch (QueryBuilderException e) {
            e.printStackTrace();
        }
        return this;
    }

    public FilterPredicate lessThanOrEqualToSubquery(String column, String subQuery) {
        try {
            if (column.isEmpty()) throw new QueryBuilderException("LESS THAN OR EQUAL TO: Column name is empty");
            if (subQuery.isEmpty()) throw new QueryBuilderException("LESS THAN OR EQUAL TO: Sub-query is empty");
            predicateClause += " " + column + " <= (" + subQuery + ") ";
        } catch (QueryBuilderException e) {
            e.printStackTrace();
        }
        return this;
    }

    public FilterPredicate greaterThan(String column, String namedVariable) {
        try {
            if (column.isEmpty()) throw new QueryBuilderException("GREATER THAN: Column name is empty");
            validateColumnArg(namedVariable, "GREATER THAN: ");
            predicateClause += " " + column + " > " + namedVariable;
        } catch (QueryBuilderException e) {
            e.printStackTrace();
        }
        return this;
    }

    public FilterPredicate greaterThanSubquery(String column, String subQuery) {
        try {
            if (column.isEmpty()) throw new QueryBuilderException("GREATER THAN: Column name is empty");
            if (subQuery.isEmpty()) throw new QueryBuilderException("GREATER THAN: Sub-query is empty");
            predicateClause += " " + column + " > (" + subQuery + ") ";
        } catch (QueryBuilderException e) {
            e.printStackTrace();
        }
        return this;
    }

    public FilterPredicate greaterThanOrEqualTo(String column, String namedVariable) {
        try {
            if (column.isEmpty()) throw new QueryBuilderException("GREATER THAN OR EQUAL TO: Column name is empty");
            validateColumnArg(namedVariable, "GREATER THAN OR EQUAL TO: ");
            predicateClause += " " + column + " >= " + namedVariable;
        } catch (QueryBuilderException e) {
            e.printStackTrace();
        }
        return this;
    }

    public FilterPredicate greaterThanOrEqualToSubquery(String column, String subQuery) {
        try {
            if (column.isEmpty()) throw new QueryBuilderException("GREATER THAN OR EQUAL TO: Column name is empty");
            if (subQuery.isEmpty()) throw new QueryBuilderException("GREATER THAN OR EQUAL TO: Sub-query is empty");
            predicateClause += " " + column + " >= (" + subQuery + ") ";
        } catch (QueryBuilderException e) {
            e.printStackTrace();
        }
        return this;
    }

    public FilterPredicate equalTo(String column, String namedVariable) {
        try {
            if (column.isEmpty()) throw new QueryBuilderException("EQUAL TO: Column name is empty");
            validateColumnArg(namedVariable, "EQUAL TO: ");
            predicateClause += " " + column + " = " + namedVariable;
        } catch (QueryBuilderException e) {
            e.printStackTrace();
        }
        return this;
    }

    public FilterPredicate equalToSubquery(String column, String subQuery) {
        try {
            if (column.isEmpty()) throw new QueryBuilderException("EQUAL TO: Column name is empty");
            if (subQuery.isEmpty()) throw new QueryBuilderException("EQUAL TO: Sub-query is empty");
            predicateClause += " " + column + " = (" + subQuery + ") ";
        } catch (QueryBuilderException e) {
            e.printStackTrace();
        }
        return this;
    }

    public FilterPredicate notEqualTo(String column, String namedVariable) {
        try {
            if (column.isEmpty()) throw new QueryBuilderException("NOT EQUAL TO: Column name is empty");
            validateColumnArg(namedVariable, "NOT EQUAL TO: ");
            predicateClause += " " + column + " <> " + namedVariable;
        } catch (QueryBuilderException e) {
            e.printStackTrace();
        }
        return this;
    }

    public FilterPredicate notEqualToSubquery(String column, String subQuery) {
        try {
            if (column.isEmpty()) throw new QueryBuilderException("NOT EQUAL TO: Column name is empty");
            if (subQuery.isEmpty()) throw new QueryBuilderException("NOT EQUAL TO: Sub-query is empty");
            predicateClause += " " + column + " <> (" + subQuery + ") ";
        } catch (QueryBuilderException e) {
            e.printStackTrace();
        }
        return this;
    }

    public FilterPredicate _case() {
        predicateClause += " (CASE ";
        return this;
    }

    /*public Query _case(String precedenceComma)
    {
        if(precedenceComma!=null && !precedenceComma.isEmpty()) predicateClause+=",";
        predicateClause+="CASE ";
        return this;
    }*/

    public FilterPredicate _end(String succeedingComma) {
        predicateClause += "END) ";
        if (succeedingComma != null && !succeedingComma.isEmpty()) predicateClause += ",";
        else predicateClause += " ";
        return this;
    }

    public FilterPredicate _end() {
        return _end(null);
    }

    public FilterPredicate _endAs(String alias, String succeedingComma) {
        predicateClause += "END AS " + alias;
        if (succeedingComma != null && !succeedingComma.isEmpty()) predicateClause += "), ";
        else predicateClause += ") ";
        return this;
    }

    public FilterPredicate _when(String whenClause) {
        predicateClause += "WHEN " + whenClause + " ";
        return this;
    }

    public FilterPredicate _then(String namedVariable) {
        try {
            if (!namedVariable.startsWith(":"))
                throw new QueryBuilderException("THEN CLAUSE IN CASE: namedVariable must start with full colon[:]");
            if (namedVariable.length() < 2) throw new QueryBuilderException("THEN CLAUSE IN CASE: namedVariable is empty");
            if (namedVariable.chars().filter(ch -> ch == ':').count() > 1)
                throw new QueryBuilderException("THEN CLAUSE IN CASE: namedVariable can only have one full colon[:]");
            predicateClause += "THEN " + namedVariable + " ";
        } catch (QueryBuilderException e) {
            e.printStackTrace();
        }
        return this;
    }

    public FilterPredicate _else(String namedVariable) {
        try {
            if (!namedVariable.startsWith(":"))
                throw new QueryBuilderException("ELSE CLAUSE IN CASE: namedVariable must start with full colon[:]");
            if (namedVariable.length() < 2) throw new QueryBuilderException("ELSE CLAUSE IN CASE: namedVariable is empty");
            if (namedVariable.chars().filter(ch -> ch == ':').count() > 1)
                throw new QueryBuilderException("ELSE CLAUSE IN CASE: namedVariable can only have one full colon[:]");
            predicateClause += "ELSE " + namedVariable + " ";
        } catch (QueryBuilderException e) {
            e.printStackTrace();
        }
        return this;
    }

    public FilterPredicate _else() {
        predicateClause += " ELSE ";
        return this;
    }

    public String getClause() {
        return predicateClause;
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

    private void validateColumnArg(String namedVariable, String errorLocation) throws QueryBuilderException {
        if (namedVariable.length() < 2) throw new QueryBuilderException(errorLocation + "namedVariable is empty");
        if (!namedVariable.startsWith(":"))
            throw new QueryBuilderException(errorLocation + "'" + namedVariable + "' named variable must start with full colon[:]");
        if (namedVariable.chars().filter(ch -> ch == ':').count() > 1)
            throw new QueryBuilderException(errorLocation + "'" + namedVariable + "' can only have one full colon[:]");
    }
}