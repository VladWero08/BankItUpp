package Account;

import Transactions.Transaction;
import Transactions.Transfer;
import Transactions.Withdrawal;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class SavingsAccount extends Account{
    private static final Float minimumBalance = 100F;
    private Integer maximumWithdrawals;
    private Float withdrawalFee;
    private Float savingsGoal;
    private Float interestRate;
    private LocalDate redeemedMoney;

    // Constructors
    public SavingsAccount(){
        super();

        this.maximumWithdrawals = 0;
        this.withdrawalFee = 0F;
        this.savingsGoal = 0F;
        this.interestRate = 0F;
    }

    public SavingsAccount(long customerID, LocalDate accountCreationDate, LocalDate accountExpireDate, Double accountBalance, Integer maximumWithdrawals, Float withdrawalFee, Float savingsGoal, Float interestRate){
        super(customerID, accountCreationDate, accountExpireDate, accountBalance);

        this.maximumWithdrawals = maximumWithdrawals;
        this.withdrawalFee = withdrawalFee;
        this.savingsGoal = savingsGoal;
        this.interestRate = interestRate;
        this.redeemedMoney = LocalDate.now();
    }

    public SavingsAccount(long customerID, LocalDate accountCreationDate, LocalDate accountExpireDate, Double accountBalance, Float savingsGoal){
        super(customerID, accountCreationDate, accountExpireDate, accountBalance);
        this.savingsGoal = savingsGoal;
        this.redeemedMoney = LocalDate.now();
    }

    // Getters
    public static Float getMinimumBalance() { return minimumBalance; }
    public Integer getMaximumWithdrawals() { return maximumWithdrawals; }
    public Float getWithdrawalFee() { return withdrawalFee; }
    public Float getSavingsGoal() { return savingsGoal; }
    public Float getInterestRate() { return interestRate; }
    public LocalDate getRedeemedMoney() { return redeemedMoney;}

    // Setters
    public void setMaximumWithdrawals(Integer maximumWithdrawals) { this.maximumWithdrawals = maximumWithdrawals; }
    public void setWithdrawalFee(Float withdrawalFee) { this.withdrawalFee = withdrawalFee; }
    public void setSavingsGoal(Float savingsGoal) { this.savingsGoal = savingsGoal; }
    public void setInterestRate(Float interestRate) { this.interestRate = interestRate; }

    // Methods
    protected void processTransactionTransfer(Transaction transaction){
        Transfer transfer = (Transfer) transaction;
        // Check if the account has enough credit to make a transaction
        if(this.getAccountBalance() >= minimumBalance){
            Float transactionValue = transfer.getAmount();

            // Check if the account has enough credit to make THIS transaction
            if(this.getAccountBalance() < transactionValue * (1 + transfer.getFee())){
                transaction.setStatus("Denied! Your savings account does not have enough credit to make this transfer.\n");
            } else{
                Account destinationAccount = bank.checkAccountInBank(transfer.getDestinationAccountID());

                // Make the transfer
                this.setAccountBalance(this.getAccountBalance() - transactionValue * (1 + transfer.getFee()));
                destinationAccount.setAccountBalance(destinationAccount.getAccountBalance() + transactionValue);

                transaction.setStatus("Confirmed!\n");
                bank.addTransaction(transaction);
                bank.addTransactionFees((double) (transactionValue * transfer.getFee()));
                this.transactionHistory.add(transaction);
            }
        } else{
            transaction.setStatus("Denied! Your savings account needs at least 100 RON in order to make transfers/withdrawals.\n");
        }
    }

    @Override
    protected void processTransactionWithdrawal(Transaction transaction){
        Withdrawal withdrawal = (Withdrawal) transaction;

        if(this.maximumWithdrawals != 0){
            Float totalWithdrawalValue = withdrawal.getAmount() * (1 + withdrawalFee);

            // Check if the account has enough credit to make the transaction
            if(this.getAccountBalance() >= totalWithdrawalValue){
                if(withdrawal.getDestinationAccountID() != -1) {
                    Account destinationAccount = bank.checkAccountInBank(withdrawal.getDestinationAccountID());

                    this.setAccountBalance(this.getAccountBalance() - totalWithdrawalValue);
                    destinationAccount.setAccountBalance(destinationAccount.getAccountBalance() + withdrawal.getAmount());
                } else{
                    this.setAccountBalance(this.getAccountBalance() - totalWithdrawalValue);
                }

                transaction.setStatus("Confirmed!\n");
                bank.addTransaction(transaction);
                this.transactionHistory.add(transaction);
            } else{
                transaction.setStatus("Denied! You do not have enough credit to make the withdrawal.\n");
            }
        } else{
            transaction.setStatus("Deniend! You do not have any withdrawals available.\n");
        }
    }

    public String pursueRedeemtionOfMoney(){
        int localDateYear = LocalDate.now().getYear(), localDateDay = LocalDate.now().getDayOfYear();
        int redeemYear = this.redeemedMoney.getYear(), redeemDay = this.redeemedMoney.getDayOfYear();

        // Redemption are available only once in 30 days:
        // -> redeem & local are in the same year, difference bigger than 30 days
        // -> redeem is previous and local is current year
        // -> difference between redeem and local >= 2 years
        if((localDateYear == redeemYear && localDateDay - redeemDay >= 30) ||
                (localDateYear == redeemYear + 1 && 365 - redeemDay + localDateDay >= 30) ||
                (localDateYear - redeemYear >= 2)){
            // Transform LocalDate to Date
            this.redeemedMoney = LocalDate.now();

            // Increase the account balance
            double bonusRedeemedMoney = this.getAccountBalance() * this.interestRate;
            this.setAccountBalance(this.getAccountBalance() + bonusRedeemedMoney);

            // Take the money from the bank
            bank.substractTransactionProvidedByBank(this.getAccountBalance() * this.interestRate);
            return "Done! You have redeemed your bonus of " + bonusRedeemedMoney + " ron.\n";
        } else{
            return "The difference between redemption must be at least 30 days. Your last redemption took place on "
                    + DateTimeFormatter.ofPattern("dd/MM/yyyy").format(this.redeemedMoney) + ".\n";
        }
    }

    public void generateMoreWithdrawals(int numOfWithdrawals){
        this.maximumWithdrawals += numOfWithdrawals;

        if(numOfWithdrawals <= 10){
            this.withdrawalFee += 0.1F;
        } else if(numOfWithdrawals <= 50){
            this.withdrawalFee += 0.2F;
        } else{
            this.withdrawalFee += 0.25F;
        }
    }

    public int calculateYearsTillSavingsGoal(){
        Double accountBalanceCopy = this.getAccountBalance();
        int numOfYears = 0;

        while(accountBalanceCopy < this.savingsGoal){
            accountBalanceCopy *= (1 + this.interestRate);
        }

        return numOfYears;
    }
}
