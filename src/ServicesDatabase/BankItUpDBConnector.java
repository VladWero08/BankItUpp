package ServicesDatabase;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class BankItUpDBConnector {
    private static final String database_URL = "";
    private static final String database_USER = "";
    private static final String database_password = "";
    public Connection init(){
        try{
            return DriverManager.getConnection(database_URL, database_USER, database_password);
        } catch(Exception e){
            System.out.println(e);
            return null;
        }
    }
}
