package Account;

import Transactions.Transaction;
import Transactions.Transfer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

class Invoice{
    public String type;
    public Float value;

    public Invoice(){
        this.type = "";
        this.value = 0F;
    }

    public Invoice(String type, Float value){
        this.type = type;
        this.value = value;
    }
}

public class CheckingAccount extends Account{
    private Float overdraftLimit;
    private Float overdraftFee;
    private Boolean billPay;
    private ArrayList<Invoice> billPayments;
    private Float checkWriting;

    // Constructors
    public CheckingAccount(){
        super();

        this.overdraftLimit = 0F;
        this.overdraftFee = 0F;
        this.billPay = false;
        this.checkWriting = 0F;
    }

    public CheckingAccount(long customerID, LocalDate accountCreationDate, LocalDate accountExpireDate, Double accountBalance, Float overdraftLimit, Float overdraftFee, Boolean billPay){
        super(customerID, accountCreationDate, accountExpireDate, accountBalance);

        this.overdraftLimit = overdraftLimit;
        this.overdraftFee = overdraftFee;
        this.billPay = billPay;
    }

    // Getters
    public Float getOverdraftLimit() { return overdraftLimit; }
    public Float getOverdraftFee() { return overdraftFee; }
    public Boolean getBillPay() { return billPay; }
    public ArrayList<Invoice> getBillPayments(){ return billPayments; }
    public Float getCheckWriting() { return checkWriting; }

    // Setters
    public void setOverdraftLimit(Float overdraftLimit) { this.overdraftLimit = overdraftLimit; }
    public void setOverdraftFee(Float overdraftFee) { this.overdraftFee = overdraftFee;}
    public void setBillPay(Boolean billPay) {this.billPay = billPay; }
    public void setCheckWriting(Float checkWriting) { this.checkWriting = checkWriting; }

    // Methods
    protected void processTransactionTransfer(Transaction transaction){
        Transfer transfer = (Transfer) transaction;

        if(this.overdraftLimit < this.getAccountBalance() - transfer.getAmount() * (1 + transfer.getFee()) - this.overdraftFee){
            // The transfer will keep the account on a positive balance
            Float totalTransferPayment = transfer.getAmount() * (1 + transfer.getFee());
            if(this.getAccountBalance() - totalTransferPayment >= 0){
                this.setAccountBalance(this.getAccountBalance() - totalTransferPayment);
                bank.addTransactionFees(transfer.getAmount() * transfer.getFee() * 1D);
            }
            // Transfer will make the account overdraft, so the overdraftFee penalty will be applied
            else{
                this.setAccountBalance(this.getAccountBalance() - totalTransferPayment - this.overdraftFee);
                bank.addTransactionFees(transfer.getAmount() * transfer.getFee() * 1D + this.overdraftFee);
            }

            bank.addTransaction(transaction);
            this.transactionHistory.add(transaction);
        } else{
            transaction.setStatus("Denied! Your checking account does not have enough credit to make this transfer.\n");
        }
    }

    public String payBill(String billName, Float billValue){
        if(this.getAccountBalance() >= billValue){
            Invoice newInvoice = new Invoice();
            newInvoice.type = billName;
            newInvoice.value = billValue;

            this.billPayments.add(newInvoice);
            this.setAccountBalance(this.getAccountBalance() - billValue);
        } else{
            return "Denied! Your checking account does not have enough credit to pay this bill.\n";
        }
        return "Confirmed! Your bill has been payed\n";
    }
}
