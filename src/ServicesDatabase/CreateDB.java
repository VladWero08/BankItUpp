package ServicesDatabase;
import java.time.LocalDate;
import java.time.ZoneId;
import java.sql.*;
import ServicesDatabase.*;
import Person.*;
import Account.*;
import Transactions.*;
public class CreateDB {
    private static CreateDB createSingleton = CreateDB.getCreateSingletonInstance();
    private static final Connection DB_conn = (new BankItUpDBConnector()).init();
    private CreateDB(){}
    public static synchronized CreateDB getCreateSingletonInstance(){
        if(createSingleton == null)
            createSingleton = new CreateDB();

        return createSingleton;
    }

    // Person and customer inserts
    public void createPerson(Person newPerson) throws SQLException {
        String createPerson = "INSERT INTO person VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
        PreparedStatement stmt = DB_conn.prepareStatement(createPerson);

        stmt.setInt(1, newPerson.getID().intValue());
        stmt.setString(2, newPerson.getFirstName());
        stmt.setString(3, newPerson.getLastName());
        stmt.setInt(4, newPerson.getBirthDate().getYear());
        stmt.setString(5, newPerson.getEmail());
        stmt.setString(6, newPerson.getPhoneNumber());
        stmt.setFloat(7, newPerson.getIncome());
        stmt.setString(8, newPerson.getGender());

        stmt.executeUpdate();
    }
    public void createCustomer(Customer newCustomer) throws SQLException {
        String createCustomer = "INSERT INTO customer VALUES (?, ?);";
        PreparedStatement stmt = DB_conn.prepareStatement(createCustomer);

        stmt.setInt(1, newCustomer.getID().intValue());
        stmt.setInt(2, newCustomer.getNumAccounts());

        stmt.executeUpdate();
    }

    public Date localDateTODate(LocalDate date){
        return Date.valueOf(date);
    }

    // Account inserts
    public void createAccount(Account newAccount) throws SQLException{
        String createAccount = "INSERT INTO account VALUES(?, ?, ?, ?);";
        PreparedStatement stmt = DB_conn.prepareStatement(createAccount);

        stmt.setInt(1, newAccount.getAccountID().intValue());
        stmt.setDate(2, localDateTODate(newAccount.getAccountCreationDate()));
        stmt.setDate(3, localDateTODate(newAccount.getAccountExpireDate()));
        stmt.setDouble(4, newAccount.getAccountBalance());

        stmt.executeUpdate();
    }

    public void createCustomerAccount(int customerID, int accountID) throws SQLException{
        String createCustomerAccount = "INSERT INTO customer_account VALUES(?, ?);";
        PreparedStatement stmt = DB_conn.prepareStatement(createCustomerAccount);

        stmt.setInt(1, customerID);
        stmt.setInt(2, accountID);
        stmt.execute();
    }

    public void createSavingsAccount(SavingsAccount newSavingsAccount) throws SQLException{
        String createSavingsAccount = "INSERT INTO savings_account VALUES(?, ?, ?, ?, ? ,?);";
        PreparedStatement stmt = DB_conn.prepareStatement(createSavingsAccount);

        stmt.setInt(1, newSavingsAccount.getAccountID().intValue());
        stmt.setInt(2, newSavingsAccount.getMaximumWithdrawals());
        stmt.setFloat(3, newSavingsAccount.getWithdrawalFee());
        stmt.setFloat(4, newSavingsAccount.getSavingsGoal());
        stmt.setFloat(5, newSavingsAccount.getInterestRate());
        stmt.setDate(6, localDateTODate(newSavingsAccount.getRedeemedMoney()));

        stmt.executeUpdate();
    }

    // Transaction inserts
    public void createTransaction(Transaction newTransaction) throws SQLException{
        String createTransaction = "INSERT INTO transaction VALUES(?, ?, ?, ?, ?, ?);";
        PreparedStatement stmt = DB_conn.prepareStatement(createTransaction);

        stmt.setInt(1, newTransaction.getTransactionID().intValue());
        stmt.setInt(2, newTransaction.getAccountID().intValue());
        stmt.setDate(3, localDateTODate(newTransaction.getTransactionDate()));
        stmt.setFloat(4, newTransaction.getAmount());
        stmt.setString(5, newTransaction.getCurrency());
        stmt.setString(6, newTransaction.getStatus());

        stmt.execute();
    }

    public void createDeposit(Deposit newDeposit) throws SQLException{
        String createDeposit = "INSERT INTO deposit VALUES (?, ?, ?);";
        String modifyBalance = "UPDATE account SET balance = balance + " + newDeposit.getAmount() + " WHERE id = " + newDeposit.getAccountID().intValue();
        PreparedStatement stmt = DB_conn.prepareStatement(createDeposit);

        stmt.setInt(1, newDeposit.getTransactionID().intValue());
        stmt.setString(2, newDeposit.getDescription());
        stmt.setString(3, newDeposit.getDepositMethod());
        stmt.execute();

        // Update the balance of the corresponding account
        stmt = DB_conn.prepareStatement(modifyBalance);
        stmt.execute();
    }
    public void createWithdrawal(Withdrawal newWithdrawal) throws SQLException{
        String createWithdrawal = "INSERT INTO withdrawal VALUES (?, ?, ?, ?);";
        String modifyBalance = "UPDATE account SET balance = balance - " + newWithdrawal.getAmount() + " WHERE id = " + newWithdrawal.getAccountID().intValue();
        PreparedStatement stmt = DB_conn.prepareStatement(createWithdrawal);

        stmt.setInt(1, newWithdrawal.getTransactionID().intValue());
        stmt.setInt(2, newWithdrawal.getDestinationAccountID().intValue());
        stmt.setString(3, newWithdrawal.getWithdrawalType());
        stmt.setString(4, newWithdrawal.getDescription());
        stmt.execute();

        // Update the balance of the corresponding account
        stmt = DB_conn.prepareStatement(modifyBalance);
        stmt.execute();

        // Check if withdrawal was made into someone else's account,
        // because that balance needs to be updated too
        if(newWithdrawal.getDestinationAccountID() != null){
            String modifyDestinationAccount = "UPDATE account SET balance = balance + " + newWithdrawal.getAmount() + " WHERE id = " + newWithdrawal.getDestinationAccountID().intValue();
            stmt = DB_conn.prepareStatement(modifyDestinationAccount);
            stmt.execute();
        }
    }
    public void createTransfer(Transfer newTransfer) throws SQLException{
        String createTransfer = "INSERT INTO transfer VALUES (?, ?, ?, ?, ?);";
        String modifySourceBalance = "UPDATE account SET balance = balance - " + newTransfer.getAmount() + " WHERE id = " + newTransfer.getAccountID().intValue();
        String modifyDestinationBalance = "UPDATE account SET balance = balance + " + newTransfer.getAmount() + " WHERE id = " + newTransfer.getDestinationAccountID().intValue();
        PreparedStatement stmt = DB_conn.prepareStatement(createTransfer);

        stmt.setInt(1, newTransfer.getAccountID().intValue());
        stmt.setInt(2, newTransfer.getDestinationAccountID().intValue());
        stmt.setDate(3, localDateTODate(newTransfer.getScheduled_date()));
        stmt.setDate(4, localDateTODate(newTransfer.getEnd_date()));
        stmt.setInt(5, newTransfer.getFrequency());
        stmt.execute();

        // Now transfer the money from one user to another
        stmt = DB_conn.prepareStatement(modifySourceBalance);
        stmt.execute();
        stmt = DB_conn.prepareStatement(modifyDestinationBalance);
        stmt.execute();
    }
}
