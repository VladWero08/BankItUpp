package Services;

import Bank.Bank;

import ServicesDatabase.CreateDB;
import ServicesDatabase.ReadDB;
import Transactions.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Scanner;

public class ServiceTransaction {
    private Bank bank = Bank.getBankInstance();
    private Scanner scanner = new Scanner(System.in);
    private Regex regex = new Regex();
    private CreateDB create_singleton = CreateDB.getCreateSingletonInstance();

    public Transaction makeTransaction(Long accountID){
        if(bank.checkAccountInBank(accountID) != null){
            String transactionType;
            Float amount;

            // TYPE OF TRANSACTION
            System.out.print("What kind of transaction do you want to make? ");
            transactionType = scanner.nextLine();
            while(regex.transactionTypeRegex(transactionType) == null){
                System.out.println("Invalid, try again!");
                System.out.print("What kind of transaction do you want to create? ");
                transactionType = scanner.nextLine();
            }
            transactionType = regex.transactionTypeRegex(transactionType);

            // AMOUNT
            System.out.print("What amount do you want to transact? ");
            amount = scanner.nextFloat();
            while(amount <= 0){
                System.out.println("Invalid, try again!");
                System.out.print("What amount do you want to transact?( >0) ");
                amount = scanner.nextFloat();
            }

            // Depending on the type of transaction, we forward the parameters
            // to the specific transaction handler
            if(Objects.equals(transactionType, "Deposit")){
                return makeDepositHandler(accountID, amount);
            } else if(Objects.equals(transactionType, "Withdrawal")){
                return makeWithdrawalHandler(accountID, amount);
            } else if(Objects.equals(transactionType, "Loan")){
                return makeLoanHandler(accountID, amount);
            } else{
                return makeTransferHandler(accountID, amount);
            }

        } else{
            System.out.println("The account does not exists in our bank!");
        }
        return null;
    }

    public Deposit makeDepositHandler(Long accountID, Float amount){
        String description, depositMethod;

        System.out.print("How do you want to deposit the amount of money? (cash/check/mobile/ATM): ");
        scanner.nextLine();
        depositMethod = scanner.nextLine();
        while(regex.depositMethodRegex(depositMethod) == null){
            System.out.println("Invalid, try again!");
            System.out.print("Cash/check/mobile/ATM? ");
            depositMethod = scanner.nextLine();
        }
        depositMethod = regex.depositMethodRegex(depositMethod);

        System.out.print("Do you want to write a description about this deposit? (If YES, write the description, if not, write 'N': ");
        description = scanner.nextLine();
        if(Objects.equals(description, "N")) {
            return new Deposit(LocalDate.now(), accountID, amount, "ron", "", depositMethod);
        } else {
            return new Deposit(LocalDate.now(), accountID, amount, "ron", description, depositMethod);
        }
    }

    public Withdrawal makeWithdrawalHandler(Long accountID, Float amount){
        Long destinationAccountID = -1L;
        String withdrawalType, description, transferWithdrawal;

        // First check if the user wants to make a transfer, and if it does
        // ask him about the accountID of the destination account
        System.out.print("Do you want to make a transfer within this withdrawal?( Y or N) ");
        scanner.nextLine();
        transferWithdrawal = scanner.nextLine();
        if(Objects.equals(transferWithdrawal, "Y")){
            System.out.print("Enter the destination's account ID: ");
            destinationAccountID = scanner.nextLong();
            while(bank.checkAccountInBank(destinationAccountID) == null){
                System.out.println("Invalid, try again!");
                System.out.print("Enter the destination's account ID: ");
                destinationAccountID = scanner.nextLong();
            }
        }

        System.out.print("How do you want to withdrawal the amount of money? (ATM/Mobile/Check/Wire): ");
        scanner.nextLine();
        withdrawalType = scanner.nextLine();
        while(regex.withdrawalMethodRegex(withdrawalType) == null){
            System.out.println("Invalid, try again!");
            System.out.print("ATM/Mobile/Check/Wire? ");
            withdrawalType = scanner.nextLine();
        }
        withdrawalType = regex.withdrawalMethodRegex(withdrawalType);

        System.out.println("Do you want to write a description about this withdrawal? (If YES, write the description, if not, write 'N'.");
        System.out.print("Description: ");
        description = scanner.nextLine();

        if(Objects.equals(description, "N")) description = "";

        return new Withdrawal(LocalDate.now(), accountID, amount, "ron", destinationAccountID, withdrawalType, description);
    }

    public Loan makeLoanHandler(Long accountID, Float amount){
        Integer duration;
        String collateral = "";

        System.out.print("How much should the payment of the loan last?( in months, at least 12): ");
        duration = scanner.nextInt();
        while(duration < 12){
            System.out.println("Invalid, try again!");
            System.out.print("Duration of loan( at least 12 months): ");
            duration = scanner.nextInt();
        }

        if(amount >= 75000){
            System.out.println("Because you have asked for a loan bigger than 75.000, we need to ask back for a collateral.");
            System.out.print("What collateral do you want to offer? (car/house/stocks/cryptocurrency): ");
            collateral = scanner.nextLine();
            while(regex.collateralRegex(collateral) == null){
                System.out.println("Invalid, try again!");
                System.out.print("Car/house/stocks/cryptocurrency: ");
                collateral = scanner.nextLine();
            }
        }

        return new Loan(LocalDate.now(), accountID, amount, "ron", duration, collateral);
    }

    public Transfer makeTransferHandler(Long accountID, Float amount){
        Long destinationAccountID;
        Integer frequency = null;
        String schedule, repeatable;
        LocalDate scheduleDate = null, endDate = null;

        System.out.print("What is the destination's account ID? ");
        destinationAccountID = scanner.nextLong();
        while(destinationAccountID < 0){
            System.out.println("Invalid, try again!");
            System.out.print("Destination's account ID( positive number): ");
            destinationAccountID = scanner.nextLong();
        }

        System.out.print("Do you want to schedule this transfer for another date than now?( Y or N): ");
        scanner.nextLine();
        schedule = scanner.nextLine();
        if(!Objects.equals(schedule, "Y")){
            // Continue on asking if the user wants to repeat this transfer.
            System.out.print("Do you want to repeat this transfer over a period of time?( Y or N): ");
            scanner.nextLine();
            repeatable = scanner.nextLine();

            if(Objects.equals(repeatable, "Y")){
                System.out.print("Until which date do you want to repeat the transfer? (dd/MM/yyyy): ");
                repeatable = scanner.nextLine();
                while(!regex.dateRegex(repeatable)){
                    System.out.println("Invalid, try again!");
                    System.out.print("End date(dd/MM/yyyy): ");
                    repeatable = scanner.nextLine();
                }
                endDate = LocalDate.parse(repeatable, DateTimeFormatter.ofPattern("dd/MM/yyyy"));

                System.out.print("How ofter per month do you want to make this transfer? (max 4): ");
                frequency = scanner.nextInt();
                while(frequency < 0 || frequency > 4){
                    System.out.println("Invalid, try again!");
                    System.out.print("Frequency( max 4): ");
                    frequency = scanner.nextInt();
                }

                return new Transfer(LocalDate.now(), accountID, amount, "ron", destinationAccountID, frequency, endDate);
            }
        } else{
            System.out.print("What date should the transfer be scheduled on? (dd/MM/yyyy): ");
            schedule = scanner.nextLine();
            while(!regex.dateScheduleRegex(schedule)){
                System.out.println("Invalid, try again!");
                System.out.print("Schedule date(dd/MM/yyyy): ");
                schedule = scanner.nextLine();
            }
            scheduleDate = LocalDate.parse(schedule, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            return new Transfer(LocalDate.now(), accountID, amount, "ron", destinationAccountID, scheduleDate);
        }

        return new Transfer(LocalDate.now(), accountID, amount, "ron", destinationAccountID);
    }

    // Display transaction
    private void displayTransaction(Transaction transaction, String transactionType){
        System.out.println("*** THIS IS A " + transactionType + " TRANSACTION ***");

        System.out.println("TransactionID: " + transaction.getTransactionID());
        System.out.println("Transaction date: " + transaction.getTransactionDate());
        System.out.println("Amount: " + transaction.getAmount());
        System.out.println("Currency: " + transaction.getCurrency());
    }

    // Display loan function
    public void displayLoan(Loan loan){
        this.displayTransaction(loan, "LOAN");

        System.out.println("Monthly payment: " + loan.getMonthlyPayment());
        System.out.println("Total interest paid: " + loan.getTotalInterestPaid());
        System.out.println("Duration: " + loan.getDuration());

        if(!Objects.equals(loan.getCollateral(), ""))
            System.out.println("Collateral: " + loan.getCollateral());
    }

    public void insertTransactionToDB(Transaction transaction) throws SQLException {
        create_singleton.createTransaction(transaction);

        if (transaction instanceof Deposit) {
            create_singleton.createDeposit((Deposit) transaction);
        } else if (transaction instanceof Withdrawal) {
            create_singleton.createWithdrawal((Withdrawal) transaction);
        } else if (transaction instanceof Transfer) {
            create_singleton.createTransfer((Transfer) transaction);
        }
    }

}
