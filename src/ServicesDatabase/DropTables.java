package ServicesDatabase;
import java.sql.*;

public class DropTables {
    private static final Connection DB_conn = (new BankItUpDBConnector()).init();

    public void dropAllTables() throws SQLException{
        Statement stmt = DB_conn.createStatement();
        String[] drop_tables = {"customer_account", "customer", "person", "deposit", "withdrawal", "transfer",  "transaction", "savings_account", "account"};
        for(int i = 0; i < drop_tables.length; i++){
            stmt.execute("DROP TABLE " + drop_tables[i]);
            System.out.println("Table '" + drop_tables[i] + "' has been dropped");
        }
        System.out.println();
    }
}
