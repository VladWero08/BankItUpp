package Transactions;

import Account.Account;
import Bank.Bank;

import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;

public class Transaction {
    protected Bank bank = Bank.getBankInstance();
    private static Long transactionObjs = 0L;
    private Long transactionID;
    private LocalDate transactionDate;
    private Long accountID;
    private Float amount;
    private String currency;
    private String status;

    // Constructors
    public Transaction(){
        transactionObjs += 1;
        this.transactionID = transactionObjs;

        this.transactionDate = LocalDate.now();
        this.accountID = -1L;
        this.amount = 0F;
        this.currency = "";
    }

    public Transaction(LocalDate transactionDate, Long accountID, Float amount, String currency) {
        transactionObjs += 1;
        this.transactionID = transactionObjs;

        this.transactionDate = transactionDate;
        this.accountID = accountID;
        this.amount = amount;
        this.currency = currency;
    }

    // Getters
    public Long getTransactionID() { return transactionID; }
    public LocalDate getTransactionDate() { return transactionDate; }
    public Long getAccountID() { return accountID; }
    public Float getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getStatus() { return status; }

    // Setters
    public void setTransactionDate(LocalDate transactionDate) { this.transactionDate = transactionDate; }
    public void setAmount(Float amount) { this.amount = amount; }
    public void setCurrency(String currency) { this.currency = currency; }
    public void setStatus(String status) { this.status = status; }

    // Methods
    public void validateTransaction(){
        Account userAccount = bank.checkAccountInBank(this.getAccountID());
        // If the account exists in the bank
        if(userAccount != null){
            // If the account is still available, the expiration date of the account
            // occurs after the transaction
            if(userAccount.getAccountExpireDate().compareTo(this.getTransactionDate()) > 0){
                // If the deposit is bigger than 0
                if(this.getAmount() > 0){
                    this.setStatus("Confirmed!\n");
                } else{
                    this.setStatus("Denied! Amount too small.\n");
                }
            } else{
                this.setStatus("Denied! Your account is not available anymore.\n");
            }
        } else{
            this.setStatus("Denied! There is no account with this ID.\n");
        }
    }
}
