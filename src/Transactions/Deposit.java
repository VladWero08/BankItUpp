package Transactions;

import java.time.LocalDate;
import java.util.Date;

public class Deposit extends  Transaction{
    private String description;
    private String depositMethod;

    // Constructors
    public Deposit(){
        super();

        this.description = "";
        this.depositMethod = "";
    }

    public Deposit(LocalDate transactionDate, long accountID, Float amount, String currency, String description, String depositMethod){
        super(transactionDate, accountID, amount, currency);
        this.description = description;
        this.depositMethod = depositMethod;
    }

    // Getters
    public String getDescription() { return description; }
    public String getDepositMethod() { return depositMethod; }

    // Setters
    public void setDescription(String description) { this.description = description; }
    public void setDepositMethod(String depositMethod) { this.depositMethod = depositMethod; }
}

