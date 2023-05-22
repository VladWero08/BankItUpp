package Services;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

public class Regex {
    private final String[] validMailProviders = {"yahoo", "gmail", "outlook", "aol", "icloud", "zoho", "gmx", "yandex", "mail"};
    private final String[] validMailTLD = {"com", "org", "net", "edu", "gov", "mil", "cn", "in", "br", "ng", "bd", "ru", "jp", "mx", "ph",
            "vn", "et", "eg", "cd", "ir", "tr", "de", "th", "fr", "gb", "it", "mm", "kr", "za", "co", "es", "ua", "tz", "ke", "ar",
            "dz", "pl", "ca", "ug", "ma", "iq", "uz", "pe", "nl", "kp", "my", "au", "cm", "np", "ci", "mg", "ne", "ro", "kh", "lk",
            "mw", "sy", "mg", "gt", "ec", "ml", "zw", "td", "sn", "zm", "sd", "tn", "be", "gr", "pt", "ht", "bj", "cz", "hn", "sv",
            "kr", "rs", "kh", "so", "bf", "ch", "tg", "il"};

    public Boolean nameRegex(String name){ return Pattern.matches("^[A-Za-z]\\w{5,29}$", name); }
    public Boolean phoneRegex(String phone){
        return Pattern.matches("[0-9]{10}", phone);
    }
    public Boolean genderRegex(String gender){
        return Pattern.matches("[Mm]a+[nN]+|[Ww][oO]+m[eE]+[nN]+|^$", gender);
    }

    public Boolean emailRegex(String email){
        String emailPattern = "(\\w+)@[a-zA-Z]+.[a-zA-Z]+";

        if(Pattern.matches(emailPattern, email)){
            Boolean checkTLD = false, checkProvider = false;
            String TLD = email.split("\\.")[1];
            String provider = email.split("\\@")[1].split("\\.")[0];
            // Search for the provider in the array of providers
            for(String str : validMailProviders){
                if(str.equals(provider)){
                    checkProvider = true;
                    break;
                }
            }
            // Search for a valid TLD domain that matches the TLD
            for(String str : validMailTLD){
                if(str.equals(TLD)){
                    checkTLD = true;
                    break;
                }
            }
            return checkTLD && checkProvider;
        }
        return false;
    }

    public Boolean dateRegex(String date){
        // Build a formatter for the date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        try {
            LocalDate.parse(date, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public Boolean dateScheduleRegex(String date){
        // Build a formatter for the date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try {
            LocalDate dateTransformed = LocalDate.parse(date, formatter);
            if(dateTransformed.isBefore(LocalDate.now())){
                return false;
            }

            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public String accountTypeRegex(String account){
        if(Pattern.matches("[Ss]a+vi+ngs", account))
            return "Savings";
        if(Pattern.matches("[Cc]he+cki+ng", account))
            return "Checking";
        if(Pattern.matches("[Cc]re+di+t", account))
            return "Credit";
        return null;
    }

    public String transactionTypeRegex(String transaction){
        if(Pattern.matches("[Dd]e+po+si+t", transaction))
            return "Deposit";
        if(Pattern.matches("[Ww]i+thdra+wa+l", transaction))
            return "Withdrawal";
        if(Pattern.matches("[Ll]o+a+n", transaction))
            return "Loan";
        if(Pattern.matches("[Tt]ra+nsfe+r", transaction))
            return "Transfer";
        return null;
    }

    public String depositMethodRegex(String depositMethods){
        if(Pattern.matches("[Cc]a+sh", depositMethods))
            return "Cash";
        if(Pattern.matches("[Cc]he+ck", depositMethods))
            return "Check";
        if(Pattern.matches("[Mm]o+bi+le+", depositMethods))
            return "Mobile";
        if(Pattern.matches("A+TM", depositMethods))
            return "ATM";
        return null;
    }

    public String withdrawalMethodRegex(String withdrawalMethod){
        if(Pattern.matches("[Ww]i+re+", withdrawalMethod))
            return "Wire";
        if(Pattern.matches("[Cc]he+ck", withdrawalMethod))
            return "Check";
        if(Pattern.matches("[Mm]o+bi+le+", withdrawalMethod))
            return "Mobile";
        if(Pattern.matches("A+TM", withdrawalMethod))
            return "ATM";
        return null;
    }

    public String collateralRegex(String collateral){
        if(Pattern.matches("[Cc]a+r", collateral))
            return "Car";
        if(Pattern.matches("[Hh]o+u+se+", collateral))
            return "House";
        if(Pattern.matches("[Ss]to+cks*", collateral))
            return "Stocks";
        if(Pattern.matches("[Cc]rypto+|[Cc]rypto+curre+ncy+", collateral))
            return "Cryptocurrency";
        return null;
    }
}

