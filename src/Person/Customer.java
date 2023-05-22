package Person;

import Account.Account;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Customer extends Person{
    private static long customerObjs = 0L;
    private long customerID;
    private int numAccounts;
    private ArrayList<Account> accounts;

    // Constructors
    public Customer(){
        super();
        customerObjs += 1;
        this.customerID = customerObjs;
        this.numAccounts = 0;
        this.accounts = new ArrayList<Account>();
    }

    public Customer(String firstName, String lastName, LocalDate birthDate, String email){
        super(firstName, lastName, birthDate, email);
        customerObjs += 1;
        this.customerID = customerObjs;
        this.numAccounts = 0;
        this.accounts = new ArrayList<Account>();
    }

    public Customer(String firstName, String lastName, LocalDate birthDate, String email, String phoneNumber, Float income, String gender) {
        super(firstName, lastName, birthDate, email, phoneNumber, income, gender);
        customerObjs += 1;
        this.customerID = customerObjs;

        this.numAccounts = 0;
        this.accounts = new ArrayList<Account>();;
    }

    public Customer(Customer customer){
        super(customer.getFirstName(), customer.getLastName(), customer.getBirthDate(), customer.getEmail(), customer.getPhoneNumber(), customer.getIncome(), customer.getGender());
        customerObjs += 1;
        this.customerID = customerObjs;
        this.numAccounts = customer.numAccounts;
        this.accounts = customer.accounts;
    }

    // Getters
    public static long getCustomerObjs() { return customerObjs; }
    public long getCustomerID() { return customerID; }
    public int getNumAccounts() { return numAccounts;}
    public ArrayList<Account> getAccounts() { return accounts;}

    // Setters
    public void setCustomerID(long customerID) { this.customerID = customerID; }
    public void setAccounts(ArrayList<Account> accounts) { this.accounts = accounts; }

    // Methods
    public void addAccount(Account account){
        this.accounts.add(account);
        this.numAccounts += 1;
    }

}
