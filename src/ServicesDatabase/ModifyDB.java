package ServicesDatabase;
import java.sql.*;
import ServicesDatabase.*;
import Person.*;
import Account.*;
import Transactions.*;
public class ModifyDB {
    private static ModifyDB modifySingleton = ModifyDB.getModifySingletonInstance();
    private static final Connection DB_conn = (new BankItUpDBConnector()).init();
    private ModifyDB(){}
    public static synchronized ModifyDB getModifySingletonInstance(){
        if(modifySingleton == null)
            modifySingleton = new ModifyDB();

        return modifySingleton;
    }

    public void updateTableRow(String tableName, int rowID, String columnName, String columnNewValue) throws SQLException {
        String SQL_stmt = "UPDATE " + tableName + " set " + columnName + " = " + columnNewValue + "where id = " + rowID;
        PreparedStatement stmt = DB_conn.prepareStatement(SQL_stmt);
        stmt.execute();
    }

    public void deleteTableRow(String tableName, int rowID) throws SQLException {
        String SQL_stmt = "DELETE FROM " + tableName + " WHERE id = " + rowID;
        PreparedStatement stmt = DB_conn.prepareStatement(SQL_stmt);
        stmt.execute();
    }
}
