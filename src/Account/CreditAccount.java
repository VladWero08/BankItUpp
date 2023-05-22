package Account;

import Transactions.Loan;
import Transactions.Transaction;

import java.time.LocalDate;
import java.time.Period;

import java.util.*;

public class CreditAccount extends Account{
    private Float creditLimit;
    private Float creditCurrent;
    private Boolean rewardsProgram;
    private Integer rewardsPoints;
    private static final Float interestRate = 0.09F;

    private ArrayList<Loan> loans;
    private Map<Long, ArrayList<Integer>> loanPayments;

    // Constructors
    public CreditAccount(){
        super();

        this.creditLimit = 0F;
        this.creditCurrent = 0F;
        this.rewardsProgram = false;
        this.rewardsPoints = 0;
        this.loans = new ArrayList<>();
        this.loanPayments = new HashMap<Long, ArrayList<Integer>>();
    }

    public CreditAccount(long customerID, LocalDate accountCreationDate, LocalDate accountExpireDate, Double accountBalance, Float creditLimit, Boolean rewardsProgram){
        super(customerID, accountCreationDate, accountExpireDate, accountBalance);

        this.creditLimit = creditLimit;
        this.creditCurrent = 0F;
        this.rewardsProgram = rewardsProgram;
        this.rewardsPoints = 0;
        this.loans = new ArrayList<>();
        this.loanPayments = new HashMap<Long, ArrayList<Integer>>();
    }

    // Getters
    public Float getCreditLimit(){ return creditLimit;}
    public Float getCreditCurrent(){ return creditCurrent;}
    public Boolean getRewardsProgram() { return rewardsProgram; }
    public Integer getRewardsPoints() { return rewardsPoints; }
    public ArrayList<Loan> getLoans() { return loans;}
    public Map<Long, ArrayList<Integer>> getLoanPayments(){ return loanPayments; }

    // Setters
    public void setCreditLimit(Float creditLimit) { this.creditLimit = creditLimit; }
    public void setRewardsProgram(Boolean rewardsProgram) { this.rewardsProgram = rewardsProgram; }
    public void setRewardsPoints(Integer rewardsPoints) { this.rewardsPoints = rewardsPoints; }

    // Methods
    protected void processTransactionLoan(Transaction transaction){
        Loan loan = (Loan) transaction;

        if(this.creditCurrent + transaction.getAmount() <= creditLimit){
            transaction.setStatus("Confirmed!\n");

            this.creditCurrent += transaction.getAmount();
            this.setAccountBalance(this.getAccountBalance() + loan.getAmount());
            bank.addTransaction(transaction);
            bank.substractTransactionProvidedByBank(loan.getAmount() * 1D);

            // Add transaction to transactionHistory and to the loan ArrayLists
            this.transactionHistory.add(transaction);
            this.loans.add(loan);

            ArrayList<Integer> newLoan = new ArrayList<>(Collections.nCopies(loan.getDuration(), 0));
            this.loanPayments.put(loan.getTransactionID(), newLoan);
        } else{
            transaction.setStatus("Denied! You can have this loan because it will go beyond the credit limit!\n");
        }
    }

    public String payLoan(Long loanID, int monthsToBePaid){
        boolean found = false;

        if(!this.loanPayments.containsKey(loanID))
            return "Denied! The ID of the loan is invalid.\n";

        // Loop through each loan and find the one with the same ID
        // as the loanID parameter
        for(int i = 0; i < loans.size(); i++){
            if(Objects.equals(this.loans.get(i).getTransactionID(), loanID)){
                // We found the loan
                found = true;

                // Get the transaction ID & date, respectively the current date
                // and the monthly payment for this loan
                LocalDate currentDate = LocalDate.now(), transactionDate = this.loans.get(i).getTransactionDate();
                int monthBetween = Period.between(transactionDate, currentDate).getMonths() + 1;
                Float monthlyPayment = this.loans.get(i).getMonthlyPayment();

                int lastUnpaidMonth = 0, monthsPaidNow = 0;
                // Find the last unpaid month, by looping through the ArrayList of the current loan
                for(int j = 0; j < this.loanPayments.get(loanID).size(); j++) {
                    if (this.loanPayments.get(loanID).get(j) == 0) break;
                    else lastUnpaidMonth += 1;
                }

                // While the loan does now exceed the current time month
                // and the numbers of months the user wants to pay
                // and the balance in his account is able to pay that amount, pay it
                while(lastUnpaidMonth < monthBetween && monthsPaidNow < monthsToBePaid
                        && this.getAccountBalance() - monthlyPayment * (1 + interestRate) >= 0){

                    this.loanPayments.get(loanID).set(lastUnpaidMonth, 1);
                    this.setAccountBalance(this.getAccountBalance() - monthlyPayment * (1 + interestRate));
                    this.loans.get(i).setTotalInterestPaid(this.loans.get(i).getTotalInterestPaid() + monthlyPayment * (1 + interestRate));
                    bank.addTransactionFees(monthlyPayment * interestRate * 1D);

                    lastUnpaidMonth += 1;
                    System.out.println(lastUnpaidMonth);
                    monthsPaidNow += 1;
                }

                // If there was only one payment, that means that the user has paid it
                // the loan for the current month in time
                if(monthsPaidNow == 1){
                    if(this.rewardsProgram) this.rewardsPoints += 1;
                    return "Confirmed! You have paid this loan until the current date.\n";
                }

                if(lastUnpaidMonth < monthBetween){
                    return "Confirmed! You paid this loan until the " + lastUnpaidMonth + "th month. You do not have enough credit to pay more.\n";
                }
            }
        }

        if(found)
            return "Confirmed! Loan has been paid till the current date.\n";
        return "Denied! You do not have enough credit to pay this loan.\n";
    }
}
