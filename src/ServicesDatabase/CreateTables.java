package ServicesDatabase;
import Services.ServiceBank;

import java.sql.*;

public class CreateTables {
    private static final Connection DB_conn = (new BankItUpDBConnector()).init();

    public void createAllTables() throws SQLException{
        this.createPersonTable();
        this.createCustomerTable();
        this.createAccountTable();
        this.createCustomerAccountAssociativeTable();

        this.createTransactionTable();
        this.createDepositTable();
        this.createWithdrawalTable();
        this.createTransferTable();
        this.createSavingsAccountTable();
        System.out.println();
    }
    public void createTable(String table_name, String table_query) throws SQLException {
        // Get connection to the database and check if the desired table exists
        // If it doesn't, execute the query for creation
        DatabaseMetaData meta_data = DB_conn.getMetaData();
        ResultSet tables = meta_data.getTables(null, null, table_name, null);

        if(!tables.next()){
            Statement stmt = DB_conn.createStatement();
            stmt.execute(table_query);
            System.out.println("Table '" + table_name + "' has been successfully created!");
        } else {
            System.out.println("Table '" + table_name + "' already exists.");
        }
    }
    public void createPersonTable() throws SQLException {
        String person = "person";
        String personTable = "CREATE TABLE PERSON(" +
                "id INTEGER not NULL," +
                "firstName VARCHAR(255) not NULL," +
                "lastName VARCHAR(255) not NULL," +
                "birthYear INTEGER not NULL," +
                "email VARCHAR(255) not NULL," +
                "phoneNumber VARCHAR(255) not NULL," +
                "income FLOAT," +
                "gender VARCHAR(50)," +
                "PRIMARY KEY ( id ))";
        this.createTable(person, personTable);
    }

    public void createCustomerTable() throws SQLException{
        String customer = "customer";
        String customerTable = "CREATE TABLE CUSTOMER(" +
                "id INTEGER not NULL," +
                "numAccounts INTEGER not NULL," +
                "PRIMARY KEY (id)," +
                "FOREIGN KEY (id) REFERENCES person(id) ON DELETE CASCADE)";
        this.createTable(customer, customerTable);
    }
    public void createAccountTable() throws SQLException{
        String account = "account";
        String accountTable = "CREATE TABLE ACCOUNT(" +
                "id INTEGER not NULL," +
                "creationDate DATE not NULL," +
                "expireDate DATE not NULL," +
                "balance DOUBLE not NULL," +
                "PRIMARY KEY (id))";
        this.createTable(account, accountTable);
    }
    public void createCustomerAccountAssociativeTable() throws SQLException{
        String customer_account = "customer_account";
        String customer_account_table = "CREATE TABLE CUSTOMER_ACCOUNT(" +
                "customer_id INTEGER not NULL," +
                "account_id INTEGER not NULL," +
                "FOREIGN KEY (customer_id) REFERENCES customer(id) ON DELETE CASCADE," +
                "FOREIGN KEY (account_id) REFERENCES account(id) ON DELETE CASCADE," +
                "PRIMARY KEY (customer_id, account_id))";
        this.createTable(customer_account, customer_account_table);
    }

    public void createTransactionTable() throws SQLException{
        String transaction = "transaction";
        String transaction_table = "CREATE TABLE TRANSACTION(" +
                "id INTEGER not NULL," +
                "account_id INTEGER not NULL," +
                "creationDate DATE not NULL," +
                "amount FLOAT not NULL," +
                "currency VARCHAR(255)," +
                "status VARCHAR(255)," +
                "FOREIGN KEY (account_id) REFERENCES account(id) ON DELETE CASCADE," +
                "PRIMARY KEY (id))";
        this.createTable(transaction, transaction_table);
    }

    public void createDepositTable() throws SQLException{
        String deposit = "deposit";
        String deposit_table = "CREATE TABLE DEPOSIT(" +
                "id INTEGER not NULL," +
                "description VARCHAR(255) not NULL," +
                "deposit_method VARCHAR(255) not NULL," +
                "FOREIGN KEY (id) REFERENCES transaction(id) ON DELETE CASCADE," +
                "PRIMARY KEY (id))";
        this.createTable(deposit, deposit_table);
    }
    public void createWithdrawalTable() throws SQLException{
        String withdrawal = "withdrawal";
        String withdrawal_table = "CREATE TABLE WITHDRAWAL(" +
                "id INTEGER not NULL," +
                "destination_account_id INTEGER," +
                "withdrawal_type VARCHAR(255) not NULL," +
                "description VARCHAR(255)," +
                "FOREIGN KEY (destination_account_id) REFERENCES account(id)," +
                "FOREIGN KEY (id) REFERENCES transaction(id) ON DELETE CASCADE," +
                "PRIMARY KEY (id))";
        this.createTable(withdrawal, withdrawal_table);
    }
    public void createTransferTable() throws SQLException{
        String transfer = "transfer";
        String transfer_table = "CREATE TABLE TRANSFER(" +
                "id INTEGER not NULL," +
                "destination_account_id INTEGER," +
                "scheduled_date DATE," +
                "end_date DATE," +
                "frequency INTEGER," +
                "CONSTRAINT check_type_of_transfer CHECK ((scheduled_date IS NULL AND end_date IS NULL AND frequency IS NULL) OR " +
                    "(scheduled_date IS NOT NULL AND end_date IS NULL AND frequency IS NULL) OR (scheduled_date IS NULL AND end_date IS NOT NULL AND frequency IS NOT NULL))," +
                "FOREIGN KEY (destination_account_id) REFERENCES transaction(id)," +
                "FOREIGN KEY (id) REFERENCES transaction(id) ON DELETE CASCADE," +
                "PRIMARY KEY (id))";
        this.createTable(transfer, transfer_table);
    }

    public void createSavingsAccountTable() throws SQLException{
        String transfer = "savings_account";
        String transfer_table = "CREATE TABLE savings_account(" +
                "id INTEGER not NULL," +
                "maximum_withdrawals INTEGER not NULL," +
                "withdrawal_fee FLOAT," +
                "savings_goal FLOAT," +
                "interest_rate FLOAT," +
                "redeemed_money DATE," +
                "FOREIGN KEY (id) REFERENCES account(id) ON DELETE CASCADE," +
                "PRIMARY KEY (id))";
        this.createTable(transfer, transfer_table);
    }
}
