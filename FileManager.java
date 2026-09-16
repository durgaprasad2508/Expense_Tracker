import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class FileManager {

    String filePath;
    String budgetFilePath;

    public FileManager(String filePath, String budgetFilePath) {
        this.filePath = filePath;
        this.budgetFilePath = budgetFilePath;
    }
    public ArrayList<Expense> loadExpenses() {

        ArrayList<Expense> expenses = new ArrayList<Expense>();
        File file = new File(filePath);

        // If there is no file, return empty list
        if (!file.exists()) {
            return expenses;
        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            String line = reader.readLine();

            while (line != null) {
                if (!line.trim().isEmpty()) {
                    // Convert the line into an Expense object
                    Expense e = Expense.fromCsv(line);
                    expenses.add(e);
                }
                line = reader.readLine();
            }
            reader.close();
        }
        catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        return expenses;
    }

    public void saveExpenses(ArrayList<Expense> expenses) {

        try {
            PrintWriter writer = new PrintWriter(new FileWriter(filePath));
            // Write each expense into the file
            for (int i = 0; i < expenses.size(); i++) {
                writer.println(expenses.get(i).toCsv());
            }
            writer.close();
        }
        catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }

    public void loadBudgets(
            ArrayList<String> budgetCategories,
            ArrayList<Double> budgetAmounts){

        File file = new File(budgetFilePath);
        // Nothing to load if the file doesn't exist
        if (!file.exists()) {
            return;
        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            while (line != null) {
                if (!line.trim().isEmpty()) {
                    // Split category and amount
                    String[] parts = line.split(",",2);
                    budgetCategories.add(parts[0]);
                    budgetAmounts.add(Double.parseDouble(parts[1]));
                }
                line = reader.readLine();
            }
            reader.close();
        }
        catch (IOException e) {
            System.out.println("Error reading budget file: "+ e.getMessage());
        }
    }

    public void saveBudgets(
            ArrayList<String> budgetCategories,
            ArrayList<Double> budgetAmounts){

        try {
            PrintWriter writer = new PrintWriter(new FileWriter(budgetFilePath));
            // Save category and its budget
            for (int i = 0; i < budgetCategories.size(); i++) {
                writer.println( budgetCategories.get(i) + "," + budgetAmounts.get(i));
            }
            writer.close();
        }
        catch (IOException e) {
            System.out.println("Error saving budget file: " + e.getMessage());
        }
    }
}