package Account;

import Bank.Bank;
import Transactions.*;

import java.util.ArrayList;
import java.time.LocalDate;

public class Account implements Comparable<Account>{
    protected Bank bank = Bank.getBankInstance();
    private static long accountObjs = 0;
    private Long accountID;
    private Long customerID;
    private LocalDate accountCreationDate;
    private LocalDate accountExpireDate;
    private Double accountBalance;
    protected ArrayList<Transaction> transactionHistory;

    // Constructors
    public Account(){
        accountObjs += 1;
        this.accountID = accountObjs;
        this.customerID = -1L;
        this.accountCreationDate = LocalDate.now();
        this.accountExpireDate = LocalDate.now();
        this.accountBalance = 0D;
        this.transactionHistory = new ArrayList<>();
    }

    public Account(Long customerID, LocalDate accountCreationDate, LocalDate accountExpireDate, Double accountBalance) {
        accountObjs += 1;
        this.accountID = accountObjs;
        this.customerID = customerID;
        this.accountCreationDate = accountCreationDate;
        this.accountExpireDate = accountExpireDate;
        this.accountBalance = accountBalance;
        this.transactionHistory = new ArrayList<>();
    }

    // Getters
    public Long getAccountID() { return accountID; }
    public Long getCustomerID() { return customerID; }
    public LocalDate getAccountCreationDate() { return accountCreationDate; }
    public LocalDate getAccountExpireDate() { return accountExpireDate; }
    public Double getAccountBalance() { return accountBalance; }
    public ArrayList<Transaction> getTransactionHistory() { return transactionHistory; }

    // Setters
    public void setAccountCreationDate(LocalDate accountCreationDate) { this.accountCreationDate = accountCreationDate; }
    public void setAccountExpireDate(LocalDate accountExpireDate) { this.accountExpireDate = accountExpireDate; }
    public void setAccountBalance(Double accountBalance) { this.accountBalance = accountBalance; }

    // Methods

    protected void processTransactionDeposit(Transaction transaction){
        // Modify the account balance by adding the deposit to the account
        this.setAccountBalance(this.getAccountBalance() + transaction.getAmount());
        transaction.setStatus("Confirmed!\n");

        // Add transaction to bank history & account history
        bank.addTransaction(transaction);
        this.transactionHistory.add(transaction);
    }

    protected void processTransactionWithdrawal(Transaction transaction){
        Withdrawal withdrawal = (Withdrawal) transaction;

        if(this.getAccountBalance() >= withdrawal.getAmount()){
            if(withdrawal.getDestinationAccountID() == -1L) {
                Account destinationAccount = bank.checkAccountInBank(withdrawal.getDestinationAccountID());

                this.setAccountBalance(this.getAccountBalance() - withdrawal.getAmount());
                destinationAccount.setAccountBalance(destinationAccount.getAccountBalance() + withdrawal.getAmount());
            } else{
                this.setAccountBalance(this.getAccountBalance() - withdrawal.getAmount());
            }

            transaction.setStatus("Confirmed!\n");
            bank.addTransaction(transaction);
            this.transactionHistory.add(transaction);
        } else{
            transaction.setStatus("Denied! You do not have enough credit to make the withdrawal.\n");
        }
    }

    public String checkTransactionType(Transaction transaction){
        if(transaction instanceof Deposit){
            return "Deposit";
        } else if(transaction instanceof Loan){
            return "Loan";
        } else if(transaction instanceof Transfer){
            return "Transfer";
        } else {
            return "Withdrawal";
        }
    }

    public void addTransaction(Transaction transaction){
        // Check if the attraction belongs to this object, because this function
        // might not be called from a valid instance( such as Deposit.processTransaction())
        if(transaction.getAccountID() == this.accountID){
            String typeOfTransaction = this.checkTransactionType(transaction);

            switch (typeOfTransaction) {
                case "Loan":
                    if( this instanceof CreditAccount){
                        ((CreditAccount) this).processTransactionLoan(transaction);
                    } else{
                        transaction.setStatus("Denied! Only credit accounts are allowed to make loans.\n");
                    }
                    break;

                case "Transfer":
                    if (this instanceof SavingsAccount) {
                        ((SavingsAccount) this).processTransactionTransfer(transaction);
                    } else if (this instanceof CheckingAccount){
                        ((CheckingAccount) this).processTransactionTransfer(transaction);
                    } else{
                        transaction.setStatus("Denied! Only savings & checking accounts are allowed to make transfers.\n");
                    }
                    break;

                case "Deposit":
                    this.processTransactionDeposit(transaction);
                    break;

                case "Withdrawal":
                    this.processTransactionWithdrawal(transaction);
                    break;
            }
        }
    }

    @Override
    public int compareTo(Account account){
        return this.accountBalance.compareTo(account.accountBalance);
    }
}
