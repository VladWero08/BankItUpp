package Services;
import Bank.Bank;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import com.opencsv.CSVWriter;

public class BackLog {
    private static BackLog backLogIO = null;
    File backlog;
    CSVWriter backlogWriter;

    public BackLog(){
      this.generateBackLogCSV();
    }

    public static synchronized BackLog getBackLogInstance(){
        if(backLogIO == null)
            backLogIO = new BackLog();

        return backLogIO;
    }
    public void generateBackLogCSV(){
        try{
            this.backlog = new File("./backlog.csv");
            if(backlog.createNewFile()){
                System.out.println("File 'backlog.csv' has been successfully created");
            } else{
                System.out.println("File 'backlog.csv' already exists.");
            }

            this.backlogWriter = new CSVWriter(new FileWriter("./backlog.csv"));
            String first_line[] = {"nume_actiune" , "timestamp"};
            backlogWriter.writeNext(first_line);

        } catch(IOException exception){
            System.out.println("An error has occured while creating backlog.csv");
            exception.printStackTrace();
        }
    }

    public void writeLog(String action){
        try{
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            String data[] = {action, formatter.format(LocalDateTime.now())};
            this.backlogWriter.writeNext(data);
            this.backlogWriter.flush();

        } catch(IOException exception){
            System.out.println("An error has occured when writing to 'backlog.csv");
            exception.printStackTrace();
        }
    }
}
