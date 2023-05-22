import Services.*;
import java.text.ParseException;
import java.sql.*;
import ServicesDatabase.*;
import Services.BackLog;

public class Main {
    public static void main(String[] args) throws ParseException, SQLException {
        CreateTables init = new CreateTables();
        DropTables destroy = new DropTables();
        destroy.dropAllTables();
        init.createAllTables();

        ServiceBank bankItUp = new ServiceBank();

        bankItUp.displayWelcomeText();
        bankItUp.createCustomerLoop();
    }
}
