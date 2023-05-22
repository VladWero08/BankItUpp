package Person;

import Account.Account;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class Employee extends Person{
    // Declaration of class attributes
    private static Long employeeObjs = 0L;
    private Long employeeID;
    private String jobTitle;
    private LocalDate hireDate;
    private Float salary;
    private Float performanceRating;
    private Float salesTarget;
    private Float salesPerformance;

    // Constructors
    public Employee(){
        super();

        employeeObjs += 1;
        this.employeeID = employeeObjs;

        this.jobTitle = "";
        this.hireDate = LocalDate.now();
        this.salary = 0F;
        this.performanceRating = 0F;
        this.salesTarget = 0F;
        this.salesPerformance = 0F;
    }

    public Employee(String firstName, String lastName, LocalDate birthDate, String email, String jobTitle, LocalDate hireDate, Float salary){
        super(firstName, lastName, birthDate, email);

        employeeObjs += 1;
        this.employeeID = employeeObjs;

        this.jobTitle = jobTitle;
        this.hireDate = hireDate;
        this.salary = salary;
    }

    public Employee(String firstName, String lastName, LocalDate birthDate, String email, String phoneNumber, Float income, String gender, String jobTitle, LocalDate hireDate, Float salary, Float performanceRating, Float salesTarget, Float salesPerformance) {
        super(firstName, lastName, birthDate, email, phoneNumber, income, gender);

        employeeObjs += 1;
        this.employeeID = employeeObjs;

        this.jobTitle = jobTitle;
        this.hireDate = hireDate;
        this.salary = salary;
        this.performanceRating = performanceRating;
        this.salesTarget = salesTarget;
        this.salesPerformance = salesPerformance;
    }

    public Employee(Employee employee){
        super(employee.getFirstName(), employee.getLastName(), employee.getBirthDate(), employee.getEmail(), employee.getPhoneNumber(), employee.getIncome(), employee.getGender());

        employeeObjs += 1;
        this.employeeID = employeeObjs;

        this.jobTitle = employee.jobTitle;
        this.hireDate = employee.hireDate;
        this.salary = employee.salary;
        this.performanceRating = employee.performanceRating;
        this.salesTarget = employee.salesTarget;
        this.salesPerformance = employee.salesPerformance;
    }

    // Getters
    public static long getEmployeeObjs() { return employeeObjs;}
    public long getEmployeeID() { return employeeID;}
    public String getJobTitle() { return jobTitle;}
    public LocalDate getHireDate() { return hireDate;}
    public Float getSalary() { return salary;}
    public Float getPerformanceRating() { return performanceRating;}
    public Float getSalesTarget() { return salesTarget;}
    public Float getSalesPerformance() { return salesPerformance;}

    // Setters
    public void setEmployeeID(long employeeID) { this.employeeID = employeeID; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }
    public void setSalary(Float salary) { this.salary = salary; }
    public void setPerformanceRating(Float performanceRating) { this.performanceRating = performanceRating; }
    public void setSalesTarget(Float salesTarget) { this.salesTarget = salesTarget; }
    public void setSalesPerformance(Float salesPerformance) { this.salesPerformance = salesPerformance; }
}
