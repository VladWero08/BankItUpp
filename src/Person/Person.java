package Person;

import java.time.LocalDate;

public class Person{
    // Declaration of class attributes
    private static long personObjs = 0;
    private Long ID;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String email;
    private String phoneNumber;
    private Float income;
    private String gender;

    // Constructors
    public Person(){
        personObjs++;
        this.ID = personObjs;
        this.firstName = "";
        this.lastName = "";
        this.birthDate = LocalDate.now();
        this.email = "";
        this.phoneNumber = "";
        this.income = 0F;
        this.gender = "";
    }

    // Constructor only with firstName, lastName, birthDate, email
    public Person(String firstName, String lastName, LocalDate birthDate, String email){
        personObjs++;
        this.ID = personObjs;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.email = email;
    }

    public Person(String firstName, String lastName, LocalDate birthDate, String email, String phoneNumber, Float income, String gender) {
        personObjs++;
        this.ID = personObjs;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.income = income;
        this.gender = gender;
    }

    public Person(Person person) {
        personObjs++;
        this.ID = personObjs;
        this.firstName = person.firstName;
        this.lastName = person.lastName;
        this.birthDate = person.birthDate;
        this.email = person.email;
        this.phoneNumber = person.phoneNumber;
        this.income = person.income;
        this.gender = person.gender;
    }

    // Getters
    public Long getID() { return ID;}
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public LocalDate getBirthDate() { return birthDate; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public Float getIncome() { return income; }
    public String getGender() { return gender;}

    // Setters
    public void setFirstName(String firstName) { this.firstName = firstName;}
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public void setEmail(String email) { this.email = email; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setIncome(Float income) { this.income = income; }
    public void setGender(String gender) { this.gender = gender;}
}
