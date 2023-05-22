package Transactions;

import Account.Account;

import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;

public class Loan extends Transaction{
    private Float monthlyPayment;
    private Float totalInterestPaid;
    private Integer duration;           // in months
    private String collateral;


    // Constructors
    public Loan(){
        super();

        this.monthlyPayment = -1F;
        this.totalInterestPaid = 0F;
        this.duration = 0;
        this.collateral = "";
    }

    public Loan(LocalDate transactionDate, long accountID, Float amount, String currency, Integer duration, String collateral){
        super(transactionDate, accountID, amount, currency);

        this.monthlyPayment = this.getAmount() / duration;
        this.totalInterestPaid = 0F;
        this.duration = duration;
        this.collateral = collateral;
    }

    // Getters
    public Float getMonthlyPayment() { return monthlyPayment; }
    public Float getTotalInterestPaid() { return totalInterestPaid; }
    public Integer getDuration() { return duration; }
    public String getCollateral() { return collateral; }

    // Setters
    public void setMonthlyPayment(Float monthlyPayment) { this.monthlyPayment = monthlyPayment; }
    public void setTotalInterestPaid(Float totalInterestPaid) { this.totalInterestPaid = totalInterestPaid; }
    public void setDuration(Integer duration) { this.duration = duration; }
    public void setCollateral(String collateral) { this.collateral = collateral; }

    // Methods
    @Override
    public void validateTransaction(){
        super.validateTransaction();

        // If the transaction was valid until this point, continue on validating
        if(Objects.equals(this.getStatus(), "Confirmed!")){
            if(this.duration > 0){
                this.setStatus("Confirmed!");
            } else{
                this.setStatus("Denied! Duration should be a positive integer.");
            }
        }
    }
}
