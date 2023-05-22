package Services;

import Bank.Bank;
import Person.*;
import Services.*;
import ServicesDatabase.ReadDB;
import Transactions.*;
import Account.*;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;

public class ServiceBank {
    private Bank bank = Bank.getBankInstance();
    private Scanner scanner = new Scanner(System.in);

    private BackLog backLog = BackLog.getBackLogInstance();
    private ServicePerson servicePerson = new ServicePerson();
    private ServiceAccount serviceAccount = new ServiceAccount();
    private ServiceTransaction serviceTransaction = new ServiceTransaction();

    private ReadDB read_singleton = ReadDB.getReadSingletonInstance();

    public void displayWelcomeText(){
        System.out.println("Welcome to BankItUp! We are very pleased to have you here.\n Within our bank application you will be able to perform different actions, such as: \n");
        System.out.println("-->-->> Build a customer profile that can have multiple accounts;");
        System.out.println("-->-->> Set up savings, checking or credit accounts as a customer;");
        System.out.println("-->-->> Depending on the type of account you have, you can: deposit, withdraw, transfer or loan money;");
        System.out.println("-->-->> You will be able to see statistics about your transactions and goals;");
        System.out.println("-->-->> NOTE! If you will set up an account for a longer period of time, you will have the interest rates in your favor!");
        System.out.println("-->-->> S O O N: Write check and automatically having them in HTML format.");
        System.out.println();
        System.out.println("-->-->>---->---->> Sounds interesting? We'll let you play around! <<----<----<<--<--");
        System.out.println();
    }

    public void createCustomerLoopOptions(){
        System.out.println("CUSTOMER --- MENU");
        System.out.println("1 --> Do you want to create a new customer? ");
        System.out.println("2 --> Do you want to use a customer that you have created?");
        System.out.println("3 --> Do you want to see all customers created? ");
        System.out.println("4 --> Do you want to see a specific customer? ");
        System.out.println("5 --> Do you want to edit details about a specific customer?");
        System.out.println("6 --> Do you want to delete a specific customer?");
        System.out.println("exit --> exit");
        System.out.print("Enter: ");
    }

    public void createCustomerLoop() throws ParseException, SQLException {
        String newCustomer = null;

        while(!Objects.equals(newCustomer, "exit")){
            this.createCustomerLoopOptions();
            newCustomer = scanner.nextLine();

            // Wait till the user enters a valid string
            while(!Objects.equals(newCustomer, "1") && !Objects.equals(newCustomer, "2")
                    && !Objects.equals(newCustomer, "3") && !Objects.equals(newCustomer, "4") &&
                    !Objects.equals(newCustomer, "5") && !Objects.equals(newCustomer, "6") &&
                    !Objects.equals(newCustomer, "exit")){
                System.out.print("Invalid! Try again: ");
                newCustomer = scanner.nextLine();
            }

            switch(newCustomer){
                // The user chose to create a new customer, so after the creation, it will be able to
                // create accounts as that customer
                case "1":
                    Customer customer = servicePerson.createCustomer();
                    createAccountLoop(customer.getCustomerID());
                    break;

                //  The user wants to use a customer that already exists
                case "2":
                    Long customerID;
                    System.out.print("Enter the ID of the customer you want to access: ");
                    customerID = scanner.nextLong();
                    while(bank.getCustomerAccounts().get(customerID) == null){
                        System.out.print("Invalid ID! Try again: ");
                        customerID = scanner.nextLong();
                    }
                    createAccountLoop(customerID);
                    break;

                // See all the customers that he built
                case "3":
                    servicePerson.displayAllCustomers();
                    break;

                // See specific customer depending on ID
                case "4":
                    long customerReadID;
                    System.out.print("Enter the ID of the customer you want to see:");
                    customerReadID = scanner.nextInt();

                    while(bank.getCustomerAccounts().get(customerReadID) == null){
                        System.out.print("Invalid ID! Try again: ");
                        customerReadID = scanner.nextLong();
                    }
                    servicePerson.displaySingleCustomer((int) customerReadID);

                // Edit customer's details
                case "5":
                    Long customerEditID;
                    System.out.print("Enter the ID of the customer you want to delete:");
                    customerEditID = scanner.nextLong();

                    while(bank.getCustomerAccounts().get(customerEditID) == null){
                        System.out.print("Invalid ID! Try again: ");
                        customerEditID = scanner.nextLong();
                    }
                    servicePerson.updateSingleCustomer(customerEditID);

                // Delete a specific customer
                case "6":
                    Long customerDeleteID;
                    System.out.print("Enter the ID of the customer you want to delete:");
                    customerDeleteID = scanner.nextLong();

                    while(bank.getCustomerAccounts().get(customerDeleteID) == null){
                        System.out.print("Invalid ID! Try again: ");
                        customerDeleteID = scanner.nextLong();
                    }
                    servicePerson.deleteSingleCustomer(customerDeleteID);

                case "exit":
                    break;

            }
        }
    }

    public void createAccountLoopOptions(){
        System.out.println("ACCOUNTS --- MENU");
        System.out.println("1 --> Do you want to create a new account? ");
        System.out.println("2 --> Do you want to use an account that you have created?");
        System.out.println("3 --> Do you want to see all accounts created? ");
        System.out.println("4 --> Do you want to see a specific account?");
        System.out.println("5 --> Do you want to delete an account?");
        System.out.println("exit --> exit");
        System.out.print("Enter: ");
    }

    public void createAccountLoop(long customerID) throws SQLException {
        String newAccount = null;

        while(!Objects.equals(newAccount, "exit")){
            this.createAccountLoopOptions();
            newAccount = scanner.nextLine();

            // Wait till the user enters a valid string
            while(!Objects.equals(newAccount, "1") && !Objects.equals(newAccount, "2")
                    && !Objects.equals(newAccount, "3") && !Objects.equals(newAccount, "4")
                    && !Objects.equals(newAccount, "5") && !Objects.equals(newAccount, "exit")){
                System.out.print("Invalid! Try again: ");
                newAccount = scanner.nextLine();
            }

            switch(newAccount){
                // The user chose to create a new customer, so after the creation, it will be able to
                // create accounts as that customer
                case "1":
                    useExistingAccountLoop(serviceAccount.createAccount(customerID));
                    break;

                //  The user wants to use a customer that already exists
                case "2":
                    Long accountID = null;
                    System.out.print("Enter the ID of the account you want to access: ");
                    customerID = scanner.nextLong();
                    while(bank.getServiceAccounts().get(accountID) == null){
                        System.out.print("Invalid ID! Try again: ");
                        accountID = scanner.nextLong();
                    }

                    Account existingAccount = bank.getServiceAccounts().get(accountID);
                    useExistingAccountLoop(existingAccount);
                    break;

                // Customer chose to see all the account that he built
                case "3":
                    Customer customer = bank.getCustomerAccounts().get(customerID);
                    for(Account acc : customer.getAccounts()){
                        // If the account is savings, get that entry from the database
                        if(acc instanceof SavingsAccount){ read_singleton.readSingleSavingsAccount(acc.getAccountID().intValue());}
                        // Else, get the account from the program
                        else if(acc instanceof CheckingAccount){ serviceAccount.displayCheckingAccount(acc);}
                        else { serviceAccount.displayCreditAccount(acc);}
                    }
                    backLog.writeLog("Displayed all accounts for customer " + customerID);
                    break;

                case "4":
                    System.out.print("Enter the ID of the account you want to see: "); long accID;
                    accID = scanner.nextLong();
                    while(bank.getServiceAccounts().get(accID) == null){
                        System.out.print("Invalid ID! Try again: ");
                        accID = scanner.nextLong();
                    }
                    Account accountToBeDisplayed = bank.getServiceAccounts().get(accID);

                    // If the account is savings, get that entry from the database
                    if(accountToBeDisplayed instanceof SavingsAccount){ read_singleton.readSingleSavingsAccount(accountToBeDisplayed.getAccountID().intValue());}
                    // Else, get the account from the program
                    else if(accountToBeDisplayed instanceof CheckingAccount){ serviceAccount.displayCheckingAccount(accountToBeDisplayed);}
                    else { serviceAccount.displayCreditAccount(accountToBeDisplayed);}

                    break;

                case "5":
                    System.out.print("Enter the ID of the account you want to delete: "); long delAccID;
                    delAccID = scanner.nextLong();
                    while(bank.getServiceAccounts().get(delAccID) == null){
                        System.out.println("Invalid ID! Try again: ");
                        delAccID = scanner.nextLong();
                    }
                    Account accountToBeDeleted = bank.getServiceAccounts().get(delAccID);
                    serviceAccount.deleteAccount(accountToBeDeleted);
                case "exit":
                    break;
            }
        }
    }

    public void useExistingAccountLoop(Account account) throws SQLException {
        if(account instanceof SavingsAccount)
            useExistingSavingsAccountLoop(account);
        else if(account instanceof CheckingAccount)
            useExistingCheckingAccountLoop(account);
        else
            useExistingCreditAccountLoop(account);
    }

    // Display messages options sectioned for each type of account
    // *** SAVINGS ACCOUNT ***
    public void SavingsAccountOptions(){
        System.out.println("SAVINGS ACCOUNT --- MENU");
        System.out.println("1 --> Do you want to make a transaction?");
        System.out.println("2 --> Do you want to redeem your money?");
        System.out.println("3 --> Do you want to generate more withdrawals?( it will make the withdrawal bigger)");
        System.out.println("4 --> Do you want to see how many years you should continue on saving till reaching your goal?");
        System.out.println("5 --> Do you want to see all your transactions till now?");
        System.out.println("exit --> exit");
        System.out.print("Enter: ");
    }

    public void useExistingSavingsAccountLoop(Account account) throws SQLException {
        String accountAction = null;

        while(!Objects.equals(accountAction, "exit")){
            this.SavingsAccountOptions();
            accountAction = scanner.nextLine();

            // Wait till the user enters a valid string
            while(!Objects.equals(accountAction, "1") && !Objects.equals(accountAction, "2")
                    && !Objects.equals(accountAction, "3")  && !Objects.equals(accountAction, "4")
                    && !Objects.equals(accountAction, "5") && !Objects.equals(accountAction, "exit")){
                System.out.print("Invalid! Try again: ");
                accountAction = scanner.nextLine();
            }

            switch(accountAction){
                // User chose to make a transaction
                case "1":
                    Transaction transaction = serviceTransaction.makeTransaction(account.getAccountID());
                    account.addTransaction(transaction);
                    System.out.print(transaction.getStatus());

                    // Check if the transaction's validity, and if it
                    // was valid insert it into the database
                    if(transaction.getStatus().substring(0, Math.min(transaction.getStatus().length(), 9)).equals("Confirmed")){
                        serviceTransaction.insertTransactionToDB(transaction);
                        backLog.writeLog("Created transaction " + transaction.getTransactionID() + " on account " + transaction.getTransactionID());
                    }
                    break;

                // User chose to redeem his money
                case "2":
                    String availableRedemption = ((SavingsAccount) account).pursueRedeemtionOfMoney();
                    System.out.println(availableRedemption);
                    break;

                // User chose to generate more withdrawals
                case "3":
                    int numOfWithdrawals;
                    System.out.print("Number of withdrawals( max 75): ");
                    numOfWithdrawals = scanner.nextInt();
                    while(numOfWithdrawals < 0 || numOfWithdrawals > 75){
                        System.out.print("Invalid number! Try again( max 75): ");
                        numOfWithdrawals = scanner.nextInt();
                    }

                    ((SavingsAccount) account).generateMoreWithdrawals(numOfWithdrawals);
                    backLog.writeLog("Generated more withdrawals on account " + account.getAccountID());
                    break;

                case "4":
                    System.out.println("You will reach your goal in " + ((SavingsAccount) account).calculateYearsTillSavingsGoal() + " years.");
                    backLog.writeLog("Checked when goal is reached for account " + account.getAccountID());
                    break;

                case "5":
                    serviceAccount.displayAllTransactions(account);
                case "exit":
                    break;
            }
        }
    }

    // *** CHECKING ACCOUNT ***
    public void CheckingAccountOptions(){
        System.out.println("CHECKING ACCOUNT --- MENU");
        System.out.println("1 --> Do you want to make a transaction? ");
        System.out.println("2 --> Do you want to pay a bill?");
        System.out.println("exit --> exit");
        System.out.print("Enter: ");
    }

    public void useExistingCheckingAccountLoop(Account account){
        String accountAction = null;

        while(!Objects.equals(accountAction, "exit")){
            this.CheckingAccountOptions();
            accountAction = scanner.nextLine();

            // Wait till the user enters a valid string
            while(!Objects.equals(accountAction, "1") && !Objects.equals(accountAction, "2")
                     && !Objects.equals(accountAction, "exit")){
                System.out.print("Invalid! Try again: ");
                accountAction = scanner.nextLine();
            }

            switch(accountAction){
                // User chose to make a transaction
                case "1":
                    Transaction transaction = serviceTransaction.makeTransaction(account.getAccountID());
                    account.addTransaction(transaction);
                    System.out.print(transaction.getStatus());

                    if(transaction.getStatus().substring(0, Math.min(transaction.getStatus().length(), 9)).equals("Confirmed")){
                        backLog.writeLog("Created transaction " + transaction.getTransactionID() + " on account " + transaction.getTransactionID());
                    }
                    break;

                case "2":
                    String billName; Float billValue;
                    System.out.print("For what utility are you paying this bill for? ");
                    billName = scanner.nextLine();
                    while(Objects.equals(billName, "")){
                        System.out.print("Invalid! Try again: ");
                        billName = scanner.nextLine();
                    }

                    System.out.print("How much does the bill costs? ");
                    billValue = scanner.nextFloat();
                    while(billValue < 0){
                        System.out.print("Invalid! Try again: ");
                        billValue = scanner.nextFloat();
                    }

                    System.out.println(((CheckingAccount) account).payBill(billName, billValue));
                    backLog.writeLog("Payed bill on account " + account.getAccountID());
                    break;

                case "exit":
                    break;
            }
        }
    }

    // *** CREDIT ACCOUNT ***
    public void CreditAccountOptions(){
        System.out.println("CREDIT ACCOUNT --- MENU");
        System.out.println("1 --> Do you want to make a transaction? ");
        System.out.println("2 --> Do you want to pay a loan? ");
        System.out.println("3 --> Do you want to see all your loans and how much you paid till the current moment? ");
        System.out.println("exit --> exit");
        System.out.print("Enter: ");
    }

    public void useExistingCreditAccountLoop(Account account){
        String accountAction = null;

        while(!Objects.equals(accountAction, "exit")){
            this.CreditAccountOptions();
            accountAction = scanner.nextLine();

            // Wait till the user enters a valid string
            while(!Objects.equals(accountAction, "1") && !Objects.equals(accountAction, "2")
                    && !Objects.equals(accountAction, "3")  && !Objects.equals(accountAction, "exit")){
                System.out.print("Invalid! Try again: ");
                accountAction = scanner.nextLine();
            }

            switch(accountAction){
                // User chose to make a transaction
                case "1":
                    Transaction transaction = serviceTransaction.makeTransaction(account.getAccountID());
                    account.addTransaction(transaction);
                    System.out.print(transaction.getStatus());

                    if(transaction.getStatus().substring(0, Math.min(transaction.getStatus().length(), 9)).equals("Confirmed")){
                        backLog.writeLog("Created transaction " + transaction.getTransactionID() + " on account " + transaction.getTransactionID());
                    }
                    break;

                case "2":
                    Long loanID; int monthsToBePaid;
                    System.out.print("Enter the ID of the loan you want to pay: ");
                    loanID = scanner.nextLong();
                    while(loanID < 0){
                        System.out.print("Invalid! Try again: ");
                        loanID = scanner.nextLong();
                    }

                    System.out.print("On how many months do you want to pay the loan? ");
                    monthsToBePaid = scanner.nextInt();
                    while(monthsToBePaid < 0){
                        System.out.print("Invalid! Try again: ");
                        monthsToBePaid = scanner.nextInt();
                    }

                    System.out.println(((CreditAccount) account).payLoan(loanID, monthsToBePaid));
                    backLog.writeLog("Loan paid on account " + account.getAccountID());
                    break;

                case "3":
                    serviceAccount.displayUserLoans(account);
                    break;

                case "exit":
                    break;
            }
        }
    }

    public List<Account> mostRichAccounts() throws SQLException {
        List<Account> allAccounts = new ArrayList<Account>(bank.getServiceAccounts().size());

        for(Map.Entry<Long, Account> entry: bank.getServiceAccounts().entrySet()){
            allAccounts.add(entry.getValue());
        }

        Collections.sort(allAccounts);

        for(Account account : allAccounts){
            if(account instanceof SavingsAccount){
                serviceAccount.displaySavingsAccount((SavingsAccount) account);
            } else if(account instanceof CheckingAccount){
                serviceAccount.displaySavingsAccount((CheckingAccount) account);
            } else{
                serviceAccount.displaySavingsAccount((CreditAccount) account);
            }
        }

        backLog.writeLog("Displayed richest accounts");
        return allAccounts;
    }
}
