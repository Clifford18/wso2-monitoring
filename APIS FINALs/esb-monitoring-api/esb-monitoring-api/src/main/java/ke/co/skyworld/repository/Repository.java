package ke.co.skyworld.repository;

import ke.co.skyworld.flexicore.connection_manager.beans.ConnectionManagerInitState;
import ke.co.skyworld.flexicore.connection_manager.connection.ConnectionManager;
import ke.co.skyworld.flexicore.connection_manager.connection.ExtendedConnection;
import ke.co.skyworld.flexicore.connection_manager.utilities.enums.ConnectionManagerInitStatus;
import ke.co.skyworld.repository.beans.*;
import ke.co.skyworld.repository.exceptions.QueryBuilderException;
import ke.co.skyworld.repository.query.FilterPredicate;
import ke.co.skyworld.repository.query.QueryBuilder;
import ke.co.skyworld.utils.Constants;
import ke.co.skyworld.utils.DateTime;
import ke.co.skyworld.utils.logging.Log;
import org.fusesource.jansi.Ansi;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static ke.co.skyworld.utils.Constants.*;
import static org.fusesource.jansi.Ansi.ansi;

@SuppressWarnings({"WeakerAccess", "NeverUsed"})
public abstract class Repository {

    public static void setup() {
        System.out.println(ansi().fg(Ansi.Color.WHITE).bold().a(" Initializing Repository...").reset());

        try {
            String dbDriverClass = Constants.getDbDriverClass();
            String dbName = Constants.getDbName();
            String dbHost = Constants.getDbHost();
            int dbPort = Constants.getDbPort();
            String dbUsername = Constants.getDbUsername();
            String dbPassword = Constants.getDbPassword();
            String connectionMetaData = Constants.getDbConnMetadata();
            String connectionURL = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName + "?" + connectionMetaData;
            int initialPoolSize = Constants.getSlingRingInitialPoolSize();
            int maxPoolSize = Constants.getSlingRingMaxPoolSize();
            int extraConnectionSize = Constants.getSlingRingExtraConnsSize();
            long checkFreeConnectionInterval = Constants.getSlingRingFindFreeConnAfter();
            TimeUnit checkFreeConnectionIntervalTimeUnit = TimeUnit.valueOf(Constants.getSlingRingFindFreeConnAfterTimeUnit());
            long pingInterval = Constants.getSlingRingPingAfter();
            TimeUnit pingIntervalTimeUnit = TimeUnit.valueOf(Constants.getSlingRingPingAfterTimeUnit());
            long downsizeInterval = Constants.getSlingRingDownSizeAfter();
            TimeUnit downsizeIntervalTimeUnit = TimeUnit.valueOf(Constants.getSlingRingDownSizeAfterTimeUnit());
            long connectionRetrySleepPeriod = 5; //Seconds

            /*System.out.println("initialPoolSize: "+ initialPoolSize);
            System.out.println("maxPoolSize: "+ maxPoolSize);
            System.out.println("extraConnectionSize: "+ extraConnectionSize);
            System.out.println("checkFreeConnectionInterval: "+ checkFreeConnectionInterval);
            System.out.println("checkFreeConnectionIntervalTimeUnit: "+ checkFreeConnectionIntervalTimeUnit);
            System.out.println("pingInterval: "+ pingInterval);
            System.out.println("pingIntervalTimeUnit: "+ pingIntervalTimeUnit);
            System.out.println("downsizeInterval: "+ downsizeInterval);
            System.out.println("downsizeIntervalTimeUnit: "+ downsizeIntervalTimeUnit);
            System.out.println("connectionRetrySleepPeriod: "+ connectionRetrySleepPeriod);*/

            ConnectionManagerInitState connectionManagerInitState = ConnectionManager.getInitializationState();

            //Check if Connection manager has been initialized
            if (connectionManagerInitState.getStatus() == ConnectionManagerInitStatus.INIT_NOT_CALLED) {
                System.out.println(ansi().fg(Ansi.Color.WHITE).a(" Setting up ").bold().a("MASTER").reset().fg(Ansi.Color.WHITE)
                        .a(" repository...").reset());

                //If NOT initialized, then initialize
                connectionManagerInitState = ConnectionManager.initialize(dbDriverClass, connectionURL, dbUsername, dbPassword, initialPoolSize,
                        maxPoolSize, extraConnectionSize, checkFreeConnectionInterval, checkFreeConnectionIntervalTimeUnit, pingInterval,
                        pingIntervalTimeUnit, downsizeInterval, downsizeIntervalTimeUnit);

                //Check Initialization Status
                if (connectionManagerInitState.getStatus().equals(ConnectionManagerInitStatus.SUCCESS)) {
                    System.out.println();
                    System.out.println(ansi().a(" Connection Manager Initialization Status             : ").bold().fgGreen().a(connectionManagerInitState.getStatus()).reset());
                    System.out.println(ansi().a(" Connection Manager Initialization Status Description : ").bold().fgGreen().a(connectionManagerInitState.getStatusDescription()).reset());
                    System.out.println(ansi().a(" Connection Pool State                                : ").bold().fgGreen().a("Connection Established!").reset());
                    System.out.println();
                } else {
                    while (connectionManagerInitState.getStatus() != ConnectionManagerInitStatus.SUCCESS){
                        //Retry creating Initial Connections
                        System.out.println();
                        System.out.println(ansi().a(" Connection Manager Initialization Status: ").bold().fgRed().a(connectionManagerInitState.getStatus()).reset());
                        System.out.println(ansi().a(" Connection Manager Initialization Status Description: ").fgYellow().fgGreen().a(connectionManagerInitState.getStatusDescription()).reset());
                        System.out.println(ansi().a(" Connection Pool State: ").bold().fgRed().a("NOT CONNECTED!").reset());
                        System.out.println();
                        System.out.println(" Waiting for "+(connectionRetrySleepPeriod)+" SECOND(S) then retrying...");
                        Thread.sleep(connectionRetrySleepPeriod*1000);
                        System.out.println(ansi().fgYellow().bold().a(" Retrying...").reset().reset());
                        System.out.println();
                        connectionManagerInitState = ConnectionManager.initialize(dbDriverClass, connectionURL, dbUsername, dbPassword, initialPoolSize,
                                maxPoolSize, extraConnectionSize, checkFreeConnectionInterval, checkFreeConnectionIntervalTimeUnit, pingInterval,
                                pingIntervalTimeUnit, downsizeInterval, downsizeIntervalTimeUnit);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.error(Repository.class, "setup",
                    "Error occurred during repository setup. (" + e.getMessage() + ")"
            );
        }
        System.out.println();
    }

    public static TransactionWrapper insertAutoIncremented(String tableName, FlexicoreHashMap recordHashMap) {
        List<String> columnsList = new ArrayList<>();
        FlexicoreHashMap queryArguments = new FlexicoreHashMap();

        recordHashMap.forEach((column, value) -> {
            if (!(column.equalsIgnoreCase("date_created") ||
                    column.equalsIgnoreCase("date_modified") ||
                    column.equalsIgnoreCase("msg_log_time") ||
                    column.equalsIgnoreCase("pesa_log_time") ||
                    column.equalsIgnoreCase("ussd_log_time") ||
                    column.equalsIgnoreCase("ussd_session_log_time") ||
                    column.equalsIgnoreCase("log_date"))) {
                columnsList.add(column);
                queryArguments.addQueryArgument(":" + column, value);
            }
        });

        setTimestamp(tableName, columnsList, queryArguments);

        String[] columns = columnsList.toArray(new String[0]);

        return insertAutoIncremented(tableName, columns, queryArguments);
    }

    public static TransactionWrapper insertAutoIncremented(ExtendedConnection extendedConnection, String tableName, FlexicoreHashMap recordHashMap) {
        List<String> columnsList = new ArrayList<>();
        FlexicoreHashMap queryArguments = new FlexicoreHashMap();

        recordHashMap.forEach((column, value) -> {
            if (!(column.equalsIgnoreCase("date_created") ||
                    column.equalsIgnoreCase("date_modified") ||
                    column.equalsIgnoreCase("msg_log_time") ||
                    column.equalsIgnoreCase("pesa_log_time") ||
                    column.equalsIgnoreCase("ussd_log_time") ||
                    column.equalsIgnoreCase("ussd_session_log_time") ||
                    column.equalsIgnoreCase("log_date"))) {
                columnsList.add(column);
                queryArguments.addQueryArgument(":" + column, value);
            }
        });

        setTimestamp(tableName, columnsList, queryArguments);

        String[] columns = columnsList.toArray(new String[0]);

        return insertAutoIncremented(extendedConnection, tableName, columns, queryArguments);
    }

    public static TransactionWrapper insertAutoIncremented(String tableName, String[] insertColumns,
                                                           FlexicoreHashMap queryArguments) {
        QueryBuilder queryBuilder = new QueryBuilder();
        queryArguments.setTableName(tableName);
        queryBuilder.insert().into(tableName).columns(insertColumns).values(formInsertNamedVars(insertColumns));
        return new QueryExecutor().insertAutoIncremented(queryBuilder, queryArguments);
    }

    public static TransactionWrapper insertAutoIncremented(ExtendedConnection extendedConnection, String tableName, String[] insertColumns,
                                                           FlexicoreHashMap queryArguments) {
        QueryBuilder queryBuilder = new QueryBuilder();
        queryArguments.setTableName(tableName);
        queryBuilder.insert().into(tableName).columns(insertColumns).values(formInsertNamedVars(insertColumns));
        return new QueryExecutor().insertAutoIncremented(extendedConnection, queryBuilder, queryArguments);
    }

    public static TransactionWrapper insertProvidedPK(String tableName, FlexicoreHashMap recordHashMap) {
        List<String> columnsList = new ArrayList<>();
        FlexicoreHashMap queryArguments = new FlexicoreHashMap();
        recordHashMap.forEach((column, value) -> {
            if (!(column.equalsIgnoreCase("date_created") ||
                    column.equalsIgnoreCase("date_modified") ||
                    column.equalsIgnoreCase("msg_log_time") ||
                    column.equalsIgnoreCase("pesa_log_time") ||
                    column.equalsIgnoreCase("ussd_log_time") ||
                    column.equalsIgnoreCase("ussd_session_log_time") ||
                    column.equalsIgnoreCase("log_date"))) {
                columnsList.add(column);
                queryArguments.addQueryArgument(":" + column, value);
            }
        });

        setTimestamp(tableName, columnsList, queryArguments);

        String[] columns = columnsList.toArray(new String[0]);

        return insertProvidedPK(tableName, columns, queryArguments);
    }

    public static TransactionWrapper insertProvidedPK(String tableName, String[] insertColumns,
                                                      FlexicoreHashMap queryArguments) {
        QueryBuilder queryBuilder = new QueryBuilder();
        queryArguments.setTableName(tableName);
        queryBuilder.insert().into(tableName).columns(insertColumns).values(formInsertNamedVars(insertColumns));
        return new QueryExecutor().insertProvidedPK(queryBuilder, queryArguments);
    }

    public static TransactionWrapper<Boolean> batchInsert(String tableName, FlexicoreArrayList insertValuesList) {
        TransactionWrapper<Boolean> twrapper = new TransactionWrapper<>(false);
        if (insertValuesList.isEmpty()) {
            twrapper.setHasErrors(true);
            twrapper.addError("Insert values list is empty");
            return twrapper;
        }
        QueryBuilder queryBuilder = new QueryBuilder();
        FlexicoreHashMap firstRecord = insertValuesList.getRecord(0);
        String[] queryVariables = firstRecord.getAllColumns().toArray(new String[0]);
        String[] insertColumns = new String[queryVariables.length];
        for (int i = 0; i < queryVariables.length; i++) {
            insertColumns[i] = queryVariables[i].replaceFirst(":", "");
        }
        queryBuilder.insert().into(tableName).columns(insertColumns).values(queryVariables);
        return new QueryExecutor().batchInsert(queryBuilder.toString(), insertValuesList);
    }

    public static TransactionWrapper delete(String tableName) {
        return delete(tableName, null, null);
    }

    public static TransactionWrapper<FlexicoreArrayList> delete(String tableName, FilterPredicate filterPredicate,
                                                                FlexicoreHashMap queryArguments) {
        if (queryArguments != null) queryArguments.setTableName(tableName);
        else queryArguments = new FlexicoreHashMap(tableName);

        QueryBuilder queryBuilder = new QueryBuilder();
        queryBuilder.deleteFrom(tableName);
        if (filterPredicate != null) queryBuilder.where(filterPredicate);
        return new QueryExecutor().delete(queryBuilder, queryArguments);
    }

    //update is a bit unique. The last query arguments are specifically for the WHERE clause for the update statement.
    public static TransactionWrapper<FlexicoreArrayList> update(String tableName,
                                                                FlexicoreHashMap updateSet,
                                                                FilterPredicate filterPredicate,
                                                                FlexicoreHashMap queryArguments) {
        TransactionWrapper<FlexicoreArrayList> twrapper = new TransactionWrapper<>();
        if (queryArguments != null) queryArguments.setTableName(tableName);
        else queryArguments = new FlexicoreHashMap(tableName);
        try {
            if (updateSet == null) {
                throw new Exception("UPDATE: Update set should not be null");
            }
            updateSet.removeColumn("date_created");
            updateSet.putValue("date_modified", new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT).format(new Date()));

            QueryBuilder queryBuilder = new QueryBuilder();
            HashMap<String, String> updateSetVariables = new HashMap<>();
            for (Map.Entry<String, Object> entry : updateSet.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                updateSetVariables.put(key, ":" + key);
                queryArguments.putValue(":" + key, value);
            }
            queryBuilder.update(tableName).set(updateSetVariables);
            if (filterPredicate != null) {
                queryBuilder.where(filterPredicate);
            }
            twrapper = new QueryExecutor().update(queryBuilder, new ArrayList<>(updateSetVariables.keySet()),
                    queryArguments);
        } catch (Exception e) {
            twrapper.setHasErrors(true);
            twrapper.addError(e.getMessage());
            e.printStackTrace();
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

            twrapper.addErrorStackTrace(sStackTrace);
        }
        return twrapper;
    }

    public static TransactionWrapper<FlexicoreArrayList> update(ExtendedConnection extendedConnection, String tableName,
                                                                FlexicoreHashMap updateSet,
                                                                FilterPredicate filterPredicate,
                                                                FlexicoreHashMap queryArguments) {
        TransactionWrapper<FlexicoreArrayList> twrapper = new TransactionWrapper<>();
        if (queryArguments != null) queryArguments.setTableName(tableName);
        else queryArguments = new FlexicoreHashMap(tableName);
        try {
            if (updateSet == null) {
                throw new Exception("UPDATE: Update set should not be null");
            }
            updateSet.removeColumn("date_created");
            updateSet.putValue("date_modified", new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT).format(new Date()));
            QueryBuilder queryBuilder = new QueryBuilder();
            HashMap<String, String> updateSetVariables = new HashMap<>();
            for (Map.Entry<String, Object> entry : updateSet.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                updateSetVariables.put(key, ":" + key);
                queryArguments.putValue(":" + key, value);
            }
            queryBuilder.update(tableName).set(updateSetVariables);
            if (filterPredicate != null) {
                queryBuilder.where(filterPredicate);
            }
            twrapper = new QueryExecutor().update(extendedConnection, queryBuilder, new ArrayList<>(updateSetVariables.keySet()),
                    queryArguments);
        } catch (Exception e) {
            twrapper.setHasErrors(true);
            twrapper.addError(e.getMessage());
            e.printStackTrace();
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

            twrapper.addErrorStackTrace(sStackTrace);
        }
        return twrapper;
    }

    public static TransactionWrapper<FlexicoreArrayList> update(ExtendedConnection extendedConnection, QueryBuilder queryBuilder,
                                                                FlexicoreHashMap updateSet, FlexicoreHashMap queryArguments) {
        TransactionWrapper<FlexicoreArrayList> twrapper = new TransactionWrapper<>();
        if (queryArguments != null) queryArguments.setTableName(queryBuilder.getTableName());
        else queryArguments = new FlexicoreHashMap(queryBuilder.getTableName());
        try {
            if (updateSet == null) {
                throw new Exception("UPDATE: Update set should not be null");
            }
            HashMap<String, String> updateSetVariables = new HashMap<>();
            for (Map.Entry<String, Object> entry : updateSet.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                updateSetVariables.put(key, ":" + key);
                queryArguments.putValue(":" + key, value);
            }
            twrapper = new QueryExecutor().update(extendedConnection, queryBuilder, new ArrayList<>(updateSetVariables.keySet()),
                    queryArguments);
        } catch (Exception e) {
            twrapper.setHasErrors(true);
            twrapper.addError(e.getMessage());
            e.printStackTrace();
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

            twrapper.addErrorStackTrace(sStackTrace);
        }
        return twrapper;
    }

    public static TransactionWrapper<HashSet> getPrimaryKeyColumns(String tableName) {
        return new QueryExecutor().getPrimaryKeyColumns(tableName);
    }

    public static TransactionWrapper<List<String>> getColumns(String tableName) {
        return new QueryExecutor().getColumns(tableName);
    }

    /************************************************   SELECT FUNCTIONS  ***************************************/
    public static TransactionWrapper joinSelectQuery(QueryBuilder queryBuilder) {
        return joinSelectQuery(queryBuilder, null, null);
    }

    public static TransactionWrapper joinSelectQuery(QueryBuilder queryBuilder, int[] pagePageSize) {
        return joinSelectQuery(queryBuilder, null, pagePageSize);
    }

    public static TransactionWrapper joinSelectQuery(QueryBuilder queryBuilder, FlexicoreHashMap queryArguments) {
        return joinSelectQuery(queryBuilder, queryArguments, null);
    }

    public static TransactionWrapper joinSelectQuery(QueryBuilder queryBuilder, FlexicoreHashMap queryArguments,
                                                     int[] pagePageSize) {
        TransactionWrapper twrapper = new TransactionWrapper();
        String tableName = queryBuilder.getTableName();
        if (queryArguments != null) queryArguments.setTableName(queryBuilder.getTableName());
        else queryArguments = new FlexicoreHashMap(queryBuilder.getTableName());

        try {
            QueryBuilder qrQueryBuilder = new QueryBuilder().rawQuery(queryBuilder.toString());
            /*if (pagePageSize != null) {
                validatePagePageSize(pagePageSize);
                queryArguments.addQueryArgument(":num_of_records", pagePageSize[1]);
                queryArguments.addQueryArgument(":offset", (pagePageSize[0] - 1) * pagePageSize[1]);
                qrQueryBuilder.limit();
            }*/
            if (pagePageSize != null) {
                validatePagePageSize(pagePageSize);
                if (pagePageSize[0] > 0) {
                    queryArguments.addQueryArgument(":num_of_records", pagePageSize[1]);
                    queryArguments.addQueryArgument(":offset", (pagePageSize[0] - 1) * pagePageSize[1]);
                    qrQueryBuilder.limit();
                }
            }

            twrapper = new QueryExecutor().select(qrQueryBuilder.toString(), queryArguments);
            if (pagePageSize != null) {
                twrapper.setData(
                        paginate(tableName, twrapper.getData(), joinCountQuery(queryBuilder, queryArguments),
                                pagePageSize[0],
                                pagePageSize[1]
                        ));
            }
        } catch (Exception e) {
            twrapper.setHasErrors(true);
            twrapper.addError(e.getMessage());
            e.printStackTrace();
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

            twrapper.addErrorStackTrace(sStackTrace);
        }
        return twrapper;
    }

    public static long joinCountQuery(QueryBuilder queryBuilder, FlexicoreHashMap queryArguments) {
        QueryBuilder countQueryBuilder = new QueryBuilder();
        countQueryBuilder.select()
                .selectColumn(new Column("*", "count", SQLEnums.Aggregate.COUNT))
                .from()
                .joinPhrase(queryBuilder.getJoinStatement());
        if (!queryBuilder.getWhereClause().isEmpty())
            countQueryBuilder.where(queryBuilder.getWhereClause());

        TransactionWrapper twrapper = new QueryExecutor().select(countQueryBuilder.toString(), queryArguments);
        FlexicoreArrayList flexicoreArrayList = (FlexicoreArrayList) twrapper.getData();
        FlexicoreHashMap flexicoreHashMap = flexicoreArrayList.get(0);
        return (long) flexicoreHashMap.get("count");
    }

    public static boolean joinExistsQuery(QueryBuilder queryBuilder) {
        FlexicoreHashMap queryArguments = new FlexicoreHashMap("JoinResult");

        QueryBuilder countQueryBuilder = new QueryBuilder();
        countQueryBuilder.select()
                .selectColumn(new Column("1"))
                .from()
                .joinPhrase(queryBuilder.getJoinStatement())
                .where(queryBuilder.getWhereClause());

        TransactionWrapper twrapper = new QueryExecutor().select(countQueryBuilder.toString(), queryArguments);
        FlexicoreArrayList flexicoreArrayList = (FlexicoreArrayList) twrapper.getData();
        return !(flexicoreArrayList == null || flexicoreArrayList.size() < 1);
    }

    /*select [columns] from table;*/
    /*-----------------------------------------------------------------------------------------------------------------------------------*/


    public static TransactionWrapper select(QueryBuilder queryBuilder) {
        return select(queryBuilder, null, null);
    }

    public static TransactionWrapper select(QueryBuilder queryBuilder, int[] pagePageSize) {
        return select(queryBuilder, null, pagePageSize);
    }

    public static TransactionWrapper select(QueryBuilder queryBuilder, FlexicoreHashMap queryArguments) {
        return select(queryBuilder, queryArguments, null);
    }

    public static TransactionWrapper select(QueryBuilder queryBuilder, FlexicoreHashMap queryArguments,
                                            int[] pagePageSize) {
        TransactionWrapper twrapper = new TransactionWrapper();
        String tableName = queryBuilder.getTableName();
        if (queryArguments != null) queryArguments.setTableName(queryBuilder.getTableName());
        else queryArguments = new FlexicoreHashMap(queryBuilder.getTableName());

        try {
            QueryBuilder qrQueryBuilder = new QueryBuilder().rawQuery(queryBuilder.toString());
            /*if (pagePageSize != null) {
                validatePagePageSize(pagePageSize);
                queryArguments.addQueryArgument(":num_of_records", pagePageSize[1]);
                queryArguments.addQueryArgument(":offset", (pagePageSize[0] - 1) * pagePageSize[1]);
                qrQueryBuilder.limit();
            }*/
            if (pagePageSize != null) {
                validatePagePageSize(pagePageSize);
                if (pagePageSize[0] > 0) {
                    queryArguments.addQueryArgument(":num_of_records", pagePageSize[1]);
                    queryArguments.addQueryArgument(":offset", (pagePageSize[0] - 1) * pagePageSize[1]);
                    qrQueryBuilder.limit();
                }
            }
            twrapper = new QueryExecutor().select(qrQueryBuilder.toString(), queryArguments);
            if (pagePageSize != null) {
                twrapper.setData(
                        paginate(tableName, twrapper.getData(), count(queryBuilder), pagePageSize[0],
                                pagePageSize[1]
                        ));
            }
        } catch (Exception e) {
            twrapper.setHasErrors(true);
            twrapper.addError(e.getMessage());
            e.printStackTrace();
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
            twrapper.addErrorStackTrace(sStackTrace);
        }
        return twrapper;
    }

    public static long count(QueryBuilder queryBuilder) {
        FlexicoreHashMap queryArguments = new FlexicoreHashMap(queryBuilder.getTableName());
        QueryBuilder countQueryBuilder = new QueryBuilder();
        countQueryBuilder.select()
                .selectColumn(new Column("*", "count", SQLEnums.Aggregate.COUNT))
                .from(queryBuilder.getTableName());
        if (!queryBuilder.getWhereClause().isEmpty())
            countQueryBuilder.where(queryBuilder.getWhereClause());

        TransactionWrapper twrapper = new QueryExecutor().select(countQueryBuilder.toString(), queryArguments);
        FlexicoreArrayList flexicoreArrayList = (FlexicoreArrayList) twrapper.getData();
        FlexicoreHashMap flexicoreHashMap = flexicoreArrayList.get(0);
        return (long) flexicoreHashMap.get("count");
    }

    public static long count(QueryBuilder queryBuilder, FlexicoreHashMap queryArguments) {
        QueryBuilder countQueryBuilder = new QueryBuilder();
        queryArguments.setTableName(queryBuilder.getTableName());
        countQueryBuilder.select()
                .selectColumn(new Column("*", "count", SQLEnums.Aggregate.COUNT))
                .from(queryBuilder.getTableName());
        if (!queryBuilder.getWhereClause().isEmpty())
            countQueryBuilder.where(queryBuilder.getWhereClause());

        TransactionWrapper twrapper = new QueryExecutor().select(countQueryBuilder.toString(), queryArguments);
        FlexicoreArrayList flexicoreArrayList = (FlexicoreArrayList) twrapper.getData();
        FlexicoreHashMap flexicoreHashMap = flexicoreArrayList.get(0);
        return (long) flexicoreHashMap.get("count");
    }

    public static boolean exists(QueryBuilder queryBuilder, FlexicoreHashMap queryArguments) {
        //FlexicoreHashMap queryArguments = new FlexicoreHashMap(queryBuilder.getTableName());
        QueryBuilder countQueryBuilder = new QueryBuilder();
        countQueryBuilder.select()
                .selectColumn(new Column("1"))
                .from(queryBuilder.getTableName())
                .where(queryBuilder.getWhereClause());

        TransactionWrapper twrapper = new QueryExecutor().select(countQueryBuilder.toString(), queryArguments);
        FlexicoreArrayList flexicoreArrayList = (FlexicoreArrayList) twrapper.getData();
        return !(flexicoreArrayList == null || flexicoreArrayList.size() < 1);
    }

    public static TransactionWrapper select(String tableName) {
        return select(tableName, (Column[]) null, FETCH_STRATEGY_LAZY, null);
    }

    public static TransactionWrapper select(String tableName, int[] pagePageSize) {
        return select(tableName, (Column[]) null, FETCH_STRATEGY_LAZY, pagePageSize);
    }

    public static TransactionWrapper selectWithFetchStrategy(String tableName, String fetchStrategy, int[] pagePageSize) {
        return select(tableName, (Column[]) null, fetchStrategy, pagePageSize);
    }

    public static TransactionWrapper selectOrderBy(String tableName, String orderByPhrase, String fetchStrategy, int[] pagePageSize) {
        return selectOrderBy(tableName, (Column[]) null, orderByPhrase, fetchStrategy, pagePageSize);
    }

    public static TransactionWrapper select(String tableName, String columns) {
        return select(tableName, formColumnArrayFromStringArray(columns.split(",")), FETCH_STRATEGY_LAZY, null);
    }

    public static TransactionWrapper select(String tableName, String columns, int[] pagePageSize) {
        return select(tableName, formColumnArrayFromStringArray(columns.split(",")), FETCH_STRATEGY_LAZY, pagePageSize);
    }

    public static TransactionWrapper select(String tableName, Column column) {
        return select(tableName, new Column[]{column}, FETCH_STRATEGY_LAZY, null);
    }

    public static TransactionWrapper select(String tableName, Column column, int[] pagePageSize) {
        return select(tableName, new Column[]{column}, FETCH_STRATEGY_LAZY, pagePageSize);
    }

    public static TransactionWrapper select(String tableName, Column[] columns) {
        return select(tableName, columns, FETCH_STRATEGY_LAZY, null);
    }

    public static TransactionWrapper select(String tableName, Column[] columns, String fetchStrategy, int[] pagePageSize) {
        TransactionWrapper twrapper = new TransactionWrapper();
        FlexicoreHashMap queryArguments = new FlexicoreHashMap(tableName);
        QueryBuilder queryBuilder = new QueryBuilder();
        try {
            queryBuilder.select();
            if (columns != null) {
                validateFetchColumns(columns);
                for (Column column : columns) queryBuilder.selectColumn(column);
            }
            queryBuilder.from(tableName);

            queryBuilder.orderBy(getOrderByField(tableName));

            /*if (pagePageSize != null) {
                validatePagePageSize(pagePageSize);
                queryArguments.addQueryArgument(":num_of_records", pagePageSize[1]);
                queryArguments.addQueryArgument(":offset", (pagePageSize[0] - 1) * pagePageSize[1]);
                queryBuilder.limit();
            }*/
            if (pagePageSize != null) {
                validatePagePageSize(pagePageSize);
                if (pagePageSize[0] > 0) {
                    queryArguments.addQueryArgument(":num_of_records", pagePageSize[1]);
                    queryArguments.addQueryArgument(":offset", (pagePageSize[0] - 1) * pagePageSize[1]);
                    queryBuilder.limit();
                }
            }

            twrapper = new TransactionWrapper<>();

            if(!fetchStrategy.equals(FETCH_STRATEGY_COUNT)){
                twrapper = new QueryExecutor().select(queryBuilder.toString(), queryArguments);
            }

            if (pagePageSize != null) {

                switch (fetchStrategy) {
                    case FETCH_STRATEGY_COUNT: {
                        twrapper.setData(paginate(tableName, new FlexicoreArrayList(), count(tableName), 0, 0
                        ));
                        break;
                    }

                    case FETCH_STRATEGY_LAZY: {
                        twrapper.setData(paginate(tableName, twrapper.getData(), 0, pagePageSize[0], pagePageSize[1]));
                        break;
                    }

                    case FETCH_STRATEGY_EAGER:
                    default: {
                        twrapper.setData(
                                paginate(tableName, twrapper.getData(), count(tableName), pagePageSize[0],
                                        pagePageSize[1]
                                ));
                    }
                }
            }

        } catch (Exception e) {
            twrapper.setHasErrors(true);
            twrapper.addError(e.getMessage());
            e.printStackTrace();
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

            twrapper.addErrorStackTrace(sStackTrace);
        }
        return twrapper;
    }

    public static TransactionWrapper selectOrderBy(String tableName, Column[] columns, String orderByPhrase, String fetchStrategy, int[] pagePageSize) {
        TransactionWrapper twrapper = new TransactionWrapper();
        FlexicoreHashMap queryArguments = new FlexicoreHashMap(tableName);
        QueryBuilder queryBuilder = new QueryBuilder();
        try {
            queryBuilder.select();
            if (columns != null) {
                validateFetchColumns(columns);
                for (Column column : columns) queryBuilder.selectColumn(column);
            }
            queryBuilder.from(tableName);

            queryBuilder.orderBy(orderByPhrase);

            /*if (pagePageSize != null) {
                validatePagePageSize(pagePageSize);
                queryArguments.addQueryArgument(":num_of_records", pagePageSize[1]);
                queryArguments.addQueryArgument(":offset", (pagePageSize[0] - 1) * pagePageSize[1]);
                queryBuilder.limit();
            }*/
            if (pagePageSize != null) {
                validatePagePageSize(pagePageSize);
                if (pagePageSize[0] > 0) {
                    queryArguments.addQueryArgument(":num_of_records", pagePageSize[1]);
                    queryArguments.addQueryArgument(":offset", (pagePageSize[0] - 1) * pagePageSize[1]);
                    queryBuilder.limit();
                }
            }

            twrapper = new TransactionWrapper<>();

            if(!fetchStrategy.equals(FETCH_STRATEGY_COUNT)){
                twrapper = new QueryExecutor().select(queryBuilder.toString(), queryArguments);
            }

            if (pagePageSize != null) {

                switch (fetchStrategy) {
                    case FETCH_STRATEGY_COUNT: {
                        twrapper.setData(paginate(tableName, new FlexicoreArrayList(), count(tableName), 0, 0
                        ));
                        break;
                    }

                    case FETCH_STRATEGY_LAZY: {
                        twrapper.setData(paginate(tableName, twrapper.getData(), 0, pagePageSize[0], pagePageSize[1]));
                        break;
                    }

                    case FETCH_STRATEGY_EAGER:
                    default: {
                        twrapper.setData(
                                paginate(tableName, twrapper.getData(), count(tableName), pagePageSize[0],
                                        pagePageSize[1]
                                ));
                    }
                }
            }

        } catch (Exception e) {
            twrapper.setHasErrors(true);
            twrapper.addError(e.getMessage());
            e.printStackTrace();
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

            twrapper.addErrorStackTrace(sStackTrace);
        }
        return twrapper;
    }
    /*-----------------------------------------------------------------------------------------------------------------------------------*/

    /*-----------------------------------------------------------------------------------------------------------------------------------*/
    public static TransactionWrapper selectOrderBy(String tableName, ColumnOrderBy columnOrderBy) {
        return selectOrderBy(tableName, (Column[]) null, new ColumnOrderBy[]{columnOrderBy}, null);
    }

    public static TransactionWrapper selectOrderBy(String tableName, ColumnOrderBy columnOrderBy,
                                                   int[] pagePageSize) {
        return selectOrderBy(tableName, (Column[]) null, new ColumnOrderBy[]{columnOrderBy},
                pagePageSize
        );
    }

    public static TransactionWrapper selectOrderBy(String tableName,
                                                   ColumnOrderBy[] columnsOrderBy) {
        return selectOrderBy(tableName, (Column[]) null, columnsOrderBy, null);
    }

    public static TransactionWrapper selectOrderBy(String tableName,
                                                   ColumnOrderBy[] columnsOrderBy,
                                                   int[] pagePageSize) {
        return selectOrderBy(tableName, (Column[]) null, columnsOrderBy, pagePageSize);
    }

    public static TransactionWrapper selectOrderBy(String tableName, String columns,
                                                   ColumnOrderBy columnOrderBy) {
        return selectOrderBy(tableName, formColumnArrayFromStringArray(columns.split(",")),
                new ColumnOrderBy[]{columnOrderBy}, null);
    }

    public static TransactionWrapper selectOrderBy(String tableName, String columns,
                                                   ColumnOrderBy[] columnsOrderBy) {
        return selectOrderBy(tableName, formColumnArrayFromStringArray(columns.split(",")), columnsOrderBy, null);
    }

    public static TransactionWrapper selectOrderBy(String tableName, String columns,
                                                   ColumnOrderBy[] columnsOrderBy, int[] pagePageSize) {
        return selectOrderBy(tableName, formColumnArrayFromStringArray(columns.split(",")), columnsOrderBy, pagePageSize);
    }

    public static TransactionWrapper selectOrderBy(String tableName, Column column,
                                                   ColumnOrderBy columnOrderBy) {
        return selectOrderBy(tableName, new Column[]{column}, new ColumnOrderBy[]{columnOrderBy}, null);
    }

    public static TransactionWrapper selectOrderBy(String tableName, Column column,
                                                   ColumnOrderBy[] columnsOrderBy) {
        return selectOrderBy(tableName, new Column[]{column}, columnsOrderBy, null);
    }

    public static TransactionWrapper selectOrderBy(String tableName, Column column,
                                                   ColumnOrderBy[] columnsOrderBy, int[] pagePageSize) {
        return selectOrderBy(tableName, new Column[]{column}, columnsOrderBy, pagePageSize);
    }

    public static TransactionWrapper selectOrderBy(String tableName, Column[] columns,
                                                   ColumnOrderBy columnOrderBy) {
        return selectOrderBy(tableName, columns, new ColumnOrderBy[]{columnOrderBy}, null);
    }

    public static TransactionWrapper selectOrderBy(String tableName, Column[] columns,
                                                   ColumnOrderBy[] columnsOrderBy) {
        return selectOrderBy(tableName, columns, columnsOrderBy, null);
    }

    public static TransactionWrapper selectOrderBy(String tableName, Column[] columns,
                                                   ColumnOrderBy[] columnOrderBIES, int[] pagePageSize) {
        TransactionWrapper twrapper = new TransactionWrapper();
        FlexicoreHashMap queryArguments = new FlexicoreHashMap(tableName);
        try {
            QueryBuilder queryBuilder = new QueryBuilder();
            queryBuilder.select();
            if (columns != null) {
                validateFetchColumns(columns);
                for (Column column : columns) queryBuilder.selectColumn(column);
            }
            queryBuilder.from(tableName);
            if (columnOrderBIES != null) {
                queryBuilder.orderBy(columnOrderBIES);
            } else {
                queryBuilder.orderBy(getOrderByField(tableName));
            }
            /*if (pagePageSize != null) {
                validatePagePageSize(pagePageSize);
                queryArguments.addQueryArgument(":num_of_records", pagePageSize[1]);
                queryArguments.addQueryArgument(":offset", (pagePageSize[0] - 1) * pagePageSize[1]);
                queryBuilder.limit();
            }*/
            if (pagePageSize != null) {
                validatePagePageSize(pagePageSize);
                if (pagePageSize[0] > 0) {
                    queryArguments.addQueryArgument(":num_of_records", pagePageSize[1]);
                    queryArguments.addQueryArgument(":offset", (pagePageSize[0] - 1) * pagePageSize[1]);
                    queryBuilder.limit();
                }
            }
            twrapper = new QueryExecutor().select(queryBuilder.toString(), queryArguments);
            if (pagePageSize != null) {
                twrapper.setData(
                        paginate(tableName, twrapper.getData(), count(tableName), pagePageSize[0],
                                pagePageSize[1]
                        ));
            }
        } catch (Exception e) {
            twrapper.setHasErrors(true);
            twrapper.addError(e.getMessage());
            e.printStackTrace();
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

            twrapper.addErrorStackTrace(sStackTrace);
        }
        return twrapper;
    }

    /*-----------------------------------------------------------------------------------------------------------------------------------*/

    public static long count(String tableName) {
        TransactionWrapper twrapper = new TransactionWrapper();
        try {
            QueryBuilder queryBuilder = new QueryBuilder();
            queryBuilder.select().selectColumn(new Column("*", "count", SQLEnums.Aggregate.COUNT)).from(tableName);
            twrapper = new QueryExecutor().select(queryBuilder.toString(), new FlexicoreHashMap(tableName));
        } catch (Exception e) {
            twrapper.setHasErrors(true);
            twrapper.addError(e.getMessage());
            e.printStackTrace();
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

            twrapper.addErrorStackTrace(sStackTrace);
        }
        FlexicoreArrayList flexicoreArrayList = (FlexicoreArrayList) twrapper.getData();
        FlexicoreHashMap flexicoreHashMap = flexicoreArrayList.get(0);
        return (long) flexicoreHashMap.get("count");
    }


    public static boolean exists(String tableName, FilterPredicate whereFilterPredicate,
                                 FlexicoreHashMap queryArguments) {
        TransactionWrapper twrapper = new TransactionWrapper();
        if (queryArguments != null) {
            queryArguments.setTableName(tableName);
        } else {
            queryArguments = new FlexicoreHashMap(tableName);
        }
        try {
            QueryBuilder queryBuilder = new QueryBuilder();
            queryBuilder.select().selectColumn(new Column("1")).from(tableName).where(whereFilterPredicate);
            twrapper = new QueryExecutor().select(queryBuilder.toString(), queryArguments);
        } catch (Exception e) {
            twrapper.setHasErrors(true);
            twrapper.addError(e.getMessage());
            e.printStackTrace();
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

            twrapper.addErrorStackTrace(sStackTrace);
        }
        FlexicoreArrayList flexicoreArrayList = (FlexicoreArrayList) twrapper.getData();
        return !(flexicoreArrayList == null || flexicoreArrayList.size() < 1);
    }

    /*---------------------------------------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------------------------------------*/
    /*select [columns] from table [group by][having (if needed)];*/
    public static TransactionWrapper selectGroupBy(String tableName, String columns,
                                                   String groupByColumns) {
        return selectGroupBy(tableName, formColumnArrayFromStringArray(columns.split(",")), groupByColumns,
                null, null, null
        );
    }

    public static TransactionWrapper selectGroupBy(String tableName, String columns,
                                                   String groupByColumns,
                                                   int[] pagePageSize) {
        return selectGroupBy(tableName, formColumnArrayFromStringArray(columns.split(",")), groupByColumns,
                null, null,
                pagePageSize
        );
    }

    public static TransactionWrapper selectGroupBy(String tableName, String columns,
                                                   String groupByColumns,
                                                   FilterPredicate havingFilterPredicate,
                                                   FlexicoreHashMap queryArguments) {
        return selectGroupBy(tableName, formColumnArrayFromStringArray(columns.split(",")), groupByColumns,
                havingFilterPredicate, queryArguments, null
        );
    }

    public static TransactionWrapper selectGroupBy(String tableName, String columns,
                                                   String groupByColumns,
                                                   FilterPredicate havingFilterPredicate,
                                                   FlexicoreHashMap queryArguments,
                                                   int[] pagePageSize) {
        return selectGroupBy(tableName, formColumnArrayFromStringArray(columns.split(",")), groupByColumns,
                havingFilterPredicate, queryArguments, pagePageSize
        );
    }

    public static TransactionWrapper selectGroupBy(String tableName, Column column,
                                                   String groupByColumns) {
        return selectGroupBy(tableName, new Column[]{column}, groupByColumns, null, null,
                null
        );
    }

    public static TransactionWrapper selectGroupBy(String tableName, Column column,
                                                   String groupByColumns,
                                                   int[] pagePageSize) {
        return selectGroupBy(tableName, new Column[]{column}, groupByColumns, null, null,
                pagePageSize
        );
    }

    public static TransactionWrapper selectGroupBy(String tableName, Column column,
                                                   String groupByColumns,
                                                   FilterPredicate havingFilterPredicate,
                                                   FlexicoreHashMap queryArguments) {
        return selectGroupBy(tableName, new Column[]{column}, groupByColumns,
                havingFilterPredicate, queryArguments, null
        );
    }

    public static TransactionWrapper selectGroupBy(String tableName, Column column,
                                                   String groupByColumns,
                                                   FilterPredicate havingFilterPredicate,
                                                   FlexicoreHashMap queryArguments,
                                                   int[] pagePageSize) {
        return selectGroupBy(tableName, new Column[]{column}, groupByColumns,
                havingFilterPredicate, queryArguments, pagePageSize
        );
    }

    public static TransactionWrapper selectGroupBy(String tableName, Column[] columns,
                                                   String groupByColumns) {
        return selectGroupBy(tableName, columns, groupByColumns, null, null, null);
    }

    public static TransactionWrapper selectGroupBy(String tableName, Column[] columns,
                                                   String groupByColumns,
                                                   int[] pagePageSize) {
        return selectGroupBy(tableName, columns, groupByColumns, null, null, pagePageSize);
    }

    public static TransactionWrapper selectGroupBy(String tableName, Column[] columns,
                                                   String groupByColumns,
                                                   FilterPredicate havingFilterPredicate,
                                                   FlexicoreHashMap queryArguments) {
        return selectGroupBy(tableName, columns, groupByColumns, havingFilterPredicate,
                queryArguments, null
        );
    }

    public static TransactionWrapper selectGroupBy(String tableName, Column[] columns,
                                                   String groupByColumns,
                                                   FilterPredicate havingFilterPredicate,
                                                   FlexicoreHashMap queryArguments,
                                                   int[] pagePageSize) {
        TransactionWrapper twrapper = new TransactionWrapper();
        if (queryArguments != null) {
            queryArguments.setTableName(tableName);
        } else {
            queryArguments = new FlexicoreHashMap(tableName);
        }
        try {
            QueryBuilder queryBuilder = new QueryBuilder();
            queryBuilder.select();
            if (columns != null) {
                validateFetchColumns(columns);
                for (Column column : columns) queryBuilder.selectColumn(column);
            }
            queryBuilder.from(tableName);
            if (groupByColumns != null) {
                queryBuilder.groupBy(groupByColumns);
            }
            if (havingFilterPredicate != null) {
                queryBuilder.having(havingFilterPredicate);
            }

            queryBuilder.orderBy(getOrderByField(tableName));

            /*if (pagePageSize != null) {
                validatePagePageSize(pagePageSize);
                queryArguments.addQueryArgument(":num_of_records", pagePageSize[1]);
                queryArguments.addQueryArgument(":offset", (pagePageSize[0] - 1) * pagePageSize[1]);
                queryBuilder.limit();
            }*/
            if (pagePageSize != null) {
                validatePagePageSize(pagePageSize);
                if (pagePageSize[0] > 0) {
                    queryArguments.addQueryArgument(":num_of_records", pagePageSize[1]);
                    queryArguments.addQueryArgument(":offset", (pagePageSize[0] - 1) * pagePageSize[1]);
                    queryBuilder.limit();
                }
            }
            twrapper = new QueryExecutor().select(queryBuilder.toString(), queryArguments);
            if (pagePageSize != null) {
                twrapper.setData(paginate(tableName, twrapper.getData(),
                        countGroupBy(tableName, groupByColumns, havingFilterPredicate,
                                queryArguments
                        ), pagePageSize[0], pagePageSize[1]
                ));
            }
        } catch (Exception e) {
            twrapper.setHasErrors(true);
            twrapper.addError(e.getMessage());
            e.printStackTrace();
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

            twrapper.addErrorStackTrace(sStackTrace);
        }
        return twrapper;
    }
    /*---------------------------------------------------------------------------------------------------------------------------*/
    /*--------------------------------------------------------------------------------------------------------------------------*/
    /*select [columns] from table [group by][having (if needed)];*/

    public static TransactionWrapper selectGroupByOrderBy(String tableName, String columns,
                                                          String groupByColumns,
                                                          ColumnOrderBy columnOrderBy) {
        return selectGroupByOrderBy(tableName, formColumnArrayFromStringArray(columns.split(",")),
                groupByColumns, null,
                new ColumnOrderBy[]{columnOrderBy}, null, null
        );
    }

    public static TransactionWrapper selectGroupByOrderBy(String tableName, String columns,
                                                          String groupByColumns,
                                                          ColumnOrderBy columnOrderBy, int[] pagePageSize) {
        return selectGroupByOrderBy(tableName, formColumnArrayFromStringArray(columns.split(",")),
                groupByColumns, null,
                new ColumnOrderBy[]{columnOrderBy}, null, pagePageSize
        );
    }

    public static TransactionWrapper selectGroupByOrderBy(String tableName, String columns,
                                                          String groupByColumns,
                                                          FilterPredicate havingFilterPredicate,
                                                          ColumnOrderBy columnOrderBy,
                                                          FlexicoreHashMap queryArguments) {
        return selectGroupByOrderBy(tableName, formColumnArrayFromStringArray(columns.split(",")),
                groupByColumns,
                havingFilterPredicate, new ColumnOrderBy[]{columnOrderBy}, queryArguments, null
        );
    }

    public static TransactionWrapper selectGroupByOrderBy(String tableName, String columns,
                                                          String groupByColumns,
                                                          FilterPredicate havingFilterPredicate,
                                                          ColumnOrderBy columnOrderBy,
                                                          FlexicoreHashMap queryArguments, int[] pagePageSize) {
        return selectGroupByOrderBy(tableName, formColumnArrayFromStringArray(columns.split(",")),
                groupByColumns,
                havingFilterPredicate, new ColumnOrderBy[]{columnOrderBy}, queryArguments,
                pagePageSize
        );
    }

    public static TransactionWrapper selectGroupByOrderBy(String tableName, String columns,
                                                          String groupByColumns,
                                                          ColumnOrderBy[] columnOrderBIES) {
        return selectGroupByOrderBy(tableName, formColumnArrayFromStringArray(columns.split(",")),
                groupByColumns, null,
                columnOrderBIES, null, null
        );
    }

    public static TransactionWrapper selectGroupByOrderBy(String tableName, String columns,
                                                          String groupByColumns,
                                                          ColumnOrderBy[] columnOrderBIES, int[] pagePageSize) {
        return selectGroupByOrderBy(tableName, formColumnArrayFromStringArray(columns.split(",")),
                groupByColumns, null,
                columnOrderBIES, null, pagePageSize
        );
    }

    public static TransactionWrapper selectGroupByOrderBy(String tableName, String columns,
                                                          String groupByColumns,
                                                          FilterPredicate havingFilterPredicate,
                                                          ColumnOrderBy[] columnOrderBIES,
                                                          FlexicoreHashMap queryArguments) {
        return selectGroupByOrderBy(tableName, formColumnArrayFromStringArray(columns.split(",")),
                groupByColumns,
                havingFilterPredicate, columnOrderBIES, queryArguments, null
        );
    }

    public static TransactionWrapper selectGroupByOrderBy(String tableName, String columns,
                                                          String groupByColumns,
                                                          FilterPredicate havingFilterPredicate,
                                                          ColumnOrderBy[] columnOrderBIES,
                                                          FlexicoreHashMap queryArguments, int[] pagePageSize) {
        return selectGroupByOrderBy(tableName, formColumnArrayFromStringArray(columns.split(",")),
                groupByColumns,
                havingFilterPredicate, columnOrderBIES, queryArguments, pagePageSize
        );
    }

    public static TransactionWrapper selectGroupByOrderBy(String tableName, Column column,
                                                          String groupByColumns,
                                                          ColumnOrderBy columnOrderBy) {
        return selectGroupByOrderBy(tableName, new Column[]{column}, groupByColumns, null,
                new ColumnOrderBy[]{columnOrderBy}, null, null
        );
    }

    public static TransactionWrapper selectGroupByOrderBy(String tableName, Column column,
                                                          String groupByColumns,
                                                          ColumnOrderBy columnOrderBy, int[] pagePageSize) {
        return selectGroupByOrderBy(tableName, new Column[]{column}, groupByColumns, null,
                new ColumnOrderBy[]{columnOrderBy}, null, pagePageSize
        );
    }

    public static TransactionWrapper selectGroupByOrderBy(String tableName, Column column,
                                                          String groupByColumns,
                                                          FilterPredicate havingFilterPredicate,
                                                          ColumnOrderBy columnOrderBy,
                                                          FlexicoreHashMap queryArguments) {
        return selectGroupByOrderBy(tableName, new Column[]{column}, groupByColumns,
                havingFilterPredicate, new ColumnOrderBy[]{columnOrderBy}, queryArguments, null
        );
    }

    public static TransactionWrapper selectGroupByOrderBy(String tableName, Column column,
                                                          String groupByColumns,
                                                          FilterPredicate havingFilterPredicate,
                                                          ColumnOrderBy columnOrderBy,
                                                          FlexicoreHashMap queryArguments, int[] pagePageSize) {
        return selectGroupByOrderBy(tableName, new Column[]{column}, groupByColumns,
                havingFilterPredicate, new ColumnOrderBy[]{columnOrderBy}, queryArguments,
                pagePageSize
        );
    }

    public static TransactionWrapper selectGroupByOrderBy(String tableName, Column column,
                                                          String groupByColumns,
                                                          ColumnOrderBy[] columnOrderBIES) {
        return selectGroupByOrderBy(tableName, new Column[]{column}, groupByColumns, null,
                columnOrderBIES, null, null
        );
    }

    public static TransactionWrapper selectGroupByOrderBy(String tableName, Column column,
                                                          String groupByColumns,
                                                          ColumnOrderBy[] columnOrderBIES, int[] pagePageSize) {
        return selectGroupByOrderBy(tableName, new Column[]{column}, groupByColumns, null,
                columnOrderBIES, null, pagePageSize
        );
    }

    public static TransactionWrapper selectGroupByOrderBy(String tableName, Column column,
                                                          String groupByColumns,
                                                          FilterPredicate havingFilterPredicate,
                                                          ColumnOrderBy[] columnOrderBIES,
                                                          FlexicoreHashMap queryArguments) {
        return selectGroupByOrderBy(tableName, new Column[]{column}, groupByColumns,
                havingFilterPredicate, columnOrderBIES, queryArguments, null
        );
    }

    public static TransactionWrapper selectGroupByOrderBy(String tableName, Column column,
                                                          String groupByColumns,
                                                          FilterPredicate havingFilterPredicate,
                                                          ColumnOrderBy[] columnOrderBIES,
                                                          FlexicoreHashMap queryArguments, int[] pagePageSize) {
        return selectGroupByOrderBy(tableName, new Column[]{column}, groupByColumns,
                havingFilterPredicate, columnOrderBIES, queryArguments, pagePageSize
        );
    }

    public static TransactionWrapper selectGroupByOrderBy(String tableName, Column[] columns,
                                                          String groupByColumns,
                                                          ColumnOrderBy columnOrderBy) {
        return selectGroupByOrderBy(tableName, columns, groupByColumns, null,
                new ColumnOrderBy[]{columnOrderBy}, null, null
        );
    }

    public static TransactionWrapper selectGroupByOrderBy(String tableName, Column[] columns,
                                                          String groupByColumns,
                                                          ColumnOrderBy columnOrderBy, int[] pagePageSize) {
        return selectGroupByOrderBy(tableName, columns, groupByColumns, null,
                new ColumnOrderBy[]{columnOrderBy}, null, pagePageSize
        );
    }

    public static TransactionWrapper selectGroupByOrderBy(String tableName, Column[] columns,
                                                          String groupByColumns,
                                                          FilterPredicate havingFilterPredicate,
                                                          ColumnOrderBy columnOrderBy,
                                                          FlexicoreHashMap queryArguments) {
        return selectGroupByOrderBy(tableName, columns, groupByColumns,
                havingFilterPredicate, new ColumnOrderBy[]{columnOrderBy}, queryArguments, null
        );
    }

    public static TransactionWrapper selectGroupByOrderBy(String tableName, Column[] columns,
                                                          String groupByColumns,
                                                          FilterPredicate havingFilterPredicate,
                                                          ColumnOrderBy columnOrderBy,
                                                          FlexicoreHashMap queryArguments, int[] pagePageSize) {
        return selectGroupByOrderBy(tableName, columns, groupByColumns,
                havingFilterPredicate, new ColumnOrderBy[]{columnOrderBy}, queryArguments,
                pagePageSize
        );
    }

    @SuppressWarnings("unchecked")
    public static TransactionWrapper selectGroupByOrderBy(String tableName, Column[] columns,
                                                          String groupByColumns,
                                                          FilterPredicate havingFilterPredicate,
                                                          ColumnOrderBy[] columnOrderBIES,
                                                          FlexicoreHashMap queryArguments, int[] pagePageSize) {
        TransactionWrapper twrapper = new TransactionWrapper();
        if (queryArguments != null) {
            queryArguments.setTableName(tableName);
        } else {
            queryArguments = new FlexicoreHashMap(tableName);
        }
        try {
            QueryBuilder queryBuilder = new QueryBuilder();
            queryBuilder.select();
            if (columns != null) {
                validateFetchColumns(columns);
                for (Column column : columns) queryBuilder.selectColumn(column);
            }
            queryBuilder.from(tableName);
            if (groupByColumns != null) {
                queryBuilder.groupBy(groupByColumns);
            }
            if (havingFilterPredicate != null) {
                queryBuilder.having(havingFilterPredicate);
            }
            if (columnOrderBIES != null) {
                queryBuilder.orderBy(columnOrderBIES);
            } else {
                queryBuilder.orderBy(getOrderByField(tableName));
            }
            /*if (pagePageSize != null) {
                validatePagePageSize(pagePageSize);
                queryArguments.addQueryArgument(":num_of_records", pagePageSize[1]);
                queryArguments.addQueryArgument(":offset", (pagePageSize[0] - 1) * pagePageSize[1]);
                queryBuilder.limit();
            }*/
            if (pagePageSize != null) {
                validatePagePageSize(pagePageSize);
                if (pagePageSize[0] > 0) {
                    queryArguments.addQueryArgument(":num_of_records", pagePageSize[1]);
                    queryArguments.addQueryArgument(":offset", (pagePageSize[0] - 1) * pagePageSize[1]);
                    queryBuilder.limit();
                }
            }
            twrapper = new QueryExecutor().select(queryBuilder.toString(), queryArguments);
            if (pagePageSize != null) {
                twrapper.setData(paginate(tableName, twrapper.getData(),
                        countGroupBy(tableName, groupByColumns, havingFilterPredicate,
                                queryArguments
                        ), pagePageSize[0], pagePageSize[1]
                ));
            }
        } catch (Exception e) {
            twrapper.setHasErrors(true);
            twrapper.addError(e.getMessage());
            e.printStackTrace();
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

            twrapper.addErrorStackTrace(sStackTrace);
        }
        return twrapper;
    }

    /****************************************************************************************************/
    public static long countGroupBy(String tableName, String groupByColumns) {
        return countGroupBy(tableName, groupByColumns, null, null);
    }

    public static long countGroupBy(String tableName, String groupByColumns,
                                    FilterPredicate havingFilterPredicate,
                                    FlexicoreHashMap queryArguments) {
        TransactionWrapper twrapper = new TransactionWrapper();
        if (queryArguments != null) {
            queryArguments.setTableName(tableName);
        } else {
            queryArguments = new FlexicoreHashMap(tableName);
        }
        try {
            QueryBuilder queryBuilder = new QueryBuilder();
            queryBuilder.select().selectColumn(new Column("*", "count", SQLEnums.Aggregate.COUNT)).from(tableName);
            if (groupByColumns != null) {
                queryBuilder.groupBy(groupByColumns);
            }
            if (havingFilterPredicate != null) {
                queryBuilder.having(havingFilterPredicate);
            }
            twrapper = new QueryExecutor().select(queryBuilder.toString(), queryArguments);
        } catch (Exception e) {
            twrapper.setHasErrors(true);
            twrapper.addError(e.getMessage());
            e.printStackTrace();
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

            twrapper.addErrorStackTrace(sStackTrace);
        }
        FlexicoreArrayList flexicoreArrayList = (FlexicoreArrayList) twrapper.getData();
        return (long) flexicoreArrayList.size();

    }

    /****************************************************************************************************/
    /*select [columns] from table [where clause];*/
    public static TransactionWrapper selectWhere(String tableName,
                                                 FilterPredicate whereFilterPredicate,
                                                 FlexicoreHashMap queryArguments) {
        return selectWhere(tableName, (Column[]) null, whereFilterPredicate, queryArguments, FETCH_STRATEGY_LAZY, null);
    }

    public static TransactionWrapper selectWhereWithJoinPhrase(String tableName, Column[] columns, String joinPhrase,
                                                               String joinPhrasePrefix,
                                                               FilterPredicate whereFilterPredicate,
                                                               FlexicoreHashMap queryArguments, String fetchStrategy, int[] pagePageSize) {

        return selectWhere(tableName, columns, joinPhrase, joinPhrasePrefix, whereFilterPredicate, queryArguments,
                fetchStrategy, pagePageSize
        );
    }

    public static TransactionWrapper selectWhereWithJoinPhraseOrderBy(String tableName, Column[] columns, String joinPhrase,
                                                               String joinPhrasePrefix,
                                                               FilterPredicate whereFilterPredicate, String orderByPhrase,
                                                               FlexicoreHashMap queryArguments, String fetchStrategy, int[] pagePageSize) {

        return selectWhereOrderBy(tableName, columns, joinPhrase, joinPhrasePrefix, whereFilterPredicate, orderByPhrase, queryArguments,
                fetchStrategy, pagePageSize
        );
    }

    public static TransactionWrapper selectWhere(String tableName,
                                                 FilterPredicate whereFilterPredicate,
                                                 FlexicoreHashMap queryArguments, String fetchStrategy, int[] pagePageSize) {
        return selectWhere(tableName, (Column[]) null, whereFilterPredicate, queryArguments,
                fetchStrategy, pagePageSize
        );
    }

    public static TransactionWrapper selectWhereOrderBy(String tableName,
                                                 FilterPredicate whereFilterPredicate, String orderByPhrase,
                                                 FlexicoreHashMap queryArguments, String fetchStrategy, int[] pagePageSize) {
        return selectWhereOrderBy(tableName, (Column[]) null, whereFilterPredicate, orderByPhrase, queryArguments, fetchStrategy,
                pagePageSize
        );
    }

    public static TransactionWrapper selectWhere(String tableName, String columns,
                                                 FilterPredicate whereFilterPredicate,
                                                 FlexicoreHashMap queryArguments) {
        return selectWhere(tableName, formColumnArrayFromStringArray(columns.split(",")), whereFilterPredicate,
                queryArguments, FETCH_STRATEGY_LAZY, null);
    }

    public static TransactionWrapper selectWhere(String tableName, String columns,
                                                 FilterPredicate whereFilterPredicate, FlexicoreHashMap queryArguments,
                                                 int[] pagePageSize) {
        return selectWhere(tableName, formColumnArrayFromStringArray(columns.split(",")), whereFilterPredicate,
                queryArguments, FETCH_STRATEGY_LAZY,
                pagePageSize
        );
    }

    public static TransactionWrapper selectWhere(String tableName, Column column,
                                                 FilterPredicate whereFilterPredicate,
                                                 FlexicoreHashMap queryArguments) {
        return selectWhere(tableName, new Column[]{column}, whereFilterPredicate, queryArguments, FETCH_STRATEGY_LAZY, null);
    }

    public static TransactionWrapper selectWhere(String tableName, Column column,
                                                 FilterPredicate whereFilterPredicate, FlexicoreHashMap queryArguments,
                                                 int[] pagePageSize) {
        return selectWhere(tableName, new Column[]{column}, whereFilterPredicate, queryArguments, FETCH_STRATEGY_LAZY,
                pagePageSize
        );
    }

    public static TransactionWrapper selectWhere(String tableName, Column[] columns,
                                                 FilterPredicate whereFilterPredicate,
                                                 FlexicoreHashMap queryArguments) {
        return selectWhere(tableName, columns, whereFilterPredicate, queryArguments, FETCH_STRATEGY_LAZY, null);
    }

    public static TransactionWrapper selectWhere(String tableName, Column[] columns,
                                                 FilterPredicate whereFilterPredicate, FlexicoreHashMap queryArguments,
                                                 String fetchStrategy, int[] pagePageSize) {
        TransactionWrapper twrapper = new TransactionWrapper();
        if (queryArguments != null) {
            queryArguments.setTableName(tableName);
        } else {
            queryArguments = new FlexicoreHashMap(tableName);
        }
        try {
            QueryBuilder queryBuilder = new QueryBuilder();
            queryBuilder.select();
            if (columns != null) {
                validateFetchColumns(columns);
                for (Column column : columns) queryBuilder.selectColumn(column);
            }
            queryBuilder.from(tableName).where(whereFilterPredicate);
            queryBuilder.orderBy(getOrderByField(tableName));

            /*if (pagePageSize != null) {
                validatePagePageSize(pagePageSize);
                queryArguments.addQueryArgument(":num_of_records", pagePageSize[1]);
                queryArguments.addQueryArgument(":offset", (pagePageSize[0] - 1) * pagePageSize[1]);
                queryBuilder.limit();
            }*/
            if (pagePageSize != null) {
                validatePagePageSize(pagePageSize);
                if (pagePageSize[0] > 0) {
                    queryArguments.addQueryArgument(":num_of_records", pagePageSize[1]);
                    queryArguments.addQueryArgument(":offset", (pagePageSize[0] - 1) * pagePageSize[1]);
                    queryBuilder.limit();
                }
            }

            twrapper = new TransactionWrapper<>();

            if(!fetchStrategy.equals(FETCH_STRATEGY_COUNT)){
                twrapper = new QueryExecutor().select(queryBuilder.toString(), queryArguments);
            }

            if (pagePageSize != null) {

                switch (fetchStrategy) {
                    case FETCH_STRATEGY_COUNT: {
                        twrapper.setData(paginate(tableName, new FlexicoreArrayList(),
                                countWhere(tableName, whereFilterPredicate, queryArguments), 0, 0
                        ));
                        break;
                    }

                    case FETCH_STRATEGY_LAZY: {
                        twrapper.setData(paginate(tableName, twrapper.getData(), 0, pagePageSize[0], pagePageSize[1]));
                        break;
                    }

                    case FETCH_STRATEGY_EAGER:
                    default: {
                        //todo: Ask Vincent if he has an update
                        if(pagePageSize[0] > 0){
                            twrapper.setData(paginate(tableName, twrapper.getData(),
                                    countWhere(tableName, whereFilterPredicate, queryArguments),
                                    pagePageSize[0], pagePageSize[1]
                            ));
                        } else {
                            twrapper.setData(paginate(tableName, twrapper.getData()));
                        }
                    }
                }
            }

        } catch (Exception e) {
            twrapper.setHasErrors(true);
            twrapper.addError(e.getMessage());
            e.printStackTrace();
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

            twrapper.addErrorStackTrace(sStackTrace);
        }
        return twrapper;
    }

    public static TransactionWrapper selectWhereOrderBy(String tableName, Column[] columns,
                                                 FilterPredicate whereFilterPredicate, String orderByPhrase, FlexicoreHashMap queryArguments,
                                                 String fetchStrategy, int[] pagePageSize) {
        TransactionWrapper twrapper = new TransactionWrapper();
        if (queryArguments != null) {
            queryArguments.setTableName(tableName);
        } else {
            queryArguments = new FlexicoreHashMap(tableName);
        }
        try {
            QueryBuilder queryBuilder = new QueryBuilder();
            queryBuilder.select();
            if (columns != null) {
                validateFetchColumns(columns);
                for (Column column : columns) queryBuilder.selectColumn(column);
            }
            queryBuilder.from(tableName).where(whereFilterPredicate);
            queryBuilder.orderBy(orderByPhrase);

            /*if (pagePageSize != null) {
                validatePagePageSize(pagePageSize);
                queryArguments.addQueryArgument(":num_of_records", pagePageSize[1]);
                queryArguments.addQueryArgument(":offset", (pagePageSize[0] - 1) * pagePageSize[1]);
                queryBuilder.limit();
            }*/
            if (pagePageSize != null) {
                validatePagePageSize(pagePageSize);
                if (pagePageSize[0] > 0) {
                    queryArguments.addQueryArgument(":num_of_records", pagePageSize[1]);
                    queryArguments.addQueryArgument(":offset", (pagePageSize[0] - 1) * pagePageSize[1]);
                    queryBuilder.limit();
                }
            }

            twrapper = new TransactionWrapper<>();

            if(!fetchStrategy.equals(FETCH_STRATEGY_COUNT)){
                twrapper = new QueryExecutor().select(queryBuilder.toString(), queryArguments);
            }

            if (pagePageSize != null) {

                switch (fetchStrategy) {
                    case FETCH_STRATEGY_COUNT: {
                        twrapper.setData(paginate(tableName, new FlexicoreArrayList(),
                                countWhere(tableName, whereFilterPredicate, queryArguments), 0, 0
                        ));
                        break;
                    }

                    case FETCH_STRATEGY_LAZY: {
                        twrapper.setData(paginate(tableName, twrapper.getData(), 0, pagePageSize[0], pagePageSize[1]));
                        break;
                    }

                    case FETCH_STRATEGY_EAGER:
                    default: {
                        //todo: Ask Vincent if he has an update
                        if(pagePageSize[0] > 0){
                            twrapper.setData(paginate(tableName, twrapper.getData(),
                                    countWhere(tableName, whereFilterPredicate, queryArguments),
                                    pagePageSize[0], pagePageSize[1]
                            ));
                        } else {
                            twrapper.setData(paginate(tableName, twrapper.getData()));
                        }
                    }
                }
            }
        } catch (Exception e) {
            twrapper.setHasErrors(true);
            twrapper.addError(e.getMessage());
            e.printStackTrace();
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

            twrapper.addErrorStackTrace(sStackTrace);
        }
        return twrapper;
    }

    public static TransactionWrapper selectWhere(String tableName, Column[] columns, String joinPhrase, String joinPhrasePrefix,
                                                 FilterPredicate whereFilterPredicate, FlexicoreHashMap queryArguments,
                                                 String fetchStrategy, int[] pagePageSize) {
        TransactionWrapper twrapper = new TransactionWrapper();
        if (queryArguments != null) {
            queryArguments.setTableName(tableName);
        } else {
            queryArguments = new FlexicoreHashMap(tableName);
        }
        try {
            QueryBuilder queryBuilder = new QueryBuilder();
            queryBuilder.select();
            if (columns != null) {
                validateFetchColumns(columns);
                for (Column column : columns) queryBuilder.selectColumn(column);
            }
            queryBuilder
                    .from(tableName)
                    .joinPhrase(joinPhrase)
                    .where(whereFilterPredicate);
            queryBuilder.orderBy(joinPhrasePrefix + "." + getOrderByField(tableName));

            /*if (pagePageSize != null) {
                validatePagePageSize(pagePageSize);
                queryArguments.addQueryArgument(":num_of_records", pagePageSize[1]);
                queryArguments.addQueryArgument(":offset", (pagePageSize[0] - 1) * pagePageSize[1]);
                queryBuilder.limit();
            }*/
            if (pagePageSize != null) {
                validatePagePageSize(pagePageSize);
                if (pagePageSize[0] > 0) {
                    queryArguments.addQueryArgument(":num_of_records", pagePageSize[1]);
                    queryArguments.addQueryArgument(":offset", (pagePageSize[0] - 1) * pagePageSize[1]);
                    queryBuilder.limit();
                }
            }
            twrapper = new TransactionWrapper<>();

            if(!fetchStrategy.equals(FETCH_STRATEGY_COUNT)){
                twrapper = new QueryExecutor().select(queryBuilder.toString(), queryArguments);
            }

            if (pagePageSize != null) {

                switch (fetchStrategy) {
                    case FETCH_STRATEGY_COUNT: {
                        twrapper.setData(paginate(tableName, new FlexicoreArrayList(),
                                countWhere(tableName, joinPhrase, whereFilterPredicate, queryArguments), 0, 0
                        ));
                        break;
                    }

                    case FETCH_STRATEGY_LAZY: {
                        twrapper.setData(paginate(tableName, twrapper.getData(), 0, pagePageSize[0], pagePageSize[1]));
                        break;
                    }

                    case FETCH_STRATEGY_EAGER:
                    default: {
                        twrapper.setData(paginate(tableName, twrapper.getData(),
                                countWhere(tableName, joinPhrase, whereFilterPredicate, queryArguments),
                                pagePageSize[0], pagePageSize[1]
                        ));
                    }
                }
            }
        } catch (Exception e) {
            twrapper.setHasErrors(true);
            twrapper.addError(e.getMessage());
            e.printStackTrace();
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

            twrapper.addErrorStackTrace(sStackTrace);
        }
        return twrapper;
    }

    public static TransactionWrapper selectWhereOrderBy(String tableName, Column[] columns, String joinPhrase, String joinPhrasePrefix,
                                                 FilterPredicate whereFilterPredicate, String orderByPhrase, FlexicoreHashMap queryArguments,
                                                        String fetchStrategy, int[] pagePageSize) {
        TransactionWrapper twrapper = new TransactionWrapper();
        if (queryArguments != null) {
            queryArguments.setTableName(tableName);
        } else {
            queryArguments = new FlexicoreHashMap(tableName);
        }
        try {
            QueryBuilder queryBuilder = new QueryBuilder();
            queryBuilder.select();
            if (columns != null) {
                validateFetchColumns(columns);
                for (Column column : columns) queryBuilder.selectColumn(column);
            }
            queryBuilder
                    .from(tableName)
                    .joinPhrase(joinPhrase)
                    .where(whereFilterPredicate);
            queryBuilder.orderBy(orderByPhrase);

            /*if (pagePageSize != null) {
                validatePagePageSize(pagePageSize);
                queryArguments.addQueryArgument(":num_of_records", pagePageSize[1]);
                queryArguments.addQueryArgument(":offset", (pagePageSize[0] - 1) * pagePageSize[1]);
                queryBuilder.limit();
            }*/
            if (pagePageSize != null) {
                validatePagePageSize(pagePageSize);
                if (pagePageSize[0] > 0) {
                    queryArguments.addQueryArgument(":num_of_records", pagePageSize[1]);
                    queryArguments.addQueryArgument(":offset", (pagePageSize[0] - 1) * pagePageSize[1]);
                    queryBuilder.limit();
                }
            }
            twrapper = new TransactionWrapper<>();

            if(!fetchStrategy.equals(FETCH_STRATEGY_COUNT)){
                twrapper = new QueryExecutor().select(queryBuilder.toString(), queryArguments);
            }

            if (pagePageSize != null) {

                switch (fetchStrategy) {
                    case FETCH_STRATEGY_COUNT: {
                        twrapper.setData(paginate(tableName, new FlexicoreArrayList(),
                                countWhere(tableName, joinPhrase, whereFilterPredicate, queryArguments), 0,0
                        ));
                        break;
                    }

                    case FETCH_STRATEGY_LAZY: {
                        twrapper.setData(paginate(tableName, twrapper.getData(), 0, pagePageSize[0], pagePageSize[1]));
                        break;
                    }

                    case FETCH_STRATEGY_EAGER:
                    default: {
                        twrapper.setData(paginate(tableName, twrapper.getData(),
                                countWhere(tableName, joinPhrase, whereFilterPredicate, queryArguments),
                                pagePageSize[0], pagePageSize[1]
                        ));
                    }
                }
            }
        } catch (Exception e) {
            twrapper.setHasErrors(true);
            twrapper.addError(e.getMessage());
            e.printStackTrace();
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

            twrapper.addErrorStackTrace(sStackTrace);
        }
        return twrapper;
    }

    /****************************************************************************************************/
    public static TransactionWrapper selectWhereOrderBy(String tableName,
                                                        FilterPredicate whereFilterPredicate,
                                                        ColumnOrderBy columnOrderBy,
                                                        FlexicoreHashMap queryArguments) {
        return selectWhereOrderBy(tableName, (Column[]) null, whereFilterPredicate,
                new ColumnOrderBy[]{columnOrderBy}, queryArguments, null
        );
    }

    public static TransactionWrapper selectWhereOrderBy(String tableName,
                                                        FilterPredicate whereFilterPredicate,
                                                        ColumnOrderBy columnOrderBy,
                                                        FlexicoreHashMap queryArguments, int[] pagePageSize) {
        return selectWhereOrderBy(tableName, (Column[]) null, whereFilterPredicate,
                new ColumnOrderBy[]{columnOrderBy}, queryArguments, pagePageSize
        );
    }


    public static TransactionWrapper selectWhereOrderBy(String tableName, String columns,
                                                        FilterPredicate whereFilterPredicate,
                                                        ColumnOrderBy columnOrderBy,
                                                        FlexicoreHashMap queryArguments) {
        return selectWhereOrderBy(tableName, formColumnArrayFromStringArray(columns.split(",")), whereFilterPredicate,
                new ColumnOrderBy[]{columnOrderBy}, queryArguments, null
        );
    }

    public static TransactionWrapper selectWhereOrderBy(String tableName, String columns,
                                                        FilterPredicate whereFilterPredicate,
                                                        ColumnOrderBy columnOrderBy,
                                                        FlexicoreHashMap queryArguments, int[] pagePageSize) {
        return selectWhereOrderBy(tableName, formColumnArrayFromStringArray(columns.split(",")), whereFilterPredicate,
                new ColumnOrderBy[]{columnOrderBy}, queryArguments, pagePageSize
        );
    }

    public static TransactionWrapper selectWhereOrderBy(String tableName, String columns,
                                                        FilterPredicate whereFilterPredicate,
                                                        ColumnOrderBy[] columnOrderBIES,
                                                        FlexicoreHashMap queryArguments) {
        return selectWhereOrderBy(tableName, formColumnArrayFromStringArray(columns.split(",")), whereFilterPredicate,
                columnOrderBIES,
                queryArguments, null
        );
    }

    public static TransactionWrapper selectWhereOrderBy(String tableName, String columns,
                                                        FilterPredicate whereFilterPredicate,
                                                        ColumnOrderBy[] columnOrderBIES,
                                                        FlexicoreHashMap queryArguments, int[] pagePageSize) {
        return selectWhereOrderBy(tableName, formColumnArrayFromStringArray(columns.split(",")), whereFilterPredicate,
                columnOrderBIES,
                queryArguments, pagePageSize
        );
    }

    public static TransactionWrapper selectWhereOrderBy(String tableName, Column column,
                                                        FilterPredicate whereFilterPredicate,
                                                        ColumnOrderBy columnOrderBy,
                                                        FlexicoreHashMap queryArguments) {
        return selectWhereOrderBy(tableName, new Column[]{column}, whereFilterPredicate,
                new ColumnOrderBy[]{columnOrderBy}, queryArguments, null
        );
    }

    public static TransactionWrapper selectWhereOrderBy(String tableName, Column column,
                                                        FilterPredicate whereFilterPredicate,
                                                        ColumnOrderBy columnOrderBy,
                                                        FlexicoreHashMap queryArguments, int[] pagePageSize) {
        return selectWhereOrderBy(tableName, new Column[]{column}, whereFilterPredicate,
                new ColumnOrderBy[]{columnOrderBy}, queryArguments, pagePageSize
        );
    }

    public static TransactionWrapper selectWhereOrderBy(String tableName, Column column,
                                                        FilterPredicate whereFilterPredicate,
                                                        ColumnOrderBy[] columnOrderBIES,
                                                        FlexicoreHashMap queryArguments) {
        return selectWhereOrderBy(tableName, new Column[]{column}, whereFilterPredicate,
                columnOrderBIES,
                queryArguments, null
        );
    }

    public static TransactionWrapper selectWhereOrderBy(String tableName, Column column,
                                                        FilterPredicate whereFilterPredicate,
                                                        ColumnOrderBy[] columnOrderBIES,
                                                        FlexicoreHashMap queryArguments, int[] pagePageSize) {
        return selectWhereOrderBy(tableName, new Column[]{column}, whereFilterPredicate,
                columnOrderBIES,
                queryArguments, pagePageSize
        );
    }

    public static TransactionWrapper selectWhereOrderBy(String tableName, Column[] columns,
                                                        FilterPredicate whereFilterPredicate,
                                                        ColumnOrderBy columnOrderBy,
                                                        FlexicoreHashMap queryArguments) {
        return selectWhereOrderBy(tableName, columns, whereFilterPredicate,
                new ColumnOrderBy[]{columnOrderBy}, queryArguments, null
        );
    }

    public static TransactionWrapper selectWhereOrderBy(String tableName, Column[] columns,
                                                        FilterPredicate whereFilterPredicate,
                                                        ColumnOrderBy columnOrderBy,
                                                        FlexicoreHashMap queryArguments, int[] pagePageSize) {
        return selectWhereOrderBy(tableName, columns, whereFilterPredicate,
                new ColumnOrderBy[]{columnOrderBy}, queryArguments, pagePageSize
        );
    }

    public static TransactionWrapper selectWhereOrderBy(String tableName,
                                                        FilterPredicate whereFilterPredicate,
                                                        ColumnOrderBy[] columnOrderBIES,
                                                        FlexicoreHashMap queryArguments) {
        return selectWhereOrderBy(tableName, (Column[]) null, whereFilterPredicate, columnOrderBIES,
                queryArguments, null
        );
    }

    public static TransactionWrapper selectWhereOrderBy(String tableName,
                                                        FilterPredicate whereFilterPredicate,
                                                        ColumnOrderBy[] columnOrderBIES,
                                                        FlexicoreHashMap queryArguments,
                                                        int[] pagePageSize) {
        return selectWhereOrderBy(tableName, (Column[]) null, whereFilterPredicate, columnOrderBIES,
                queryArguments, pagePageSize
        );
    }

    public static TransactionWrapper selectWhereOrderBy(String tableName, Column[] columns,
                                                        FilterPredicate whereFilterPredicate,
                                                        ColumnOrderBy[] columnOrderBIES,
                                                        FlexicoreHashMap queryArguments) {
        return selectWhereOrderBy(tableName, columns, whereFilterPredicate, columnOrderBIES,
                queryArguments, null
        );
    }

    public static TransactionWrapper selectWhereOrderBy(String tableName, Column[] columns,
                                                        FilterPredicate whereFilterPredicate,
                                                        ColumnOrderBy[] columnOrderBIES,
                                                        FlexicoreHashMap queryArguments, int[] pagePageSize) {
        TransactionWrapper twrapper = new TransactionWrapper();
        if (queryArguments != null) {
            queryArguments.setTableName(tableName);
        } else {
            queryArguments = new FlexicoreHashMap(tableName);
        }
        try {
            QueryBuilder queryBuilder = new QueryBuilder();
            queryBuilder.select();
            if (columns != null) {
                validateFetchColumns(columns);
                for (Column column : columns) queryBuilder.selectColumn(column);
            }
            queryBuilder.from(tableName).where(whereFilterPredicate);
            if (columnOrderBIES != null) {
                queryBuilder.orderBy(columnOrderBIES);
            } else {
                queryBuilder.orderBy(getOrderByField(tableName));
            }
            /*if (pagePageSize != null) {
                validatePagePageSize(pagePageSize);
                queryArguments.addQueryArgument(":num_of_records", pagePageSize[1]);
                queryArguments.addQueryArgument(":offset", (pagePageSize[0] - 1) * pagePageSize[1]);
                queryBuilder.limit();
            }*/
            if (pagePageSize != null) {
                validatePagePageSize(pagePageSize);
                if (pagePageSize[0] > 0) {
                    queryArguments.addQueryArgument(":num_of_records", pagePageSize[1]);
                    queryArguments.addQueryArgument(":offset", (pagePageSize[0] - 1) * pagePageSize[1]);
                    queryBuilder.limit();
                }
            }
            twrapper = new QueryExecutor().select(queryBuilder.toString(), queryArguments);
            if (pagePageSize != null) {
                twrapper.setData(paginate(tableName, twrapper.getData(),
                        countWhere(tableName, whereFilterPredicate, queryArguments),
                        pagePageSize[0], pagePageSize[1]
                ));
            }
        } catch (Exception e) {
            twrapper.setHasErrors(true);
            twrapper.addError(e.getMessage());
            e.printStackTrace();
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

            twrapper.addErrorStackTrace(sStackTrace);
        }
        return twrapper;
    }

    public static long countWhere(String tableName, String joinPhrase, FilterPredicate whereFilterPredicate,
                                  FlexicoreHashMap queryArguments) {
        TransactionWrapper twrapper = new TransactionWrapper();
        if (queryArguments != null) {
            queryArguments.setTableName(tableName);
        } else {
            queryArguments = new FlexicoreHashMap(tableName);
        }
        try {
            QueryBuilder queryBuilder = new QueryBuilder();
            queryBuilder.select().selectColumn(new Column("*", "count", SQLEnums.Aggregate.COUNT))
                    .from(tableName)
                    .joinPhrase(joinPhrase);
            if (whereFilterPredicate != null) {
                queryBuilder.where(whereFilterPredicate);
            }
            twrapper = new QueryExecutor().select(queryBuilder.toString(), queryArguments);
        } catch (Exception e) {
            twrapper.setHasErrors(true);
            twrapper.addError(e.getMessage());
            e.printStackTrace();
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

            twrapper.addErrorStackTrace(sStackTrace);
        }
        FlexicoreArrayList flexicoreArrayList = (FlexicoreArrayList) twrapper.getData();
        FlexicoreHashMap flexicoreHashMap = flexicoreArrayList.get(0);
        return (long) flexicoreHashMap.get("count");
    }

    public static long countWhere(String tableName, FilterPredicate whereFilterPredicate,
                                  FlexicoreHashMap queryArguments) {
        TransactionWrapper twrapper = new TransactionWrapper();
        if (queryArguments != null) {
            queryArguments.setTableName(tableName);
        } else {
            queryArguments = new FlexicoreHashMap(tableName);
        }
        try {
            QueryBuilder queryBuilder = new QueryBuilder();
            queryBuilder.select().selectColumn(new Column("*", "count", SQLEnums.Aggregate.COUNT))
                    .from(tableName);
            if (whereFilterPredicate != null) {
                queryBuilder.where(whereFilterPredicate);
            }
            twrapper = new QueryExecutor().select(queryBuilder.toString(), queryArguments);
        } catch (Exception e) {
            twrapper.setHasErrors(true);
            twrapper.addError(e.getMessage());
            e.printStackTrace();
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

            twrapper.addErrorStackTrace(sStackTrace);
        }
        FlexicoreArrayList flexicoreArrayList = (FlexicoreArrayList) twrapper.getData();
        FlexicoreHashMap flexicoreHashMap = flexicoreArrayList.get(0);
        return (long) flexicoreHashMap.get("count");
    }

    /**********************************************************************************************************************/
    /*select [columns] from table [where clause][groun by][having];*/
    public static TransactionWrapper selectWhereGroupBy(String tableName, String columns,
                                                        FilterPredicate whereFilterPredicate, String groupByColumns,
                                                        FlexicoreHashMap queryArguments) {
        return selectWhereGroupBy(tableName, formColumnArrayFromStringArray(columns.split(",")), whereFilterPredicate,
                groupByColumns, null, queryArguments, null, true
        );
    }

    public static TransactionWrapper selectWhereGroupBy(String tableName, String columns,
                                                        FilterPredicate whereFilterPredicate, String groupByColumns,
                                                        FlexicoreHashMap queryArguments,
                                                        boolean orderBy) {
        return selectWhereGroupBy(tableName, formColumnArrayFromStringArray(columns.split(",")), whereFilterPredicate,
                groupByColumns, null, queryArguments, null, orderBy
        );
    }

    public static TransactionWrapper selectWhereGroupBy(String tableName, String columns,
                                                        FilterPredicate whereFilterPredicate, String groupByColumns,
                                                        FlexicoreHashMap queryArguments, int[] pagePageSize) {
        return selectWhereGroupBy(tableName, formColumnArrayFromStringArray(columns.split(",")), whereFilterPredicate,
                groupByColumns, null, queryArguments, pagePageSize, true
        );
    }

    public static TransactionWrapper selectWhereGroupBy(String tableName, String columns,
                                                        FilterPredicate whereFilterPredicate, String groupByColumns,
                                                        FilterPredicate havingFilterPredicate,
                                                        FlexicoreHashMap queryArguments) {
        return selectWhereGroupBy(tableName, formColumnArrayFromStringArray(columns.split(",")), whereFilterPredicate,
                groupByColumns, havingFilterPredicate, queryArguments, null, true
        );
    }

    public static TransactionWrapper selectWhereGroupBy(String tableName, String columns,
                                                        FilterPredicate whereFilterPredicate, String groupByColumns,
                                                        FilterPredicate havingFilterPredicate,
                                                        FlexicoreHashMap queryArguments, int[] pagePageSize) {
        return selectWhereGroupBy(tableName, formColumnArrayFromStringArray(columns.split(",")), whereFilterPredicate,
                groupByColumns, havingFilterPredicate, queryArguments, pagePageSize, true
        );
    }

    public static TransactionWrapper selectWhereGroupBy(String tableName, Column column,
                                                        FilterPredicate whereFilterPredicate, String groupByColumns,
                                                        FlexicoreHashMap queryArguments) {
        return selectWhereGroupBy(tableName, new Column[]{column}, whereFilterPredicate,
                groupByColumns, null, queryArguments, null, true
        );
    }

    public static TransactionWrapper selectWhereGroupBy(String tableName, Column column,
                                                        FilterPredicate whereFilterPredicate, String groupByColumns,
                                                        FlexicoreHashMap queryArguments, int[] pagePageSize) {
        return selectWhereGroupBy(tableName, new Column[]{column}, whereFilterPredicate,
                groupByColumns, null, queryArguments, pagePageSize, true
        );
    }

    public static TransactionWrapper selectWhereGroupBy(String tableName, Column column,
                                                        FilterPredicate whereFilterPredicate, String groupByColumns,
                                                        FilterPredicate havingFilterPredicate,
                                                        FlexicoreHashMap queryArguments) {
        return selectWhereGroupBy(tableName, new Column[]{column}, whereFilterPredicate,
                groupByColumns, havingFilterPredicate, queryArguments, null, true
        );
    }

    public static TransactionWrapper selectWhereGroupBy(String tableName, Column column,
                                                        FilterPredicate whereFilterPredicate, String groupByColumns,
                                                        FilterPredicate havingFilterPredicate,
                                                        FlexicoreHashMap queryArguments, int[] pagePageSize) {
        return selectWhereGroupBy(tableName, new Column[]{column}, whereFilterPredicate,
                groupByColumns, havingFilterPredicate, queryArguments, pagePageSize, true
        );
    }

    public static TransactionWrapper selectWhereGroupBy(String tableName, Column[] columns,
                                                        FilterPredicate whereFilterPredicate, String groupByColumns,
                                                        FlexicoreHashMap queryArguments) {
        return selectWhereGroupBy(tableName, columns, whereFilterPredicate, groupByColumns,
                null, queryArguments, null, true
        );
    }

    public static TransactionWrapper selectWhereGroupBy(String tableName, Column[] columns,
                                                        FilterPredicate whereFilterPredicate, String groupByColumns,
                                                        FlexicoreHashMap queryArguments, int[] pagePageSize) {
        return selectWhereGroupBy(tableName, columns, whereFilterPredicate, groupByColumns,
                null, queryArguments, pagePageSize, true
        );
    }

    public static TransactionWrapper selectWhereGroupBy(String tableName, Column[] columns,
                                                        FilterPredicate whereFilterPredicate, String groupByColumns,
                                                        FilterPredicate havingFilterPredicate,
                                                        FlexicoreHashMap queryArguments) {
        return selectWhereGroupBy(tableName, columns, whereFilterPredicate, groupByColumns,
                havingFilterPredicate, queryArguments, null, true
        );
    }

    public static TransactionWrapper selectWhereGroupBy(String tableName, Column[] columns,
                                                        FilterPredicate whereFilterPredicate, String groupByColumns,
                                                        FilterPredicate havingFilterPredicate,
                                                        FlexicoreHashMap queryArguments,
                                                        int[] pagePageSize) {
        return selectWhereGroupBy(tableName, columns, whereFilterPredicate, groupByColumns,
                havingFilterPredicate, queryArguments, pagePageSize, true
        );
    }


    public static TransactionWrapper selectWhereGroupBy(String tableName, Column[] columns,
                                                        FilterPredicate whereFilterPredicate, String groupByColumns,
                                                        FilterPredicate havingFilterPredicate,
                                                        FlexicoreHashMap queryArguments,
                                                        int[] pagePageSize, boolean orderBy) {
        TransactionWrapper twrapper = new TransactionWrapper();
        if (queryArguments != null) {
            queryArguments.setTableName(tableName);
        } else {
            queryArguments = new FlexicoreHashMap(tableName);
        }
        try {
            QueryBuilder queryBuilder = new QueryBuilder();
            queryBuilder.select();
            if (columns != null) {
                validateFetchColumns(columns);
                for (Column column : columns) queryBuilder.selectColumn(column);
            }
            queryBuilder.from(tableName).where(whereFilterPredicate);
            if (groupByColumns != null) {
                queryBuilder.groupBy(groupByColumns);
            }
            if (havingFilterPredicate != null) {
                queryBuilder.having(havingFilterPredicate);
            }

            if(orderBy) queryBuilder.orderBy(getOrderByField(tableName));

            /*if (pagePageSize != null) {
                validatePagePageSize(pagePageSize);
                queryArguments.addQueryArgument(":num_of_records", pagePageSize[1]);
                queryArguments.addQueryArgument(":offset", (pagePageSize[0] - 1) * pagePageSize[1]);
                queryBuilder.limit();
            }*/
            if (pagePageSize != null) {
                validatePagePageSize(pagePageSize);
                if (pagePageSize[0] > 0) {
                    queryArguments.addQueryArgument(":num_of_records", pagePageSize[1]);
                    queryArguments.addQueryArgument(":offset", (pagePageSize[0] - 1) * pagePageSize[1]);
                    queryBuilder.limit();
                }
            }
            twrapper = new QueryExecutor().select(queryBuilder.toString(), queryArguments);
            if (pagePageSize != null) {
                twrapper.setData(paginate(tableName, twrapper.getData(),
                        countWhereGroupBy(tableName, whereFilterPredicate,
                                groupByColumns, havingFilterPredicate, queryArguments
                        ), pagePageSize[0], pagePageSize[1]
                ));
            }
        } catch (Exception e) {
            twrapper.setHasErrors(true);
            twrapper.addError(e.getMessage());
            e.printStackTrace();
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

            twrapper.addErrorStackTrace(sStackTrace);
        }
        return twrapper;
    }
    /*****************************************************************************************************************************/

    /*****************************************************************************************************************************/
    /*select [columns] from table [where clause][group by][having][order by];*/
    public static TransactionWrapper selectWhereGroupByOrderBy(String tableName, String columns,
                                                               FilterPredicate whereFilterPredicate,
                                                               String groupByColumns,
                                                               ColumnOrderBy columnOrderBy,
                                                               FlexicoreHashMap queryArguments) {
        return selectWhereGroupByOrderBy(tableName, formColumnArrayFromStringArray(columns.split(",")),
                whereFilterPredicate,
                groupByColumns, null, new ColumnOrderBy[]{columnOrderBy},
                queryArguments, null
        );
    }

    public static TransactionWrapper selectWhereGroupByOrderBy(String tableName, String columns,
                                                               FilterPredicate whereFilterPredicate,
                                                               String groupByColumns,
                                                               ColumnOrderBy columnOrderBy,
                                                               FlexicoreHashMap queryArguments,
                                                               int[] pagePageSize) {
        return selectWhereGroupByOrderBy(tableName, formColumnArrayFromStringArray(columns.split(",")),
                whereFilterPredicate,
                groupByColumns, null, new ColumnOrderBy[]{columnOrderBy},
                queryArguments, pagePageSize
        );
    }

    public static TransactionWrapper selectWhereGroupByOrderBy(String tableName, String columns,
                                                               FilterPredicate whereFilterPredicate,
                                                               String groupByColumns,
                                                               FilterPredicate havingFilterPredicate,
                                                               ColumnOrderBy columnOrderBy,
                                                               FlexicoreHashMap queryArguments) {
        return selectWhereGroupByOrderBy(tableName, formColumnArrayFromStringArray(columns.split(",")),
                whereFilterPredicate,
                groupByColumns, havingFilterPredicate,
                new ColumnOrderBy[]{columnOrderBy}, queryArguments, null
        );
    }

    public static TransactionWrapper selectWhereGroupByOrderBy(String tableName, String columns,
                                                               FilterPredicate whereFilterPredicate,
                                                               String groupByColumns,
                                                               FilterPredicate havingFilterPredicate,
                                                               ColumnOrderBy columnOrderBy,
                                                               FlexicoreHashMap queryArguments, int[] pagePageSize) {
        return selectWhereGroupByOrderBy(tableName, formColumnArrayFromStringArray(columns.split(",")),
                whereFilterPredicate,
                groupByColumns, havingFilterPredicate,
                new ColumnOrderBy[]{columnOrderBy}, queryArguments, pagePageSize
        );
    }

    public static TransactionWrapper selectWhereGroupByOrderBy(String tableName, Column column,
                                                               FilterPredicate whereFilterPredicate,
                                                               String groupByColumns,
                                                               ColumnOrderBy columnOrderBy,
                                                               FlexicoreHashMap queryArguments) {
        return selectWhereGroupByOrderBy(tableName, new Column[]{column}, whereFilterPredicate,
                groupByColumns, null, new ColumnOrderBy[]{columnOrderBy},
                queryArguments, null
        );
    }

    public static TransactionWrapper selectWhereGroupByOrderBy(String tableName, Column column,
                                                               FilterPredicate whereFilterPredicate,
                                                               String groupByColumns,
                                                               ColumnOrderBy columnOrderBy,
                                                               FlexicoreHashMap queryArguments,
                                                               int[] pagePageSize) {
        return selectWhereGroupByOrderBy(tableName, new Column[]{column}, whereFilterPredicate,
                groupByColumns, null, new ColumnOrderBy[]{columnOrderBy},
                queryArguments, pagePageSize
        );
    }

    public static TransactionWrapper selectWhereGroupByOrderBy(String tableName, Column column,
                                                               FilterPredicate whereFilterPredicate,
                                                               String groupByColumns,
                                                               FilterPredicate havingFilterPredicate,
                                                               ColumnOrderBy columnOrderBy,
                                                               FlexicoreHashMap queryArguments) {
        return selectWhereGroupByOrderBy(tableName, new Column[]{column}, whereFilterPredicate,
                groupByColumns, havingFilterPredicate,
                new ColumnOrderBy[]{columnOrderBy}, queryArguments, null
        );
    }

    public static TransactionWrapper selectWhereGroupByOrderBy(String tableName, Column column,
                                                               FilterPredicate whereFilterPredicate,
                                                               String groupByColumns,
                                                               FilterPredicate havingFilterPredicate,
                                                               ColumnOrderBy columnOrderBy,
                                                               FlexicoreHashMap queryArguments, int[] pagePageSize) {
        return selectWhereGroupByOrderBy(tableName, new Column[]{column}, whereFilterPredicate,
                groupByColumns, havingFilterPredicate,
                new ColumnOrderBy[]{columnOrderBy}, queryArguments, pagePageSize
        );
    }

    public static TransactionWrapper selectWhereGroupByOrderBy(String tableName, Column[] columns,
                                                               FilterPredicate whereFilterPredicate,
                                                               String groupByColumns,
                                                               ColumnOrderBy columnOrderBy,
                                                               FlexicoreHashMap queryArguments) {
        return selectWhereGroupByOrderBy(tableName, columns, whereFilterPredicate,
                groupByColumns, null, new ColumnOrderBy[]{columnOrderBy},
                queryArguments, null
        );
    }

    public static TransactionWrapper selectWhereGroupByOrderBy(String tableName, Column[] columns,
                                                               FilterPredicate whereFilterPredicate,
                                                               String groupByColumns,
                                                               ColumnOrderBy columnOrderBy,
                                                               FlexicoreHashMap queryArguments,
                                                               int[] pagePageSize) {
        return selectWhereGroupByOrderBy(tableName, columns, whereFilterPredicate,
                groupByColumns, null, new ColumnOrderBy[]{columnOrderBy},
                queryArguments, pagePageSize
        );
    }

    public static TransactionWrapper selectWhereGroupByOrderBy(String tableName, Column[] columns,
                                                               FilterPredicate whereFilterPredicate,
                                                               String groupByColumns,
                                                               FilterPredicate havingFilterPredicate,
                                                               ColumnOrderBy columnOrderBy,
                                                               FlexicoreHashMap queryArguments) {
        return selectWhereGroupByOrderBy(tableName, columns, whereFilterPredicate,
                groupByColumns, havingFilterPredicate,
                new ColumnOrderBy[]{columnOrderBy}, queryArguments, null
        );
    }

    public static TransactionWrapper selectWhereGroupByOrderBy(String tableName, Column[] columns,
                                                               FilterPredicate whereFilterPredicate,
                                                               String groupByColumns,
                                                               FilterPredicate havingFilterPredicate,
                                                               ColumnOrderBy columnOrderBy,
                                                               FlexicoreHashMap queryArguments, int[] pagePageSize) {
        return selectWhereGroupByOrderBy(tableName, columns, whereFilterPredicate,
                groupByColumns, havingFilterPredicate,
                new ColumnOrderBy[]{columnOrderBy}, queryArguments, pagePageSize
        );
    }

    public static TransactionWrapper selectWhereGroupByOrderBy(String tableName, Column[] columns,
                                                               FilterPredicate whereFilterPredicate,
                                                               String groupByColumns,
                                                               FilterPredicate havingFilterPredicate,
                                                               ColumnOrderBy[] columnOrderBIES,
                                                               FlexicoreHashMap queryArguments, int[] pagePageSize) {
        TransactionWrapper twrapper = new TransactionWrapper();
        if (queryArguments != null) {
            queryArguments.setTableName(tableName);
        } else {
            queryArguments = new FlexicoreHashMap(tableName);
        }
        try {
            QueryBuilder queryBuilder = new QueryBuilder();
            queryBuilder.select();
            if (columns != null) {
                validateFetchColumns(columns);
                for (Column column : columns) queryBuilder.selectColumn(column);
            }
            queryBuilder.from(tableName).where(whereFilterPredicate);
            if (groupByColumns != null) {
                queryBuilder.groupBy(groupByColumns);
            }
            if (havingFilterPredicate != null) {
                queryBuilder.having(havingFilterPredicate);
            }
            if (columnOrderBIES != null) {
                queryBuilder.orderBy(columnOrderBIES);
            } else {
                queryBuilder.orderBy(getOrderByField(tableName));
            }
            /*if (pagePageSize != null) {
                validatePagePageSize(pagePageSize);
                queryArguments.addQueryArgument(":num_of_records", pagePageSize[1]);
                queryArguments.addQueryArgument(":offset", (pagePageSize[0] - 1) * pagePageSize[1]);
                queryBuilder.limit();
            }*/
            if (pagePageSize != null) {
                validatePagePageSize(pagePageSize);
                if (pagePageSize[0] > 0) {
                    queryArguments.addQueryArgument(":num_of_records", pagePageSize[1]);
                    queryArguments.addQueryArgument(":offset", (pagePageSize[0] - 1) * pagePageSize[1]);
                    queryBuilder.limit();
                }
            }
            twrapper = new QueryExecutor().select(queryBuilder.toString(), queryArguments);
            if (pagePageSize != null) {
                twrapper.setData(paginate(tableName, twrapper.getData(),
                        countWhereGroupBy(tableName, whereFilterPredicate,
                                groupByColumns, havingFilterPredicate, queryArguments
                        ), pagePageSize[0], pagePageSize[1]
                ));
            }
        } catch (Exception e) {
            twrapper.setHasErrors(true);
            twrapper.addError(e.getMessage());
            e.printStackTrace();
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

            twrapper.addErrorStackTrace(sStackTrace);
        }
        return twrapper;
    }

    public static long countWhereGroupBy(String tableName, FilterPredicate whereFilterPredicate,
                                         String groupByColumns, FilterPredicate havingFilterPredicate,
                                         FlexicoreHashMap queryArguments) {
        TransactionWrapper twrapper = new TransactionWrapper();
        if (queryArguments != null) {
            queryArguments.setTableName(tableName);
        } else {
            queryArguments = new FlexicoreHashMap(tableName);
        }
        try {
            QueryBuilder queryBuilder = new QueryBuilder();
            queryBuilder.select().selectColumn(new Column("*", "count", SQLEnums.Aggregate.COUNT)).from(tableName);
            if (whereFilterPredicate != null) {
                queryBuilder.where(whereFilterPredicate);
            }
            if (groupByColumns != null) {
                queryBuilder.groupBy(groupByColumns);
            }
            if (havingFilterPredicate != null) {
                queryBuilder.having(havingFilterPredicate);
            }
            twrapper = new QueryExecutor().select(queryBuilder.toString(), queryArguments);
        } catch (Exception e) {
            twrapper.setHasErrors(true);
            twrapper.addError(e.getMessage());
            e.printStackTrace();
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

            twrapper.addErrorStackTrace(sStackTrace);
        }
        FlexicoreArrayList flexicoreArrayList = (FlexicoreArrayList) twrapper.getData();
        return (long) flexicoreArrayList.size();

    }

    private static void validateFetchColumns(Column[] columns) throws Exception {
        if (columns.length < 1) {
            throw new QueryBuilderException("columns to be selected not provided");
        }
        for (Column column : columns)
            if (column.getColumnName().isEmpty()) {
                throw new QueryBuilderException("columns to be selected cannot be empty");
            }
    }

    private static Column[] formColumnArrayFromStringArray(String[] strColumnsArray) {
        Column[] columns = new Column[strColumnsArray.length];
        for (int i = 0; i < strColumnsArray.length; i++) {
            strColumnsArray[i] = strColumnsArray[i].trim();
            columns[i] = new Column(strColumnsArray[i]);
        }
        return columns;
    }

    private static void validatePagePageSize(int[] pagePageSize) throws Exception {
        if (pagePageSize.length != 2) {
            throw new QueryBuilderException("LIMIT: Page and page size variables must be provided [page, page size]");
        }
        /*if (pagePageSize[0] < 1) {
            throw new QueryBuilderException("LIMIT: Page must be greater than 1");
        }*/
        if (pagePageSize[1] < 1) {
            throw new QueryBuilderException("LIMIT: Page size must be greater than 1");
        }
    }

    private static String[] formInsertNamedVars(String[] columns) {
        int length = columns.length;
        String[] c = Arrays.copyOf(columns, columns.length);
        for (int i = 0; i < length; i++) {
            c[i] = ":" + columns[i];
        }
        return c;
    }

    @SuppressWarnings("unchecked")
    private static Object paginate(String tableName, Object records, long totalCount, int page, int pageSize) {
        PageableWrapper pageableWrapper = new PageableWrapper();
        pageableWrapper.setCurrentPage(page);
        pageableWrapper.setPageSize(pageSize);
        pageableWrapper.setDomain(tableName);
        pageableWrapper.setRecords(records);
        pageableWrapper.setTotalCount(totalCount);
        return pageableWrapper;
    }

    private static Object paginate(String tableName, Object records) {
        PageableWrapper pageableWrapper = new PageableWrapper();
        pageableWrapper.setDomain(tableName);
        pageableWrapper.setRecords(records);
        return pageableWrapper;
    }

    public static FlexicoreHashMap getServerIdentity(){
        QueryBuilder query = new QueryBuilder();
        query.rawQuery("SELECT @@SERVER_ID AS server_id, @@HOSTNAME AS server_hostname");
        return select(query).getSingleRecord();
    }

    public static String getOrderByField(String tableName) {

        switch (tableName) {
            case Table.ACCESS_TOKENS:
            case Table.REQUEST_LOGS:
                return "date_created " + SQLEnums.OrderBy.DESC;
            case Table.APPLICATION_WORKFLOW_LOGS:
                return "log_date " + SQLEnums.OrderBy.DESC;
            default:
                return "date_modified " + SQLEnums.OrderBy.DESC;
        }
    }

    public static void setTimestamp(String tableName, List<String> columnsList, FlexicoreHashMap queryArguments) {
        switch (tableName) {
            case Table.ACCESS_TOKENS:
                columnsList.add("date_created");
                queryArguments.addQueryArgument(":date_created", DateTime.getCurrentDateTime());
                break;
            case Table.APPLICATION_WORKFLOW_LOGS:
                columnsList.add("log_date");
                queryArguments.addQueryArgument(":log_date", DateTime.getCurrentDateTime());
                break;
            default:
                columnsList.add("date_created");
                columnsList.add("date_modified");
                queryArguments.addQueryArgument(":date_created", DateTime.getCurrentDateTime());
                queryArguments.addQueryArgument(":date_modified", DateTime.getCurrentDateTime());
                break;
        }
    }
}