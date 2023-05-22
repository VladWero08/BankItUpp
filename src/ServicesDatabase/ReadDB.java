package ServicesDatabase;
import java.sql.*;
import ServicesDatabase.*;
import Person.*;
import Account.*;
import Transactions.*;

public class ReadDB {
    private static ReadDB readSingleton = ReadDB.getReadSingletonInstance();
    private static final Connection DB_conn = (new BankItUpDBConnector()).init();
    private ReadDB(){}
    public static synchronized ReadDB getReadSingletonInstance(){
        if(readSingleton == null)
            readSingleton = new ReadDB();

        return readSingleton;
    }

    // General function that will handle SELECT statements
    // with multiple rows responses
    public void readAllEntries(String tableName, String SQL_query) throws SQLException{
        Statement stmt = DB_conn.createStatement();
        // execute the query to get the information about all
        // the entries in the table
        ResultSet result = stmt.executeQuery(SQL_query);
        // get metadata to access column names
        ResultSetMetaData result_metadata = result.getMetaData();
        int numOfAttributes = result_metadata.getColumnCount();
        int numOfResponses = 1;

        // for every response, display the data
        while(result.next()){
            System.out.println("( #" + numOfResponses + " ) ------------------>");
            for(int i = 1; i <= numOfAttributes; i++){
                System.out.println(result_metadata.getColumnName(i) + ": " + result.getObject(i));
            }
            System.out.println();
            numOfResponses += 1;
        }

        // if there were no queries
        if(numOfResponses == 1)
            System.out.println("There are no entries in the table " +  tableName.toUpperCase() + "!");
    }

    // General function that will handle SELECT statements
    // with single row responses
    public void readSingleEntryById(String tableName, String SQL_query) throws SQLException{
        Statement stmt = DB_conn.createStatement();
        // execute the query to get the information the entry
        ResultSet result = stmt.executeQuery(SQL_query);
        // get metadata to access column names
        ResultSetMetaData result_metadata = result.getMetaData();
        int numOfAttributes = result_metadata.getColumnCount();
        boolean numOfResponses = false;

        // display the values
        if(result.next()){
            for(int i = 1; i <= numOfAttributes; i++){
                System.out.println(result_metadata.getColumnName(i) + ": " + result.getObject(i));
            }
            System.out.println();
            numOfResponses = true;
        }

        // if there were no queries
        if(!numOfResponses)
            System.out.println("There is no entry in the table " +  tableName.toUpperCase() + " with the specific ID!");
    }

    // Customer queries
    public void readSingleCustomer(int customerID) throws SQLException {
        String SQL_stmt = "SELECT * FROM customer INNER JOIN person ON customer.id = person.id WHERE customer.id = " + customerID;
        this.readSingleEntryById("customer", SQL_stmt);
    }

    public void readAllCustomers() throws SQLException {
        String SQL_stmt = "SELECT * FROM customer INNER JOIN person ON customer.id = person.id";
        this.readAllEntries("customer", SQL_stmt);
    }

    // Savings account queries
    public void readSingleSavingsAccount(int accountID) throws SQLException{
        String SQL_stmt = "SELECT * FROM savings_account INNER JOIN account ON account.id = savings_account.id WHERE savings_account.id = " + accountID;
        this.readSingleEntryById("savings_account", SQL_stmt);
    }

    public void readAllSavingsAccount() throws SQLException{
        String SQL_stmt = "SELECT * FROM savings_account INNER JOIN account ON account.id = savings_account.id;";
        this.readAllEntries("savings_account", SQL_stmt);
    }

    // Transaction queries
    public void readSingleDeposit(int depositID) throws SQLException{
        System.out.println("*** THIS IS A DEPOSIT TRANSACTION ***");
        String SQL_stmt = "SELECT * FROM deposit INNER JOIN transaction ON deposit.id = transaction.id WHERE transaction.id = " + depositID;
        this.readSingleEntryById("deposit", SQL_stmt);
    }
    public void readSingleTransfer(int transferID) throws SQLException{
        System.out.println("*** THIS IS A TRANSFER TRANSACTION ***");
        String SQL_stmt = "SELECT * FROM transfer INNER JOIN transaction ON transfer.id = transaction.id WHERE transaction.id = " + transferID;
        this.readSingleEntryById("transfer", SQL_stmt);
    }
    public void readSingleWithdrawal(int withdrawalID) throws SQLException{
        System.out.println("*** THIS IS A WITHDRAWAL TRANSACTION ***");
        String SQL_stmt = "SELECT * FROM withdrawal INNER JOIN transaction ON withdrawal.id = transaction.id WHERE transaction.id = " + withdrawalID;
        this.readSingleEntryById("withdrawal", SQL_stmt);
    }

}
