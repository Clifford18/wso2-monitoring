package ke.co.skyworld.repository.query;

import ke.co.skyworld.domain.enums.PortalTypes;
import ke.co.skyworld.repository.Q;
import ke.co.skyworld.repository.beans.FlexicoreHashMap;
import ke.co.skyworld.repository.beans.TransactionWrapper;
import ke.co.skyworld.repository.exceptions.FilterException;
import ke.co.skyworld.utils.Constants;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

public class FilterTokenizer {
    private static final String COMBINERS_DELIMITER = "\\|";
    private static final String SINGLE_CLAUSE_DELIMITER = ":";
    private static final Set<String> COMBINERS = new HashSet<>();
    private static final HashMap<String, String> RELATIONS_AND_SYMBOLS = new HashMap<>();
    private static final HashMap<String, String> RELATIONS_AND_FULL_NAMES = new HashMap<>();

    static {
        COMBINERS.add("AND");
        COMBINERS.add("OR");

        RELATIONS_AND_SYMBOLS.put("eq", "=");
        RELATIONS_AND_FULL_NAMES.put("eq", "Equal To");
        RELATIONS_AND_SYMBOLS.put("!eq", "<>");
        RELATIONS_AND_FULL_NAMES.put("!eq", "Not Equal To");

        RELATIONS_AND_SYMBOLS.put("gt", ">");
        RELATIONS_AND_FULL_NAMES.put("gt", "Greater Than");
        RELATIONS_AND_SYMBOLS.put("gte", ">=");
        RELATIONS_AND_FULL_NAMES.put("gte", "Greater Than Or Equal To");
        RELATIONS_AND_SYMBOLS.put("lt", "<");
        RELATIONS_AND_FULL_NAMES.put("lt", "Less Than");
        RELATIONS_AND_SYMBOLS.put("lte", "<=");
        RELATIONS_AND_FULL_NAMES.put("lte", "Less Than Or Equal To");

        RELATIONS_AND_SYMBOLS.put("contains", "LIKE");
        RELATIONS_AND_FULL_NAMES.put("contains", "Contains");
        RELATIONS_AND_SYMBOLS.put("btwn", "BETWEEN");
        RELATIONS_AND_FULL_NAMES.put("btwn", "Between");
        RELATIONS_AND_SYMBOLS.put("in", "IN");
        RELATIONS_AND_FULL_NAMES.put("in", "In");
        RELATIONS_AND_SYMBOLS.put("null", "IS NULL");
        RELATIONS_AND_FULL_NAMES.put("null", "Is Null");
        RELATIONS_AND_SYMBOLS.put("sw", "LIKE");
        RELATIONS_AND_FULL_NAMES.put("sw", "Starts With");
        RELATIONS_AND_SYMBOLS.put("ew", "LIKE");
        RELATIONS_AND_FULL_NAMES.put("ew", "Ends With");

        RELATIONS_AND_SYMBOLS.put("!sw", "NOT LIKE");
        RELATIONS_AND_FULL_NAMES.put("!sw", "Not Starting With");
        RELATIONS_AND_SYMBOLS.put("!ew", "NOT LIKE");
        RELATIONS_AND_FULL_NAMES.put("!ew", "Not Starting With");
        RELATIONS_AND_SYMBOLS.put("!contains", "NOT LIKE");
        RELATIONS_AND_FULL_NAMES.put("!contains", "Not containing");
        RELATIONS_AND_SYMBOLS.put("!btwn", "NOT BETWEEN");
        RELATIONS_AND_FULL_NAMES.put("!btwn", "Not Between");
        RELATIONS_AND_SYMBOLS.put("!in", "NOT IN");
        RELATIONS_AND_FULL_NAMES.put("!in", "Not In");
        RELATIONS_AND_SYMBOLS.put("!null", "IS NOT NULL");
        RELATIONS_AND_FULL_NAMES.put("!null", "Is Not Null");
    }

    public static List<String> getRelationsAndSymbols()
    {
        return new ArrayList<>(RELATIONS_AND_SYMBOLS.keySet());
    }

     public static List<String> getRelationsAndFullNames()
    {
        return new ArrayList<>(RELATIONS_AND_FULL_NAMES.keySet());
    }


    /*public static Object[] generatePredicate(String filterStatement) {
        try {
            validateCurlyBraces(filterStatement);
            validateParentheses(filterStatement);
            filterStatement = stripCurlyBraces(filterStatement);
            return new FormPredicate(filterStatement).getFilterPredicate();
        } catch (FilterException e) {

            e.printStackTrace();
        }
        return null;
    }*/

    public static TransactionWrapper<Object[]> generatePredicate(String filterStatement, String tableName) {
        TransactionWrapper<Object[]> tWrapper = new TransactionWrapper<>();
        try {
            validateCurlyBraces(filterStatement);
            validateParentheses(filterStatement);
            filterStatement = stripCurlyBraces(filterStatement);
            tWrapper.setData(new FormPredicate(filterStatement, tableName).getFilterPredicate());
        } catch (FilterException e) {
            tWrapper.setHasErrors(true);
            tWrapper.addError(e.getMessage());
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String sStackTrace = sw.toString();
            try {
                sw.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            pw.close();
            tWrapper.addErrorStackTrace(sStackTrace);
            e.printStackTrace();
        }
        return tWrapper;
    }

    private static void validateCurlyBraces(String filterStatement) throws FilterException {
        if (filterStatement.chars().filter(ch -> ch == '{').count() > 1 || filterStatement.chars().filter(ch -> ch == '}').count() > 1)
            throw new FilterException("Only one of '{}' are allowed in the filter. Offender: " + filterStatement);
        if (!filterStatement.startsWith("{"))
            throw new FilterException("Filter must start with '{'. Offender: " + filterStatement);
        if (!filterStatement.endsWith("}"))
            throw new FilterException("Filter must end with '}'. Offender: " + filterStatement);
    }

    private static void validateParentheses(String filterStatement) throws FilterException {
        if (filterStatement.chars().filter(ch -> ch == '(').count() != filterStatement.chars().filter(ch -> ch == ')').count())
            throw new FilterException("The number of '(' must match number of ')'. Offender: " + filterStatement);
    }

    private static String stripCurlyBraces(String filterStatement) {
        return filterStatement.replace("{", "").replace("}", "");
    }

    private static class FormPredicate {
        private String filterStatement;
        private StringBuilder filterPredicate = new StringBuilder();
        private int queryArgsCounter = 0;
        private FlexicoreHashMap queryArguments = new FlexicoreHashMap();
        private boolean nextIsCombinerOperator = false;
        private boolean shouldRecurse = false;
        private boolean checkIllegalCombinerStart = true;
        private final String or = COMBINERS_DELIMITER + "\\s*OR\\s*" + COMBINERS_DELIMITER;
        private final String and = COMBINERS_DELIMITER + "\\s*AND\\s*" + COMBINERS_DELIMITER;

        public FormPredicate(String filterStatement, String tableName) throws FilterException {
            this.filterStatement = filterStatement;
            perform(tableName);
        }

        public Object[] getFilterPredicate() {
            //return new Object[]{"(" + filterPredicate.toString() + ")", queryArguments};

            if(!filterPredicate.toString().isEmpty()){
                return new Object[]{"(" + filterPredicate.toString() + ")", queryArguments};
            } else {
                return new Object[]{filterPredicate.toString(), queryArguments};
            }
        }

        public void perform(String tableName) throws FilterException {
            filterStatement = filterStatement.trim();
            if (filterStatement.isEmpty()) return;

            if (filterStatement.startsWith("(")) {
                filterPredicate.append("(");
                filterStatement = filterStatement.replaceFirst("\\(", "").trim();
                if (filterStatement.matches("^(" + or + "?|" + and + ").*$"))
                    throw new FilterException("Combiner operator after '(' without a preceding clause " + "disallowed. Offender: " + filterStatement);
                perform(tableName);
            }
            if (checkIllegalCombinerStart) {
                checkIllegalCombinerStart = false;
                if (filterStatement.matches("^(" + or + "?|" + and + ").*$"))
                    throw new FilterException("First combiner operator without a preceding clause " + "disallowed. Offender: " + filterStatement);
            }

            int iOp = filterStatement.indexOf("(");
            int iCp = filterStatement.indexOf(")");
            if (iOp >= 0) {
                if (iOp < iCp) evaluateCombinedClause(filterStatement.substring(0, iOp), tableName);
                else {
                    evaluateCombinedClause(filterStatement.substring(0, iCp), tableName);
                    nextIsCombinerOperator = true;
                }
                shouldRecurse = true;
            } else if (iCp >= 0) {
                evaluateCombinedClause(filterStatement.substring(0, iCp), tableName);
                nextIsCombinerOperator = true;
                shouldRecurse = true;
            } else {
                if (!filterStatement.isEmpty()) evaluateCombinedClause(filterStatement, tableName);
                shouldRecurse = false;
            }
            if (shouldRecurse) perform(tableName);
        }

        private void evaluateCombinedClause(String semiClause, String tableName) throws FilterException {
            String[] combinedClauses = semiClause.trim().split(COMBINERS_DELIMITER);
            for (int i = 0; i < combinedClauses.length; i++) {
                String singleClause = combinedClauses[i].trim();
                filterStatement =
                        filterStatement.replaceFirst(COMBINERS_DELIMITER + "\\s*(" + singleClause + ")\\s*" + COMBINERS_DELIMITER, "").trim();
                if (!singleClause.isEmpty()) {
                    if (filterStatement.startsWith("(") || filterStatement.startsWith(")")) nextIsCombinerOperator = true;
                    if (nextIsCombinerOperator) {
                        if (!COMBINERS.contains(singleClause))
                            throw new FilterException("Combiner Operator " + singleClause + " disallowed. Offender: " + semiClause);
                        if (i == combinedClauses.length - 1 && !filterStatement.startsWith("(") && !filterStatement.startsWith(")"))
                            throw new FilterException("Ending combiner operator " + singleClause + " without a succeeding clause " +
                                    "disallowed. Offender: " + semiClause);
                        filterPredicate.append(singleClause).append(" ");

                    } else evaluateSingleClause(singleClause, tableName);
                    nextIsCombinerOperator = !nextIsCombinerOperator;

                } else {
                    filterPredicate.append(" ");
                }
            }
            filterStatement = filterStatement.trim();
            if (filterStatement.startsWith(")")) {
                filterPredicate = new StringBuilder(filterPredicate.toString().trim());
                filterPredicate.append(")");
                filterStatement = filterStatement.replaceFirst("\\)", "").trim();
                nextIsCombinerOperator = true;
            }
        }

        private void evaluateSingleClause(String singleClause, String tableName) throws FilterException {
            //String[] components = singleClause.trim().split(SINGLE_CLAUSE_DELIMITER);
            String[] components = split(singleClause, SINGLE_CLAUSE_DELIMITER);
            if (components.length < 2) throw new FilterException("Clause missing the corresponding operation. Offender: " + singleClause);
            if (!RELATIONS_AND_SYMBOLS.containsKey(components[1].trim()))
                throw new FilterException("Unresolvable Operator '" + components[1].trim() + "'. Offender: " + singleClause);

            String columnName = components[0].trim();
            String tableAndColumn = tableName+columnName;

            filterPredicate.append(columnName).append(" ");
            String relationName = components[1].trim();

            switch (relationName) {
                case "btwn":
                case "!btwn":
                    if (components.length < 3)
                        throw new FilterException("Between expects two values that are comma separated. Offender: " + singleClause);

                    String[] btnOperands = components[2].trim().split(",");
                    if (btnOperands.length < 2)
                        throw new FilterException("Between expects two values that are comma separated. Offender: " + singleClause);

                    filterPredicate.append(RELATIONS_AND_SYMBOLS.get(relationName))
                            .append(" ")
                            .append(":").append(columnName).append(queryArgsCounter)
                            .append(" AND ")
                            .append(":").append(columnName).append((queryArgsCounter + 1))
                            .append(" ");
                    queryArguments.addQueryArgument(":" + columnName + queryArgsCounter, btnOperands[0].trim());
                    queryArguments.addQueryArgument(":" + columnName + (queryArgsCounter + 1), btnOperands[1].trim());
                    break;
                case "in":
                case "!in":
                    if (components.length < 3)
                        throw new FilterException("IN expects at least one value. Offender: " + singleClause);

                    filterPredicate.append(RELATIONS_AND_SYMBOLS.get(relationName)).append(" (");
                    String[] inValues = components[2].split(",");
                    int length = inValues.length;
                    for (int i = 0; i < length; i++) {
                        filterPredicate.append(":").append(columnName).append((queryArgsCounter + i));
                        queryArguments.addQueryArgument(":" + columnName + (queryArgsCounter + i), inValues[i].trim());
                        if (i != length - 1) filterPredicate.append(",");
                    }
                    filterPredicate.append(") ");
                    break;
                case "null":
                case "!null":
                    filterPredicate.append(RELATIONS_AND_SYMBOLS.get(relationName)).append(" ");
                    break;
                default:
                    if (components.length < 3)
                        throw new FilterException(RELATIONS_AND_FULL_NAMES.get(relationName) + " expects at least one value. Offender: " + singleClause);
                    filterPredicate.append(RELATIONS_AND_SYMBOLS.get(relationName))
                            .append(" ")
                            .append(":").append(columnName).append(queryArgsCounter)
                            .append(" ");

                    switch (relationName) {
                        case "contains":
                        case "!contains":
                            queryArguments.addQueryArgument(":" + columnName + queryArgsCounter, "%" + components[2].trim() + "%");
                            break;
                        case "sw":
                        case "!sw":
                            queryArguments.addQueryArgument(":" + columnName + queryArgsCounter, components[2].trim() + "%");
                            break;
                        case "ew":
                        case "!ew":
                            queryArguments.addQueryArgument(":" + columnName + queryArgsCounter, "%" + components[2].trim());
                            break;
                        default:
                            queryArguments.addQueryArgument(":" + columnName + queryArgsCounter, components[2].trim());
                            break;
                    }
                    break;
            }
            filterStatement = filterStatement.replaceFirst(singleClause, "").trim();
            queryArgsCounter++;
        }

        private String[] split(String string, String delem) {
            ArrayList<String> list = new ArrayList<String>();
            char[] charArr = string.toCharArray();
            char[] delemArr = delem.toCharArray();
            int counter = 0;
            int stopCount = 0;
            for (int i = 0; i < charArr.length; i++) {
                //if(stopCount<2){
                int k = 0;
                for (int j = 0; j < delemArr.length; j++) {
                    if (charArr[i+j] == delemArr[j]) {
                        k++;
                    } else {
                        break;
                    }
                }
                if (k == delemArr.length) {
                    String s = "";
                    while (counter < i ) {
                        s += charArr[counter];
                        counter++;
                    }
                    counter = i = i + k;
                    list.add(s);
                    if(stopCount==1) break;
                    stopCount++;
                    //System.out.println(" k = "+k+" i= "+i);
                }
                //}

            }
            String s = "";
            if (counter < charArr.length) {
                while (counter < charArr.length) {
                    s += charArr[counter];
                    counter++;
                }
                list.add(s);
            }
            return list.toArray(new String[list.size()]);
        }
    }
}
