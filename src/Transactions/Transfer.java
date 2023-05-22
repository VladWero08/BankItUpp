package Transactions;

import Account.Account;
import java.time.LocalDate;
import java.util.Objects;


public class Transfer extends Transaction{
    private Long destinationAccountID;
    private final Float fee = 0.03F;
    private LocalDate scheduled_date;
    private Integer frequency;
    private LocalDate end_date;

    // Constructors
    public Transfer(){
        super();

        this.destinationAccountID = -1L;
        this.frequency = -1;
        this.end_date = null;
        this.scheduled_date = null;
    }

    public Transfer(LocalDate transactionDate, Long accountID, Float amount, String currency, long destinationAccountID){
        super(transactionDate, accountID, amount, currency);

        this.destinationAccountID = destinationAccountID;
        this.scheduled_date = null;
        this.frequency = null;
        this.end_date = null;
    }

    public Transfer(LocalDate transactionDate, Long accountID, Float amount, String currency, Long destinationAccountID, LocalDate scheduled_date){
        super(transactionDate, accountID, amount, currency);

        this.destinationAccountID = destinationAccountID;
        this.scheduled_date = scheduled_date;
        this.frequency = null;
        this.end_date = null;
    }

    public Transfer(LocalDate transactionDate, Long accountID, Float amount, String currency, Long destinationAccountID, Integer frequency, LocalDate end_date){
        super(transactionDate, accountID, amount, currency);

        this.destinationAccountID = destinationAccountID;
        this.scheduled_date = null;
        this.frequency = frequency;
        this.end_date = end_date;
    }

    // Getters
    public Long getDestinationAccountID() { return destinationAccountID; }
    public Float getFee() { return fee; }
    public LocalDate getScheduled_date() { return scheduled_date; }
    public Integer getFrequency() { return frequency; }
    public LocalDate getEnd_date() { return end_date; }

    // Setters
    public void setDestinationAccountID(long destinationAccountID) { this.destinationAccountID = destinationAccountID; }
    public void setScheduled_date(LocalDate scheduled_date) { this.scheduled_date = scheduled_date; }
    public void setFrequency(Integer frequency) { this.frequency = frequency; }
    public void setEnd_date(LocalDate end_date) { this.end_date = end_date; }

    // Methods
    @Override
    public void validateTransaction(){
        super.validateTransaction();

        Account destinationAccount = bank.checkAccountInBank(this.destinationAccountID);

        // Verify if the destination account exists
        if(Objects.equals(this.getStatus(), "Confirmed!")) {
            if(destinationAccount != null && this.destinationAccountID != this.getAccountID()){
                // Compare current date with potential end/scheduled date
                int localDateYear = LocalDate.now().getYear(), localDateMonth = LocalDate.now().getMonthValue();
                int scheduledDateYear = this.scheduled_date.getYear(), scheduledDateMonth = this.scheduled_date.getMonthValue();
                int endDateYear = this.end_date.getYear(), endDateMonth = this.end_date.getMonthValue();

                // Verify if the scheduled date for the transfer is valid
                if(this.scheduled_date != null && localDateYear == scheduledDateYear && localDateMonth < scheduledDateMonth){
                    this.setStatus("Confirmed!");
                } else{
                    this.setStatus("Denied! Scheduled date of the transaction is not valid.");
                }

                // Verify if end date of the repeating transfer and frequenct are valid
                if(this.end_date != null && localDateYear == endDateYear && localDateMonth < endDateMonth){
                    if(frequency > 0 && frequency <= 4){
                        this.setStatus("Confirmed!");
                    } else{
                        this.setStatus("Denied! Frequency of the transfer/month can be between [1,4] times.");
                    }
                } else{
                    this.setStatus("Denied! End date of the transaction is not valid.");
                }
            } else{
                this.setStatus("Denied! Destination account is invalid.");
            }
        }
    }
}
