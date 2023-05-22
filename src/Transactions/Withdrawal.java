package Transactions;

import Account.Account;

import java.time.LocalDate;
import java.util.Date;

public class Withdrawal extends Transaction{
    private Long destinationAccountID;
    private String withdrawalType;
    private String description;

    public Withdrawal(){
        super();

        this.destinationAccountID = -1L;
        this.withdrawalType = "";
        this.description = "";
    }

    public Withdrawal(LocalDate transactionDate, Long accountID, Float amount, String currency, String withdrawalType, String description){
        super(transactionDate, accountID, amount, currency);
        this.destinationAccountID = -1L;
        this.withdrawalType = withdrawalType;
        this.description = description;
    }

    // In case of description not being provided
    public Withdrawal(LocalDate transactionDate, Long accountID, Float amount, String currency, String withdrawalType){
        this(transactionDate, accountID, amount, currency, withdrawalType, "");
        this.destinationAccountID = -1L;
    }

    public Withdrawal(LocalDate transactionDate, Long accountID, Float amount, String currency, Long destinationAccountID, String withdrawalType, String description){
        super(transactionDate, accountID, amount, currency);

        this.withdrawalType = withdrawalType;
        this.destinationAccountID = destinationAccountID;
        this.description = description;
    }

    // Getters
    public Long getDestinationAccountID() { return destinationAccountID; }
    public String getWithdrawalType() { return withdrawalType; }
    public String getDescription() {return description; }

    // Setters
    public void setDestinationAccountID(long destinationAccountID) { this.destinationAccountID = destinationAccountID; }
    public void setWithdrawalType(String withdrawalType) { this.withdrawalType = withdrawalType; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public void validateTransaction(){
        super.validateTransaction();

        if(destinationAccountID != -1){
            Account destinationWithdrawingAccount = bank.checkAccountInBank(this.destinationAccountID);
            if(destinationWithdrawingAccount != null){
                this.setStatus("Confirmed!");
            } else{
                this.setStatus("Denied! Destination account ID is not valid.");
            }
        }
    }
}
