import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public class ExpenseTracker {

    ArrayList<Expense> expenses;
    FileManager fileManager;
    int nextId;

    ArrayList<String> budgetCategories;
    ArrayList<Double> budgetAmounts;

    public ExpenseTracker(String filePath, String budgetFilePath) {
        fileManager = new FileManager(filePath, budgetFilePath);
        // Load old expenses
        expenses = fileManager.loadExpenses();

        budgetCategories = new ArrayList<String>();
        budgetAmounts = new ArrayList<Double>();

        // Load saved budgets
        fileManager.loadBudgets(budgetCategories, budgetAmounts);
        // Find the next id
        int maxId = 0;

        for (int i = 0; i < expenses.size(); i++) {
            if (expenses.get(i).getId() > maxId) {
                maxId = expenses.get(i).getId();
            }
        }
        nextId = maxId+1;
    }

    public static void main(String[] args) {
        ExpenseTracker tracker = new ExpenseTracker("expenses.csv", "budgets.csv");
        tracker.run();
    }

public void run() {
    Scanner scanner = new Scanner(System.in);
    boolean running = true;
    boolean showMenu = false;

    while (running) {

        if (!showMenu) {
            System.out.println("\n=== Expense Tracker ===");
            System.out.println("\n1. Show Menu");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");

            String firstChoice = scanner.nextLine();

            if (firstChoice.equals("1")) {
                showMenu = true;
            }
            else if (firstChoice.equals("0")) {
                running = false;
                System.out.println("Goodbye");
            }
            else {
                System.out.println("Invalid option,try again.");
            }
        }
        else {
            printMenu();
            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                addExpense(scanner);
                showMenu = false;
            }
            else if (choice.equals("2")) {
                viewAllExpenses();
                showMenu = false;
            }
            else if (choice.equals("3")) {
                viewByCategory(scanner);
                showMenu = false;
            }
            else if (choice.equals("4")) {
                showTotal();
                showMenu = false;
            }
            else if (choice.equals("5")) {
                showMonthlySummary(scanner);
                showMenu = false;
            }
            else if (choice.equals("6")) {
                editExpense(scanner);
                showMenu = false;
            }
            else if (choice.equals("7")) {
                setBudget(scanner);
                showMenu = false;
            }
            else if (choice.equals("0")) {
                showMenu = false;
            }
            else {
                System.out.println("Invalid option,try again.");
            }
        }
    }
    scanner.close();
}

    public void printMenu() {

        System.out.println("\n=== Choose an Action ===");
        System.out.println("\n1. Add expense");
        System.out.println("2. View all expenses");
        System.out.println("3. View expenses by category");
        System.out.println("4. Show total spend");
        System.out.println("5. Show monthly summary");
        System.out.println("6. Edit an expense");
        System.out.println("7. Set budget for a category");
        System.out.println("0. Exit");
        System.out.print("Choose an option: ");
    }

    public void addExpense(Scanner scanner) {

        try {
            System.out.print("Amount: ");
            double amount = Double.parseDouble(scanner.nextLine());

            System.out.print("Category (e.g. Food, Travel, Bills): ");
            String category = scanner.nextLine();

            System.out.print("Description: ");
            String description = scanner.nextLine();

            System.out.print("Date (YYYY-MM-DD, leave blank for today): ");
            String dateInput = scanner.nextLine();

            LocalDate date;
            // If no date is given, use today's date
            if (dateInput.isEmpty()) {
                date = LocalDate.now();
            }
            else {
                date = LocalDate.parse(dateInput);
            }
            Expense expense = new Expense(nextId, amount, category, description, date);
            nextId++;
            expenses.add(expense);
            // Save the new expense
            fileManager.saveExpenses(expenses);
            System.out.println("Expense added successfully.");
            checkBudget(category);
        }
        catch (Exception e) {
            System.out.println("Invalid input, expense not added.");
        }
    }

    public void checkBudget(String category) {
        int index = budgetCategories.indexOf(category);
        // No budget for this category
        if (index == -1) {
            return;
        }
        double limit = budgetAmounts.get(index);
        double spent = 0;

        // Add all expenses of this category
        for (int i = 0; i < expenses.size(); i++) {
            if (expenses.get(i).getCategory().equalsIgnoreCase(category)) {
                spent = spent + expenses.get(i).getAmount();
            }
        }
        if (spent > limit) {
            System.out.println("WARNING:- You have crossed your budget for " + category + "! Spent $" + spent + " out of $" + limit);
        }
    }

    public void viewAllExpenses() {

        if (expenses.isEmpty()) {
            System.out.println("No expenses recorded yet.");
            return;
        }
        System.out.println("\n--- All Expenses ---");
        for (int i = 0; i < expenses.size(); i++) {
            System.out.println((i + 1) + ". " + expenses.get(i));
        }
    }

    public void viewByCategory(Scanner scanner) {

        System.out.print("Enter category: ");
        String category = scanner.nextLine();

        boolean found = false;

        System.out.println("\n--- Expenses in " + category + " ---");
        for (int i = 0; i < expenses.size(); i++) {
            Expense e = expenses.get(i);
            if (e.getCategory().equalsIgnoreCase(category)) {
                System.out.println(e);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No expenses found for category: " + category);
        }
    }

    public void showTotal() {
        double total = 0;

        // Add all expense amounts
        for (int i = 0; i < expenses.size(); i++) {
            total = total + expenses.get(i).getAmount();
        }
        System.out.println("Total spend: $ " + total);
    }

    public void showMonthlySummary(Scanner scanner) {
        System.out.print("Enter month (YYYY-MM,leave blank for current month): ");
        String input = scanner.nextLine();
        String targetMonth;

        if (input.isEmpty()) {
            LocalDate today = LocalDate.now();
            // Get only year and month
            targetMonth = today.toString().substring(0, 7);
        }
        else {
            targetMonth = input;
        }
        double total = 0;
        // Check expenses for the selected month
        for (int i = 0; i < expenses.size(); i++) {
            Expense e = expenses.get(i);
            String expenseMonth = e.getDate().toString().substring(0, 7);
            if (expenseMonth.equals(targetMonth)) {
                total = total + e.getAmount();
            }
        }
        System.out.println("Total spend for " + targetMonth + ": $" + total);
    }

    public void editExpense(Scanner scanner) {
        System.out.print("Enter expense ID to edit: ");

        try {
            int id = Integer.parseInt(scanner.nextLine());
            int index = -1;
            // Find the expense
            for (int i = 0; i < expenses.size(); i++) {
                if (expenses.get(i).getId() == id) {
                    index = i;
                    break;
                }
            }
            if (index == -1) {
                System.out.println("No expense found with that ID.");
                return;
            }
            Expense old = expenses.get(index);
            System.out.println("Editing: " + old);
            System.out.println("Leave a field blank to keep the current value");
            System.out.print("New amount [" + old.getAmount() + "]: ");
            String amountInput = scanner.nextLine();
            
            double amount;
            
            if (amountInput.isEmpty()) {
                amount = old.getAmount();
            }
            else {
                amount = Double.parseDouble(amountInput);
            }
            System.out.print("New category [" + old.getCategory() + "]: ");

            String categoryInput = scanner.nextLine();
            String category;

            if (categoryInput.isEmpty()) {
                category = old.getCategory();
            }
            else {
                category = categoryInput;
            }
            System.out.print(
                    "New description [" + old.getDescription() + "]: ");

            String descInput = scanner.nextLine();
            String description;

            if (descInput.isEmpty()) {
                description = old.getDescription();
            }
            else {
                description = descInput;
            }
            Expense updated = new Expense( old.getId(), amount, category, description, old.getDate());
            expenses.set(index, updated);
            fileManager.saveExpenses(expenses);
            System.out.println("Expense updated: " + updated);
        }
        catch (NumberFormatException e) {
            System.out.println("Invalid input, edit cancelled.");
        }
    }

    public void setBudget(Scanner scanner) {

        System.out.print("Enter category: ");

        String category = scanner.nextLine();

        System.out.print("Enter budget limit for " + category + ": ");

        try {
            double limit = Double.parseDouble(scanner.nextLine());
            int index = budgetCategories.indexOf(category);
            if (index == -1) {
                // Add new budget
                budgetCategories.add(category);
                budgetAmounts.add(limit);
            }
            else {
                // Change existing budget
                budgetAmounts.set(index, limit);
            }
            fileManager.saveBudgets(budgetCategories,budgetAmounts);
                    
            System.out.println("Budget set: " + category + " -> $" + limit);
        }
        catch (NumberFormatException e) {
            System.out.println("Invalid amount, budget not set.");
        }
    }
}
