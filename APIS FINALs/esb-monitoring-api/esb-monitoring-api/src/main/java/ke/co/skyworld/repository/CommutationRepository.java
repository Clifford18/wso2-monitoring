package ke.co.skyworld.repository;

import ke.co.skyworld.flexicore.connection_manager.connection.ConnectionManager;
import ke.co.skyworld.flexicore.connection_manager.connection.ExtendedConnection;
import ke.co.skyworld.repository.beans.FlexicoreArrayList;
import ke.co.skyworld.repository.beans.FlexicoreHashMap;
import ke.co.skyworld.repository.beans.TransactionWrapper;
import ke.co.skyworld.repository.query.FilterPredicate;
import ke.co.skyworld.repository.query.QueryBuilder;
import ke.co.skyworld.utils.memory.JvmManager;

import java.sql.SQLException;

public class CommutationRepository {
    private final ExtendedConnection extendedConnection;

    public CommutationRepository() {
        extendedConnection = new ExtendedConnection(ConnectionManager.getConnectionOutsidePool());
        try {
            extendedConnection.makeBusy();
            extendedConnection.setAutoCommit(false);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void commit(){
        try {
            extendedConnection.commit();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        extendedConnection.release();

        try {
            extendedConnection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        JvmManager.gc(extendedConnection);
    }

    public void rollback(){
        try {
            extendedConnection.rollback();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        extendedConnection.release();

        try {
            extendedConnection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        JvmManager.gc(extendedConnection);
    }

    public TransactionWrapper insertAutoIncremented(String tableName, FlexicoreHashMap recordHashMap) {
        return Repository.insertAutoIncremented(extendedConnection, tableName, recordHashMap);
    }

    public TransactionWrapper insertAutoIncremented(String tableName, String[] insertColumns,
                                                    FlexicoreHashMap queryArguments) {
        return Repository.insertAutoIncremented(extendedConnection, tableName, insertColumns, queryArguments);
    }

    //update is a bit unique. The last query arguments are specifically for the WHERE clause for the update statement.
    public TransactionWrapper<FlexicoreArrayList> update(String tableName,
                                                         FlexicoreHashMap updateSet,
                                                         FilterPredicate filterPredicate,
                                                         FlexicoreHashMap queryArguments) {
        return Repository.update(extendedConnection, tableName, updateSet, filterPredicate, queryArguments);
    }

    public TransactionWrapper<FlexicoreArrayList> update(QueryBuilder queryBuilder,
                                                         FlexicoreHashMap updateSet,
                                                         FlexicoreHashMap queryArguments) {
        return Repository.update(extendedConnection, queryBuilder, updateSet, queryArguments);
    }
}
