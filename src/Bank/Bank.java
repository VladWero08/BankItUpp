package Bank;

import Account.Account;
import Person.Customer;
import Person.Employee;
import Transactions.Transaction;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Bank {
    private static Bank singleBankInstance = null;
    private String name;
    private LocalDate establishmentYear;
    private String[] locations;
    private Double earnings;
    private Map<Long, Customer> customerAccounts;
    private Map<Long, Account> serviceAccounts;
    private Map<Long, Transaction> transactions;
    private Map<Long, Employee> employees;

    private Bank(){
        this.name = "BankItUp";
        this.establishmentYear = LocalDate.of(2023, 1, 5);
        this.earnings = 100000D;

        this.customerAccounts = new HashMap<Long, Customer>();
        this.serviceAccounts = new HashMap<Long, Account>();
        this.transactions = new HashMap<Long, Transaction>();
        this.employees = new HashMap<Long, Employee>();
    }

    public static synchronized Bank getBankInstance(){
        if(singleBankInstance == null)
            singleBankInstance = new Bank();

        return singleBankInstance;
    }

    // Getters
    public String getName() { return this.name; }
    public LocalDate getEstablishmentYear() { return this.establishmentYear; }
    public String[] getLocations() { return this.locations; }
    public Double getEarnings(){ return this.earnings; }

    public Map<Long, Customer> getCustomerAccounts() { return customerAccounts; }
    public Map<Long, Account> getServiceAccounts() { return serviceAccounts; }
    public Map<Long, Transaction> getTransactions() { return transactions; }
    public Map<Long, Employee> getEmployees() { return employees; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setEstablishmentYear(LocalDate establishmentYear) { this.establishmentYear = establishmentYear; }
    public void setLocations(String[] locations) { this.locations = locations; }

    // Methods ---> Customers
    public void addCustomer(Customer customer){ this.customerAccounts.put(customer.getCustomerID(), customer); }
    public void removeCustomer(Long customerID){
        if(this.customerAccounts.containsKey(customerID)){
            Customer deleltedCustomer = this.customerAccounts.remove(customerID);
            removeAccountAssociatedWithCustomer(deleltedCustomer);

            // Tell the garbage collector that this object
            // is no longer needed by assigning it null
            deleltedCustomer = null;
        }
    }

    // Methods ---> Accounts
    public Account checkAccountInBank(Long accountID){
        if(this.serviceAccounts.containsKey(accountID)){
            return this.serviceAccounts.get(accountID);
        } else{
            return null;
        }
    }

    public void addAccount(Account newAccount){ this.serviceAccounts.put(newAccount.getAccountID(), newAccount); }
    public void removeAccount(Long accountID){
        if(this.serviceAccounts.containsKey(accountID)){
            Account deletedAccount = this.serviceAccounts.remove(accountID);
            this.removeTransactionsAssociatedWithAccount(deletedAccount);

            deletedAccount = null;
        }
    }

    public void removeAccountAssociatedWithCustomer(Customer deletedCustomer){
        for(Account account: deletedCustomer.getAccounts()){
            // If the account actually exists
            if(this.serviceAccounts.containsKey(account.getAccountID())){
                Account deletedAccount = this.serviceAccounts.remove(account.getAccountID());
                removeTransactionsAssociatedWithAccount(deletedAccount);

                // Tell the garbage collector that this object
                // is no longer needed by assigning it null
                deletedAccount = null;
            }
        }
    }

    // Methods ---> Transactions
    public void addTransaction(Transaction newTransaction){ this.transactions.put(newTransaction.getTransactionID(), newTransaction); }
    public void addTransactionFees(Double amount){ this.earnings += amount;}
    public void substractTransactionProvidedByBank(Double amount) { this.earnings -= amount;}

    public void removeTransactionsAssociatedWithAccount(Account deletedAccount){
        for(Transaction transaction: deletedAccount.getTransactionHistory()){
            // Verify if the transaction exists inside the bank
            if(this.transactions.containsKey(transaction.getTransactionID())){
                Transaction deletedTransaction = this.transactions.remove(transaction.getTransactionID());
                deletedTransaction = null;
            }
        }
    }
    // Methods ---> Employees
    public void addEmployee(Employee newEmployee){ this.employees.put(newEmployee.getEmployeeID(), newEmployee); }
    public void removeEmployee(Long employeeID){
        if(this.employees.containsKey(employeeID)){
            this.employees.remove(employeeID);
        }
    }
}
