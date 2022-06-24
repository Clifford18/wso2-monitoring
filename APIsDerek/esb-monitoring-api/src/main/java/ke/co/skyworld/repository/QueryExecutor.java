package ke.co.skyworld.repository;

import com.mysql.cj.exceptions.CJCommunicationsException;
import com.mysql.cj.exceptions.ConnectionIsClosedException;
import com.mysql.cj.jdbc.exceptions.CommunicationsException;
import ke.co.skyworld.flexicore.connection_manager.connection.ConnectionManager;
import ke.co.skyworld.flexicore.connection_manager.connection.ExtendedConnection;
import ke.co.skyworld.repository.beans.Column;
import ke.co.skyworld.repository.beans.FlexicoreArrayList;
import ke.co.skyworld.repository.beans.FlexicoreHashMap;
import ke.co.skyworld.repository.beans.TransactionWrapper;
import ke.co.skyworld.repository.query.FilterPredicate;
import ke.co.skyworld.repository.query.QueryBuilder;
import ke.co.skyworld.repository.statement.NamedPreparedStatement;
import ke.co.skyworld.utils.Constants;
import ke.co.skyworld.utils.logging.Log;
import ke.co.skyworld.utils.memory.JvmManager;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class QueryExecutor {
    private NamedPreparedStatement preparedStatement;
    private ResultSet resultSet;

    public TransactionWrapper<FlexicoreHashMap> insertAutoIncremented(QueryBuilder queryBuilder,
                                                                      FlexicoreHashMap queryArguments) {
        Object lastInsertId = 0;
        TransactionWrapper<FlexicoreHashMap> tWrapper = new TransactionWrapper<>();
        ExtendedConnection extendedConnection = ConnectionManager.getConnection();
        try {
            String query = queryBuilder.toString();
            String tableName = queryBuilder.getTableName();
            validateQueryArguments(query, queryArguments);

            preparedStatement = NamedPreparedStatement.prepareStatement(extendedConnection, query, Statement.RETURN_GENERATED_KEYS);
            for (Map.Entry<String, Object> entry : queryArguments.entrySet()) {
                preparedStatement.setObject(entry.getKey().replaceFirst(":", ""), entry.getValue());
            }

            Log.debug(QueryExecutor.class, "insertAutoIncremented", "Sling Ringer-"+
                    ConnectionManager.getConnectionIndex(extendedConnection)+": "+
                    extendedConnection.getUser()+"@"+extendedConnection.getCatalog());
            //Log.debug(QueryExecutor.class, "insertAutoIncremented", "Index: "+ConnectionManager.getConnectionIndex(extendedConnection));
            //TODO: Do I have latest Repository? Add DB name in insertAutoIncrement()
            //TODO: Provide option to show sql and also show variables in deep debug mode
            //TODO: Show SQL before execute
            //TODO: Package this stuff :)
            if(Constants.showSql())
                Log.debug(QueryExecutor.class, "insertAutoIncremented", SQLFormatter.format(query));
                //Log.debug(QueryExecutor.class, "insertAutoIncremented", SQLFormatter.format(preparedStatement.getQuery()));

            preparedStatement.executeUpdate();

            String pkColumnName = getAutoIncrementedPKColumn(extendedConnection, tableName);
            resultSet = preparedStatement.getGeneratedKeys();

            if (!queryArguments.containsKey(":" + pkColumnName)) {
                if (resultSet.next()) {
                    lastInsertId = resultSet.getObject(1);
                }
            } else lastInsertId = queryArguments.getValue(":" + pkColumnName);

            //select query to get last inserted record
            gc();
            {
                QueryBuilder q = new QueryBuilder();
                q.select().from(tableName).where(new FilterPredicate().equalTo(pkColumnName, ":" + pkColumnName));

                tWrapper.setData(insertSelect(q.toString(),extendedConnection,
                        new FlexicoreHashMap(tableName).addQueryArgument(":" + pkColumnName, lastInsertId)).getSingleRecord());
            }

            gc();
        } catch (Exception e) {
            if(preparedStatement != null){
                System.out.println("SQL Query: "+preparedStatement.getQuery());
            }
            e.printStackTrace();
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
            if (e instanceof CJCommunicationsException || e instanceof CommunicationsException || e instanceof ConnectionIsClosedException || e instanceof SQLNonTransientConnectionException) {
                ConnectionManager.removeExpiredConnection(extendedConnection);
                tWrapper = insertAutoIncremented(queryBuilder, queryArguments);
            }
        }
        extendedConnection.release();
        return tWrapper;
    }

    public TransactionWrapper<FlexicoreHashMap> insertAutoIncremented(ExtendedConnection extendedConnection, QueryBuilder queryBuilder,
                                                                      FlexicoreHashMap queryArguments) {
        Object lastInsertId = 0;
        TransactionWrapper<FlexicoreHashMap> tWrapper = new TransactionWrapper<>();
        try {
            String query = queryBuilder.toString();
            String tableName = queryBuilder.getTableName();
            validateQueryArguments(query, queryArguments);

            preparedStatement = NamedPreparedStatement.prepareStatement(extendedConnection, query, Statement.RETURN_GENERATED_KEYS);
            for (Map.Entry<String, Object> entry : queryArguments.entrySet()) {
                preparedStatement.setObject(entry.getKey().replaceFirst(":", ""), entry.getValue());
            }

            Log.debug(QueryExecutor.class, "insertAutoIncremented", "Sling Ringer-"+
                    ConnectionManager.getConnectionIndex(extendedConnection)+": "+
                    extendedConnection.getUser()+"@"+extendedConnection.getCatalog());
            //Log.debug(QueryExecutor.class, "insertAutoIncremented", "Index: "+ConnectionManager.getConnectionIndex(extendedConnection));
            //TODO: Do I have latest Repository? Add DB name in insertAutoIncrement()
            //TODO: Provide option to show sql and also show variables in deep debug mode
            //TODO: Show SQL before execute
            //TODO: Package this stuff :)
            if(Constants.showSql())
                Log.debug(QueryExecutor.class, "insertAutoIncremented", SQLFormatter.format(query));
            //Log.debug(QueryExecutor.class, "insertAutoIncremented", SQLFormatter.format(preparedStatement.getQuery()));

            preparedStatement.executeUpdate();

            String pkColumnName = getAutoIncrementedPKColumn(extendedConnection, tableName);
            resultSet = preparedStatement.getGeneratedKeys();

            if (!queryArguments.containsKey(":" + pkColumnName)) {
                if (resultSet.next()) {
                    lastInsertId = resultSet.getObject(1);
                }
            } else lastInsertId = queryArguments.getValue(":" + pkColumnName);

            //select query to get last inserted record
            gc();
            {
                QueryBuilder q = new QueryBuilder();
                q.select().from(tableName).where(new FilterPredicate().equalTo(pkColumnName, ":" + pkColumnName));

                tWrapper.setData(insertSelect(q.toString(),extendedConnection,
                        new FlexicoreHashMap(tableName).addQueryArgument(":" + pkColumnName, lastInsertId)).getSingleRecord());
            }

            gc();
        } catch (Exception e) {
            if(preparedStatement != null){
                System.out.println("SQL Query: "+preparedStatement.getQuery());
            }
            e.printStackTrace();
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
        }
        return tWrapper;
    }

    //insert for when primary key is NOT autoincrement
    public TransactionWrapper<FlexicoreHashMap> insertProvidedPK(QueryBuilder queryBuilder,
                                                                 FlexicoreHashMap queryArguments) {
        TransactionWrapper<FlexicoreHashMap> tWrapper = new TransactionWrapper<>();
        ExtendedConnection extendedConnection = ConnectionManager.getConnection();
        try {
            String query = queryBuilder.toString();
            String tableName = queryBuilder.getTableName();
            validateQueryArguments(query, queryArguments);
            Object[] queryBuilderPlusArguments = prepareProvidedPKSelect(queryArguments);

            //insert record
            preparedStatement = NamedPreparedStatement.prepareStatement(extendedConnection, query);
            for (Map.Entry<String, Object> entry : queryArguments.entrySet()) {
                preparedStatement.setObject(entry.getKey().replaceFirst(":", ""),
                        entry.getValue());
            }
            preparedStatement.executeUpdate();
            if(Constants.showSql())
                Log.debug(QueryExecutor.class, "insertProvidedPK", SQLFormatter.format(query));

            //select query to get inserted record
            gc();
            {
                QueryBuilder q = (QueryBuilder) queryBuilderPlusArguments[0];
                FlexicoreHashMap newQueryArguments = (FlexicoreHashMap) queryBuilderPlusArguments[1];
                if(Constants.showSql())
                    Log.debug(QueryExecutor.class, "insertProvidedPK", SQLFormatter.format(q.toString()));

                tWrapper.setData(insertSelect(q.toString(), extendedConnection, newQueryArguments).getSingleRecord());
            }
            gc();
        } catch (Exception e) {
            if(preparedStatement != null){
                System.out.println("SQL Query: "+preparedStatement.getQuery());
            }
            e.printStackTrace();
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
            if (e instanceof CJCommunicationsException || e instanceof CommunicationsException || e instanceof ConnectionIsClosedException || e instanceof SQLNonTransientConnectionException) {
                ConnectionManager.removeExpiredConnection(extendedConnection);
                tWrapper = insertProvidedPK(queryBuilder, queryArguments);
            }
        }
        extendedConnection.release();
        return tWrapper;
    }

    public TransactionWrapper<Boolean> batchInsert(String query, FlexicoreArrayList insertValuesList) {
        TransactionWrapper<Boolean> tWrapper = new TransactionWrapper<>(false);
        ExtendedConnection extendedConnection = ConnectionManager.getConnection();
        try {
            preparedStatement = NamedPreparedStatement.prepareStatement(extendedConnection, query);
            for (FlexicoreHashMap singleRecord : insertValuesList) {
                for (Map.Entry<String, Object> entry : singleRecord.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    preparedStatement.setObject(key.replaceFirst(":", ""), value);
                }
                preparedStatement.addBatch();
            }
            Log.debug(QueryExecutor.class, "batchInsert", SQLFormatter.format(query));
            preparedStatement.executeBatch();
            tWrapper.setData(true);
        } catch (Exception e) {
            if(preparedStatement != null){
                System.out.println("SQL Query: "+preparedStatement.getQuery());
            }
            e.printStackTrace();
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
            if (e instanceof CJCommunicationsException || e instanceof CommunicationsException || e instanceof ConnectionIsClosedException || e instanceof SQLNonTransientConnectionException) {
                ConnectionManager.removeExpiredConnection(extendedConnection);
                tWrapper = batchInsert(query, insertValuesList);
            }
        } finally {
            gc();
        }
        extendedConnection.release();
        return tWrapper;
    }


    private TransactionWrapper<FlexicoreHashMap> insertSelect(String query,
                                                              ExtendedConnection extendedConnection,
                                                              FlexicoreHashMap queryArguments) {
        TransactionWrapper<FlexicoreHashMap> tWrapper = new TransactionWrapper<>();
        try {
            preparedStatement = NamedPreparedStatement.prepareStatement(extendedConnection, query);
            for (Map.Entry<String, Object> entry : queryArguments.entrySet()) {
                preparedStatement.setObject(entry.getKey().replaceFirst(":", ""), entry.getValue());
            }

            if(Constants.showSql())
                Log.debug(QueryExecutor.class, "insertSelect", SQLFormatter.format(query));

            Log.debug(QueryExecutor.class, "insertSelect", "Sling Ringer-"+
                    ConnectionManager.getConnectionIndex(extendedConnection)+": "+
                    extendedConnection.getUser()+"@"+extendedConnection.getCatalog());
            //Log.debug(QueryExecutor.class, "insertSelect", "Index: "+ConnectionManager.getConnectionIndex(extendedConnection));

            resultSet = preparedStatement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columns = metaData.getColumnCount();

            if (resultSet.next()) {
                FlexicoreHashMap recordHashMap = new FlexicoreHashMap(queryArguments.getTableName());
                for (int i = 1; i <= columns; ++i) {
                    recordHashMap.putValue(metaData.getColumnLabel(i), resultSet.getObject(i));
                }
                tWrapper.setData(recordHashMap);
            }
            gc();
        } catch (Exception e) {
            if(preparedStatement != null){
                System.out.println("SQL Query: "+preparedStatement.getQuery());
            }
            e.printStackTrace();
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
            if (e instanceof CJCommunicationsException || e instanceof CommunicationsException || e instanceof ConnectionIsClosedException || e instanceof SQLNonTransientConnectionException) {
                ConnectionManager.removeExpiredConnection(extendedConnection);
                tWrapper = insertSelect(query, extendedConnection, queryArguments);
            }
        }
        return tWrapper;
    }

    public TransactionWrapper<FlexicoreArrayList> select(String query,
                                                         FlexicoreHashMap queryArguments) {
        TransactionWrapper<FlexicoreArrayList> tWrapper = new TransactionWrapper<>();
        ExtendedConnection extendedConnection = ConnectionManager.getConnection();
        try {
            List<String> allQueryVariables = validateQueryArguments(query, queryArguments);
            preparedStatement = NamedPreparedStatement.prepareStatement(extendedConnection, query);
            for (String field : allQueryVariables) {
                preparedStatement.setObject(field.replaceFirst(":", ""), queryArguments.getValue(field));
            }

            if(Constants.showSql())
                Log.debug(QueryExecutor.class, "select", SQLFormatter.format(query));
                //Log.debug(QueryExecutor.class, "select", SQLFormatter.format(preparedStatement.getQuery()));

            Log.debug(QueryExecutor.class, "select", "Sling Ringer-"+
                    ConnectionManager.getConnectionIndex(extendedConnection)+": "+
                    extendedConnection.getUser()+"@"+extendedConnection.getCatalog());
            //Log.debug(QueryExecutor.class, "select", "Index: "+ConnectionManager.getConnectionIndex(extendedConnection));

            resultSet = preparedStatement.executeQuery();

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columns = metaData.getColumnCount();
            FlexicoreArrayList flexicoreArrayList = new FlexicoreArrayList(queryArguments.getTableName());
            while (resultSet.next()) {
                FlexicoreHashMap recordHashMap = new FlexicoreHashMap(queryArguments.getTableName());
                for (int i = 1; i <= columns; ++i) {
                    recordHashMap.putValue(metaData.getColumnLabel(i), resultSet.getObject(i));
                }
                flexicoreArrayList.addNewRecord(recordHashMap);
            }
            tWrapper.setData(flexicoreArrayList);
            gc();
            extendedConnection.release();
        } catch (Exception e) {
            if(preparedStatement != null){
                System.out.println("SQL Query: "+preparedStatement.getQuery());
            }
            e.printStackTrace();
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
            if (e instanceof CJCommunicationsException || e instanceof CommunicationsException || e instanceof ConnectionIsClosedException || e instanceof SQLNonTransientConnectionException) {
                ConnectionManager.removeExpiredConnection(extendedConnection);
                tWrapper = select(query, queryArguments);
            }
            extendedConnection.release();
        }
        extendedConnection.release();
        return tWrapper;
    }

    public TransactionWrapper<FlexicoreArrayList> update(QueryBuilder queryBuilder, List<String> updateColumns,
                                                         FlexicoreHashMap queryArguments) {
        TransactionWrapper<FlexicoreArrayList> tWrapper = new TransactionWrapper<>();
        String tableName = queryArguments.getTableName();
        ExtendedConnection extendedConnection = ConnectionManager.getConnection();
        try {
            validateQueryArguments(queryBuilder.toString(), queryArguments);
            HashSet<String> pkColumnsHasSet = getPKColumns(extendedConnection, tableName);
            {
                QueryBuilder selectQueryBuilder = new QueryBuilder();
                selectQueryBuilder.select();
                for (String column : pkColumnsHasSet) selectQueryBuilder.selectColumn(new Column(column));
                if (updateColumns != null) for (String column : updateColumns) selectQueryBuilder.selectColumn(new Column(column));
                selectQueryBuilder.from(tableName);
                selectQueryBuilder.where(queryBuilder.getWhereClause());

                tWrapper = select(selectQueryBuilder.toString(), queryArguments);
                FlexicoreArrayList oldValuesList = tWrapper.getData();
                for (FlexicoreHashMap hashMap : oldValuesList) {
                    StringBuilder pkColumn = new StringBuilder();
                    StringBuilder pkValue = new StringBuilder();

                    boolean addDelimiter = false;
                    for (String column : pkColumnsHasSet) {
                        if (addDelimiter) {
                            pkColumn.append(Constants.PK_DELIMITER).append(column);
                            pkValue.append(Constants.PK_DELIMITER).append(hashMap.get(column));
                        } else {
                            pkColumn.append(column);
                            pkValue.append(hashMap.get(column));
                        }
                        addDelimiter = true;
                    }
                    hashMap.put("pkValues", pkColumn.toString() + "," + pkValue.toString());
                }
                //tWrapper.setData(oldValuesList);
            }

            preparedStatement = NamedPreparedStatement.prepareStatement(extendedConnection, queryBuilder.toString());
            if (!queryArguments.isEmpty()) {
                for (Map.Entry<String, Object> entry : queryArguments.entrySet()) {
                    preparedStatement.setObject(entry.getKey().replaceFirst(":", ""), entry.getValue());
                }
            }

            if(Constants.showSql())
                Log.debug(QueryExecutor.class, "update", SQLFormatter.format(queryBuilder.toString()));

            Log.debug(QueryExecutor.class, "update", "Sling Ringer-"+
                    ConnectionManager.getConnectionIndex(extendedConnection)+": "+
                    extendedConnection.getUser()+"@"+extendedConnection.getCatalog());
            //Log.debug(QueryExecutor.class, "update", "Index: "+ConnectionManager.getConnectionIndex(extendedConnection));

            preparedStatement.executeUpdate();
            gc();
        } catch (Exception e) {
            if(preparedStatement != null){
                System.out.println("SQL Query: "+preparedStatement.getQuery());
            }
            e.printStackTrace();
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
            if (e instanceof CJCommunicationsException || e instanceof CommunicationsException || e instanceof ConnectionIsClosedException || e instanceof SQLNonTransientConnectionException) {
                ConnectionManager.removeExpiredConnection(extendedConnection);
                tWrapper = update(queryBuilder, updateColumns, queryArguments);
            }
        }
        extendedConnection.release();
        return tWrapper;
    }

    public TransactionWrapper<FlexicoreArrayList> update(ExtendedConnection extendedConnection, QueryBuilder queryBuilder, List<String> updateColumns,
                                                         FlexicoreHashMap queryArguments) {
        TransactionWrapper<FlexicoreArrayList> tWrapper = new TransactionWrapper<>();
        String tableName = queryArguments.getTableName();
        try {
            validateQueryArguments(queryBuilder.toString(), queryArguments);
            HashSet<String> pkColumnsHasSet = getPKColumns(extendedConnection, tableName);
            {
                QueryBuilder selectQueryBuilder = new QueryBuilder();
                selectQueryBuilder.select();
                for (String column : pkColumnsHasSet) selectQueryBuilder.selectColumn(new Column(column));
                if (updateColumns != null) for (String column : updateColumns) selectQueryBuilder.selectColumn(new Column(column));
                selectQueryBuilder.from(tableName);
                selectQueryBuilder.where(queryBuilder.getWhereClause());

                tWrapper = select(selectQueryBuilder.toString(), queryArguments);
                FlexicoreArrayList oldValuesList = tWrapper.getData();
                for (FlexicoreHashMap hashMap : oldValuesList) {
                    StringBuilder pkColumn = new StringBuilder();
                    StringBuilder pkValue = new StringBuilder();

                    boolean addDelimiter = false;
                    for (String column : pkColumnsHasSet) {
                        if (addDelimiter) {
                            pkColumn.append(Constants.PK_DELIMITER).append(column);
                            pkValue.append(Constants.PK_DELIMITER).append(hashMap.get(column));
                        } else {
                            pkColumn.append(column);
                            pkValue.append(hashMap.get(column));
                        }
                        addDelimiter = true;
                    }
                    hashMap.put("pkValues", pkColumn.toString() + "," + pkValue.toString());
                }
                //tWrapper.setData(oldValuesList);
            }

            preparedStatement = NamedPreparedStatement.prepareStatement(extendedConnection, queryBuilder.toString());
            if (!queryArguments.isEmpty()) {
                for (Map.Entry<String, Object> entry : queryArguments.entrySet()) {
                    preparedStatement.setObject(entry.getKey().replaceFirst(":", ""), entry.getValue());
                }
            }

            if(Constants.showSql())
                Log.debug(QueryExecutor.class, "update", SQLFormatter.format(queryBuilder.toString()));

            Log.debug(QueryExecutor.class, "update", "Sling Ringer-"+
                    ConnectionManager.getConnectionIndex(extendedConnection)+": "+
                    extendedConnection.getUser()+"@"+extendedConnection.getCatalog());
            //Log.debug(QueryExecutor.class, "update", "Index: "+ConnectionManager.getConnectionIndex(extendedConnection));

            preparedStatement.executeUpdate();
            gc();
        } catch (Exception e) {
            if(preparedStatement != null){
                System.out.println("SQL Query: "+preparedStatement.getQuery());
            }
            e.printStackTrace();
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
        }
        return tWrapper;
    }


    public TransactionWrapper<FlexicoreArrayList> delete(QueryBuilder queryBuilder,
                                                         FlexicoreHashMap queryArguments) {
        TransactionWrapper<FlexicoreArrayList> tWrapper = new TransactionWrapper<>();
        String tableName = queryArguments.getTableName();
        ExtendedConnection extendedConnection = ConnectionManager.getConnection();
        try {
            validateQueryArguments(queryBuilder.toString(), queryArguments);
            QueryBuilder selectQueryBuilder = new QueryBuilder();
            selectQueryBuilder.select().from(tableName);
            selectQueryBuilder.where(queryBuilder.getWhereClause());
            FlexicoreArrayList oldValuesList = select(selectQueryBuilder.toString(), queryArguments).getData();
            tWrapper.setData(oldValuesList);

            preparedStatement = NamedPreparedStatement.prepareStatement(extendedConnection, queryBuilder.toString());
            if (!queryArguments.isEmpty()) {
                for (Map.Entry<String, Object> entry : queryArguments.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    preparedStatement.setObject(key.replaceFirst(":", ""), value);
                }
            }

            if(Constants.showSql())
                Log.debug(QueryExecutor.class, "delete", SQLFormatter.format(queryBuilder.toString()));
            
            Log.debug(QueryExecutor.class, "delete", "Sling Ringer-"+
                    ConnectionManager.getConnectionIndex(extendedConnection)+": "+
                    extendedConnection.getUser()+"@"+extendedConnection.getCatalog());
            //Log.debug(QueryExecutor.class, "delete", "Index: "+ConnectionManager.getConnectionIndex(extendedConnection));

            preparedStatement.executeUpdate();
            gc();
        } catch (Exception e) {
            if(preparedStatement != null){
                System.out.println("SQL Query: "+preparedStatement.getQuery());
            }
            e.printStackTrace();
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
            if (e instanceof CJCommunicationsException || e instanceof CommunicationsException || e instanceof ConnectionIsClosedException || e instanceof SQLNonTransientConnectionException) {
                ConnectionManager.removeExpiredConnection(extendedConnection);
                tWrapper = delete(queryBuilder, queryArguments);
            }
        }
        extendedConnection.release();
        return tWrapper;
    }

    public TransactionWrapper<HashSet> getPrimaryKeyColumns(String tableName) {
        TransactionWrapper<HashSet> tWrapper = new TransactionWrapper<>();
        ExtendedConnection extendedConnection = ConnectionManager.getConnection();
        tWrapper.setData(getPKColumns(extendedConnection, tableName));
        gc();
        extendedConnection.release();
        return tWrapper;
    }

    public TransactionWrapper<List<String>> getColumns(String tableName) {
        TransactionWrapper<List<String>>tWrapper = new TransactionWrapper<>();
        ExtendedConnection extendedConnection = ConnectionManager.getConnection();

        List<String> columnNames = new ArrayList<>();
        DatabaseMetaData databaseMetaData = null;
        ResultSet rs = null;
        try {
            assert extendedConnection != null;
            databaseMetaData = extendedConnection.getMetaData();
            rs = databaseMetaData.getColumns(Q.getParentDatabase(tableName), null, tableName, null);
            while (rs.next()) {
                columnNames.add(rs.getString("COLUMN_NAME"));
            }
            tWrapper.setData(columnNames);
        } catch (Exception e) {
            if(preparedStatement != null){
                System.out.println("SQL Query: "+preparedStatement.getQuery());
            }
            e.printStackTrace();
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
            if (e instanceof CJCommunicationsException || e instanceof CommunicationsException || e instanceof ConnectionIsClosedException || e instanceof SQLNonTransientConnectionException) {
                ConnectionManager.removeExpiredConnection(extendedConnection);
                tWrapper = getColumns(tableName);
            }
        } finally {
            JvmManager.gc(databaseMetaData, rs);
        }

        gc();
        extendedConnection.release();
        return tWrapper;
    }

    private HashSet<String> getPKColumns(ExtendedConnection extendedConnection, String tableName) {
        HashSet<String> primaryKeyColumnNames = new HashSet<>();
        DatabaseMetaData databaseMetaData = null;
        ResultSet rs = null;
        try {
            assert extendedConnection != null;
            databaseMetaData = extendedConnection.getMetaData();
            rs = databaseMetaData.getPrimaryKeys(Q.getParentDatabase(tableName), null, tableName);
            while (rs.next()) {
                primaryKeyColumnNames.add(rs.getString("COLUMN_NAME"));
            }
        } catch (Exception e) {
            if(preparedStatement != null){
                System.out.println("SQL Query: "+preparedStatement.getQuery());
            }
            e.printStackTrace();
            if (e instanceof CJCommunicationsException || e instanceof CommunicationsException || e instanceof ConnectionIsClosedException || e instanceof SQLNonTransientConnectionException) {
                ConnectionManager.removeExpiredConnection(extendedConnection);
                primaryKeyColumnNames = getPKColumns(extendedConnection, tableName);
            }
        } finally {
            JvmManager.gc(databaseMetaData, rs);
        }
        return primaryKeyColumnNames;
    }

    private String getAutoIncrementedPKColumn(ExtendedConnection connection, String tableName) throws SQLException {
        String pkColumnName = null;
        DatabaseMetaData meta = connection.getMetaData();
        ResultSet rs = meta.getPrimaryKeys(Q.getParentDatabase(tableName), null, tableName);
        while (rs.next()) {
            pkColumnName = rs.getString("COLUMN_NAME");
            ResultSet resultSet = connection.createStatement().executeQuery(
                    "SELECT " + pkColumnName + " FROM " + Q.getParentDatabase(tableName)+"."+tableName + " LIMIT 1");
            ResultSetMetaData metadata = resultSet.getMetaData();
            if (metadata.isAutoIncrement(1)) {
                break;
            }
        }
        return pkColumnName;
    }

    private List<String> validateQueryArguments(String query, FlexicoreHashMap queryArguments) throws Exception {
        List<String> allQueryVariables = new ArrayList<>();
        Pattern pattern = Pattern.compile(":\\w+");
        Matcher matcher = pattern.matcher(query);
        while (matcher.find()) {
            String queryVar = matcher.group();
            if (!queryArguments.containsKey(queryVar)) {
                throw new Exception(queryVar + " value not provided in query arguments");
            }
            allQueryVariables.add(queryVar);
        }
        return allQueryVariables;
    }

    private Object[] prepareProvidedPKSelect(FlexicoreHashMap queryArguments) throws Exception {
        QueryBuilder queryBuilder = new QueryBuilder();
        FilterPredicate whereFilterPredicate = new FilterPredicate();
        FlexicoreHashMap newQueryArguments = new FlexicoreHashMap(queryArguments.getTableName());
        queryBuilder.select().from(queryArguments.getTableName());

        TransactionWrapper<HashSet> twrapper = getPrimaryKeyColumns(queryArguments.getTableName());
        HashSet<String> primaryKeyColumns = twrapper.getData();
        if (primaryKeyColumns == null) primaryKeyColumns = new HashSet<>();
        boolean addAnd = false;
        for (Map.Entry<String, Object> entry : queryArguments.entrySet()) {
            String key = entry.getKey();
            if (primaryKeyColumns.contains(key.replaceFirst(":", ""))) {
                if (addAnd) whereFilterPredicate.and();
                whereFilterPredicate.equalTo(key.replaceFirst(":", ""), key);
                newQueryArguments.addQueryArgument(key, entry.getValue());
                addAnd = true;
            }
        }
        queryBuilder.where(whereFilterPredicate);
        return new Object[]{queryBuilder, newQueryArguments};
    }

    private void gc() {
        try {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } finally {
                    preparedStatement = null;
                }
            }

            if (resultSet != null) {
                try {
                    resultSet.close();
                } finally {
                    resultSet = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            preparedStatement = null;
            resultSet = null;
        }
    }
}
