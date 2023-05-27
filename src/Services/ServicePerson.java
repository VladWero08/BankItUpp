package Services;

import Bank.Bank;
import Person.*;
import Account.*;
import Services.ServiceAccount;
import Services.BackLog;
import ServicesDatabase.*;

import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Scanner;

public class ServicePerson {
    private final Bank bank = Bank.getBankInstance();
    private final Scanner scanner = new Scanner(System.in);
    private final Regex regex = new Regex();
    private BackLog backLog = BackLog.getBackLogInstance();

    // Singletons to interact with the database
    private final CreateDB createPersons = CreateDB.getCreateSingletonInstance();
    private final ReadDB readPersons = ReadDB.getReadSingletonInstance();
    private final ModifyDB modifyPersons = ModifyDB.getModifySingletonInstance();

    // CREATE operations
    public Customer createCustomer() throws ParseException, SQLException {
        String firstName, lastName, email, phoneNumber, gender, birthDate;
        Float income;

        // FIRST NAME
        System.out.print("First name: ");
        firstName = scanner.nextLine();
        while(!regex.nameRegex(firstName)){
            System.out.println("Invalid, try again!");
            System.out.print("First name( between 5 and 29 characters, only letters): ");
            firstName = scanner.nextLine();
        }

        // LAST NAME
        System.out.print("Last name: ");
        lastName = scanner.nextLine();
        while(!regex.nameRegex(lastName)){
            System.out.println("Invalid, try again!");
            System.out.print("Last name( between 5 and 29 characters, only letters): ");
            lastName = scanner.nextLine();
        }

        // BIRTH DATE
        System.out.print("Birth date(dd/MM/yyyy): ");
        birthDate = scanner.nextLine();
        while(!regex.dateRegex(birthDate)){
            System.out.println("Invalid, try again!");
            System.out.print("Birth date(dd/MM/yyyy): ");
            birthDate = scanner.nextLine();
        }
        LocalDate birthDateTransformed = LocalDate.parse(birthDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        // EMAIL
        System.out.print("Email: ");
        email = scanner.nextLine();
        while(!regex.emailRegex(email)){
            System.out.println("Invalid, try again!");
            System.out.print("Email: ");
            email = scanner.nextLine();
        }

        // PHONE NUMBER
        System.out.print("Phone number: ");
        phoneNumber = scanner.nextLine();
        while(!regex.phoneRegex(phoneNumber)){
            System.out.println("Invalid, try again!");
            System.out.print("Phone number: ");
            phoneNumber = scanner.nextLine();
        }

        // GENDER
        System.out.print("Gender( optional): ");
        gender = scanner.nextLine();
        while(!regex.genderRegex(gender)){
            System.out.println("Invalid, try again!");
            System.out.print("Gender( optional): ");
            gender = scanner.nextLine();
        }

        // INCOME
        System.out.print("Income: ");
        income = scanner.nextFloat();
        while(income < 0){
            System.out.println("Invalid, try again!");
            System.out.print("Income: ");
            income = scanner.nextFloat();
        }

        System.out.println();
        Customer newCustomer = new Customer(firstName, lastName, birthDateTransformed, email, phoneNumber, income, gender);
        bank.addCustomer(newCustomer);

        // Also add to the database
        createPersons.createPerson(newCustomer);
        createPersons.createCustomer(newCustomer);
        backLog.writeLog("Created customer " + newCustomer.getCustomerID());
        return newCustomer;
    }

    // READ operations
    public void displayAllCustomers() throws SQLException {
        backLog.writeLog("Displayed all customers");
        readPersons.readAllCustomers();
    }

    public void displaySingleCustomer(int customerID) throws SQLException{
        backLog.writeLog("Displayed customer " + customerID);
        readPersons.readSingleCustomer(customerID);
    }

    // UPDATE operations
    public void updateSingleCustomer(Long customerID) throws SQLException{
        System.out.println("EDIT CUSTOMER -- OPTIONS");
        System.out.println("1 --> First name");
        System.out.println("2 --> Last name");
        System.out.println("3 --> Email");
        System.out.println("4 --> Phone number");
        System.out.println("5 --> Income");
        System.out.println("6 --> Gender");

        String firstName, lastName, email, phoneNumber, gender, editChoice = "";
        Float income;

        while(!Objects.equals(editChoice, "exit")){
            editChoice = scanner.nextLine();

            switch (editChoice){
                case "1":
                    System.out.print("First name: "); firstName = scanner.nextLine();
                    while(!regex.nameRegex(firstName)){
                        System.out.println("Invalid, try again!");
                        System.out.print("First name( between 5 and 29 characters, only letters): ");
                        firstName = scanner.nextLine();
                    }

                    bank.getCustomerAccounts().get(customerID).setFirstName(firstName);
                    modifyPersons.updateTableRow("person", customerID.intValue(), "firstName", "string", firstName);
                    backLog.writeLog("Updated customer's " + customerID + " first name");

                    editChoice = "exit";
                    break;

                case "2":
                    System.out.print("Last name: "); lastName = scanner.nextLine();
                    while(!regex.nameRegex(lastName)){
                        System.out.println("Invalid, try again!");
                        System.out.print("First name( between 5 and 29 characters, only letters): ");
                        lastName = scanner.nextLine();
                    }

                    bank.getCustomerAccounts().get(customerID).setLastName(lastName);
                    modifyPersons.updateTableRow("person", customerID.intValue(), "lastName", "string", lastName);
                    backLog.writeLog("Updated customer's " + customerID + " last name");

                    editChoice = "exit";
                    break;

                case "3":
                    System.out.print("Email: "); email = scanner.nextLine();
                    while(!regex.emailRegex(email)){
                        System.out.println("Invalid, try again!");
                        System.out.print("Email: ");
                        email = scanner.nextLine();
                    }

                    bank.getCustomerAccounts().get(customerID).setEmail(email);
                    modifyPersons.updateTableRow("person", customerID.intValue(), "email", "string", email);
                    backLog.writeLog("Updated customer's " + customerID + " email");

                    editChoice = "exit";
                    break;

                case "4":
                    System.out.print("Phone number: "); phoneNumber = scanner.nextLine();
                    while(!regex.phoneRegex(phoneNumber)){
                        System.out.println("Invalid, try again!");
                        System.out.print("Phone number: ");
                        phoneNumber = scanner.nextLine();
                    }

                    bank.getCustomerAccounts().get(customerID).setPhoneNumber(phoneNumber);
                    modifyPersons.updateTableRow("person", customerID.intValue(), "phoneNumber", "string", phoneNumber);
                    backLog.writeLog("Updated customer's " + customerID + " phone number");

                    editChoice = "exit";
                    break;

                case "5":
                    System.out.print("Income: "); income = scanner.nextFloat();
                    while(income < 0){
                        System.out.println("Invalid, try again!");
                        System.out.print("Income: ");
                        income = scanner.nextFloat();
                    }
                    bank.getCustomerAccounts().get(customerID).setIncome(income);
                    modifyPersons.updateTableRow("person", customerID.intValue(), "income", "float", income.toString());
                    backLog.writeLog("Updated customer's " + customerID + " income");

                    editChoice = "exit";
                    break;

                case "6":
                    System.out.print("Gender( optional): "); gender = scanner.nextLine();
                    while(!regex.genderRegex(gender)){
                        System.out.println("Invalid, try again!");
                        System.out.print("Gender( optional): ");
                        gender = scanner.nextLine();
                    }

                    bank.getCustomerAccounts().get(customerID).setGender(gender);
                    modifyPersons.updateTableRow("person", customerID.intValue(), "gender", "string", gender);
                    backLog.writeLog("Updated customer's " + customerID + " gender");

                    editChoice = "exit";
                    break;

                case "exit":
                    break;
            }
        }
    }

    // DELETE operations
    public void deleteSingleCustomer(Long customerID) throws SQLException{
        // Firstly remove from the bank object
        bank.removeCustomer(customerID);
        // Remove from the database
        modifyPersons.deleteTableRow("person", customerID.intValue());
        backLog.writeLog("Deleted customer " + customerID);
    }
}
