package Services;

import Account.*;
import Services.ServiceTransaction;
import Bank.Bank;
import ServicesDatabase.*;
import Transactions.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class ServiceAccount {
    private Bank bank = Bank.getBankInstance();
    private Scanner scanner = new Scanner(System.in);
    private Regex regex = new Regex();

    private BackLog backLog = BackLog.getBackLogInstance();
    private ServiceTransaction serviceTransaction = new ServiceTransaction();

    private final CreateDB createAccounts = CreateDB.getCreateSingletonInstance();
    private final ReadDB readAccounts = ReadDB.getReadSingletonInstance();
    private final ModifyDB modifyAccounts = ModifyDB.getModifySingletonInstance();

    public Account createAccount(long customerID) throws SQLException {
        if(bank.getCustomerAccounts().containsKey(customerID)){
            String accountType;
            LocalDate creationDate, expireDate;
            int numOfYears;
            Double accountBalance;

            // TYPE OF ACCOUNT
            System.out.println("You have decided to create an account.");
            System.out.print("What kind of account do you want to create? Savings, checking or credit account? ");
            accountType = scanner.nextLine();
            while(regex.accountTypeRegex(accountType) == null){
                System.out.println("Invalid, try again!");
                System.out.print("What kind of account do you want to create? ");
                accountType = scanner.nextLine();
            }
            accountType = regex.accountTypeRegex(accountType);

            // LIFE OF THE ACCOUNT
            System.out.print("How many years do you want your account to live?( Max.5, then you can extend it): ");
            numOfYears = scanner.nextInt();
            while(numOfYears > 5 || numOfYears < 0){
                System.out.println("Invalid, try again!");
                System.out.print("Number of years: ");
                numOfYears = scanner.nextInt();
            }

            // The creation date will be the current date,
            // The expire date will be exactly over the number of years introduced by the user
            creationDate = LocalDate.now();
            expireDate = creationDate.plusYears(numOfYears);
            accountBalance = 0D;

            if(Objects.equals(accountType, "Savings")){
                return createSavingAccount(customerID, creationDate, expireDate, accountBalance, numOfYears);
            } else if(Objects.equals(accountType, "Checking")){
                return createCheckingAccount(customerID, creationDate, expireDate, accountBalance, numOfYears);
            } else{
                return createCreditAccount(customerID, creationDate, expireDate, accountBalance, numOfYears);
            }
        } else{
            System.out.println("The customer does not exists in our bank!");
        }

        return null;
    }

    public SavingsAccount createSavingAccount(long customerID, LocalDate accountCreationDate, LocalDate accountExpireDate, Double accountBalance, int numOfYears) throws SQLException {
        Float savingGoal, withdrawalFee, interestRate;
        Integer maximumWithdrawals;

        // SAVINGS GOALS
        System.out.println("You chose to create a savings account, congratulations!");
        System.out.print("What is your savings goal?( enter a positive number): ");
        savingGoal = scanner.nextFloat();
        while(savingGoal <= 1000){
            System.out.println("Invalid, try again!");
            System.out.print("Savings goal( at least 1000): ");
            savingGoal = scanner.nextFloat();
        }

        // Calculate the fees and interest rate
        // depending on the user's life of account
        interestRate = 0.017F + (6 - numOfYears) * 0.02F;
        withdrawalFee = 0.65F * interestRate;
        maximumWithdrawals = 10 * numOfYears;

        System.out.println();
        SavingsAccount newSavingAccount = new SavingsAccount(customerID, accountCreationDate, accountExpireDate, accountBalance, maximumWithdrawals, withdrawalFee, savingGoal, interestRate);
        this.addAccountToCustomerAndBank(customerID, newSavingAccount);

        // Add data to the ACCOUNT, ACCOUNT CUSTOMER and SAVINGS ACCOUNT tables
        // inside the database
        createAccounts.createAccount(newSavingAccount);
        createAccounts.createCustomerAccount((int) customerID, newSavingAccount.getAccountID().intValue());
        createAccounts.createSavingsAccount(newSavingAccount);

        backLog.writeLog("Created savings account " + newSavingAccount.getAccountID() + " for customer " + customerID);
        return newSavingAccount;
    }

    public CheckingAccount createCheckingAccount(long customerID, LocalDate accountCreationDate, LocalDate accountExpireDate, Double accountBalance, int numOfYears){
        Boolean bills = false;
        Float overdraftLimit, overdraftFee;
        String inputBills;

        System.out.println("You chose to create a checking account, congratulations!");
        System.out.print("Do you want to pay bills using this account? (Y or N)");
        scanner.nextLine();
        inputBills = scanner.nextLine();
        while(!Objects.equals(inputBills, "Y") && !Objects.equals(inputBills, "N")){
            System.out.println("Invalid, try again!");
            System.out.print("Possibility to pay bills(Y or N): ");
            inputBills = scanner.nextLine();
        }

        if(inputBills.equals("Y"))
            bills = true;

        // Calculate the fees and overdraft limit;
        // depending on the user's life of account
        overdraftLimit = 100F * numOfYears;
        overdraftFee = 2F * (6 - numOfYears);

        System.out.println();
        CheckingAccount newCheckingAccount = new CheckingAccount(customerID, accountCreationDate, accountExpireDate, accountBalance, overdraftLimit, overdraftFee, bills);
        this.addAccountToCustomerAndBank(customerID, newCheckingAccount);

        backLog.writeLog("Created checking account " + newCheckingAccount.getAccountID() + " for customer " + customerID);
        return newCheckingAccount;
    }

    public CreditAccount createCreditAccount(long customerID, LocalDate accountCreationDate, LocalDate accountExpireDate, Double accountBalance, int numOfYears){
        Boolean rewards = false;
        String inputRewards;
        Float creditLimit;

        System.out.println("You chose to create a credit account, congratulations!");
        System.out.print("Do you want to be in the rewards program? You can get money if you pay your monthly loans on time!(Y or N) ");
        scanner.nextLine();
        inputRewards = scanner.nextLine();
        while(!Objects.equals(inputRewards, "Y") && !Objects.equals(inputRewards, "N")){
            System.out.println("Invalid, try again!");
            System.out.print("Possibility to have rewards(Y or N): ");
            inputRewards = scanner.nextLine();
        }

        if(inputRewards.equals("Y")){
            rewards = true;
        }

        // Calculate the credit limit and minimum payment;
        // depending on the user's life of account
        creditLimit = 15000F * numOfYears;

        System.out.println();
        CreditAccount newCreditAccount = new CreditAccount(customerID, accountCreationDate, accountExpireDate, accountBalance, creditLimit, rewards);
        this.addAccountToCustomerAndBank(customerID, newCreditAccount);

        backLog.writeLog("Created credit account " + newCreditAccount.getAccountID() + " for customer " + customerID);
        return newCreditAccount;
    }

    // Function that will add an account to a specific customer in the bank
    // && will add the account to the bank
    private void addAccountToCustomerAndBank(Long customerID, Account account){
        bank.addAccount(account);
        bank.getCustomerAccounts().get(customerID).addAccount(account);
    }

    // Decide what type of account the user has
    public void checkTypeOfAccountToDisplay(Account account) throws SQLException {
        if(account instanceof SavingsAccount){
            this.displaySavingsAccount((SavingsAccount) account);
        } else if(account instanceof CheckingAccount){
            this.displayCheckingAccount((CheckingAccount) account);
        } else{
            this.displayCreditAccount((CreditAccount) account);
        }
    }

    // Functions that will display a specific accounts
    public void displayBasicAccount(Account account, String typeOfAccount){
        System.out.println("*** THIS IS A " + typeOfAccount + " ACCOUNT ***");
        System.out.println("AccountID: " + account.getAccountID());
        System.out.println("Account creation date: " + account.getAccountCreationDate());
        System.out.println("Account expire date: " + account.getAccountExpireDate());
        System.out.println("Balance: " + account.getAccountBalance());
    }

    // SAVINGS ACCOUNT
    public void displaySavingsAccount(Account account) throws SQLException {
        readAccounts.readSingleSavingsAccount(account.getAccountID().intValue());
        backLog.writeLog("Displayed saving account " + account.getAccountID());
    }

    // CHECKING ACCOUNT
    public void displayCheckingAccount(Account account){
        this.displayBasicAccount(account, "CHECKING");

        System.out.println("Overdraft limit: " + ((CheckingAccount) account).getOverdraftLimit());
        System.out.println("Overdraft fee: " + ((CheckingAccount) account).getOverdraftFee());

        if(((CheckingAccount) account).getBillPay()) System.out.println("Account is eligible for bill payments.");
        else System.out.println("Account is not eligible for bill payments.");

        System.out.println("Money spent on check writing: " + ((CheckingAccount) account).getCheckWriting());
        System.out.println();
        backLog.writeLog("Displayed checking account " + account.getAccountID());
    }

    // CREDIT ACCOUNT
    public void displayCreditAccount(Account account){
        this.displayBasicAccount(account, "CREDIT");

        System.out.println("Credit limit: " + ((CreditAccount) account).getCreditLimit());
        System.out.println("Credit current: " + ((CreditAccount) account).getCreditCurrent());

        if(((CreditAccount) account).getRewardsProgram()){
            System.out.println("Account is eligible for rewards.");
            System.out.println("Reward points accumulated: " + ((CreditAccount) account).getRewardsPoints());
        } else{
            System.out.println("Account is not eligible for rewards");
        }
        System.out.println();
        backLog.writeLog("Displayed credit account " + account.getAccountID());
    }

    public void displayUserLoans(Account account){
        if(!(account instanceof CreditAccount))
            return;

        ServiceTransaction serviceTransaction = new ServiceTransaction();
        for(Loan loan : ((CreditAccount) account).getLoans()){
            // Display details about the loan
            serviceTransaction.displayLoan(loan);

            // Firstly, get the corresponding account of the loan from the bank
            // Cast that Account to a Credit account and access its loanPayments
            ArrayList<Integer> monthsPaid = ((CreditAccount) bank.getServiceAccounts().get(loan.getAccountID())).getLoanPayments().get(loan.getTransactionID());
            int monthBetween = Period.between(loan.getTransactionDate(), LocalDate.now()).getMonths() + 1;

            // Display how much of the loan has been paid
            for(int i = 0; i < monthBetween; i++){
                if(monthsPaid.get(i) == 1)
                    System.out.print("(month " + (i + 1) + ", yes) ");
                else
                    System.out.print("(month " + (i + 1) + ", no) ");
            }

            for(int i = monthBetween; i < monthsPaid.size(); i++){
                System.out.print("(month " + (i+1) + ", unavailable) ");
            }
            System.out.println();
            System.out.println();
        }

        backLog.writeLog("Displayed customer's " + account.getAccountID() + " loans in the account " + account.getAccountID());
    }

    public void displayAllTransactions(Account account) throws SQLException {
        ArrayList<Transaction> allTransactions = account.getTransactionHistory();

        for(Transaction transaction: allTransactions){
            int transactionID = transaction.getTransactionID().intValue();
            if(transaction instanceof Deposit){
                readAccounts.readSingleDeposit(transactionID);
            } else if(transaction instanceof Transfer){
                readAccounts.readSingleTransfer(transactionID);
            } else if(transaction instanceof Withdrawal){
                readAccounts.readSingleWithdrawal(transactionID);
            }
        }

        backLog.writeLog("Displayed all transactions for account " + account.getAccountID());
    }

    public void deleteAccount(Account accountToBeDeleted) throws SQLException {
        bank.removeAccount(accountToBeDeleted.getAccountID());

        // Also delete from the database
        if(accountToBeDeleted instanceof SavingsAccount){
            modifyAccounts.deleteTableRow("account", accountToBeDeleted.getAccountID().intValue());
            modifyAccounts.deleteTableRow("savings_account", accountToBeDeleted.getAccountID().intValue());
        }

        backLog.writeLog("Deleted account " + accountToBeDeleted.getAccountID());
    }
}
