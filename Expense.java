import java.time.LocalDate;

public class Expense {

    int id;
    double amount;
    String category;
    String description;
    LocalDate date;

    public Expense(int id, double amount, String category, String description, LocalDate date) {

        this.id = id;
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDate() {
        return date;
    }

    // Convert expense into CSV format
    public String toCsv() {
        return id + "," + amount + "," + category + "," + description + "," + date;
    }

    // Convert CSV line back into Expense object
    public static Expense fromCsv(String line) {

        String[] parts = line.split(",", 5);

        int id = Integer.parseInt(parts[0]);
        double amount = Double.parseDouble(parts[1]);
        String category = parts[2];
        String description = parts[3];
        LocalDate date = LocalDate.parse(parts[4]);
        return new Expense(id, amount, category, description, date);
    }

    @Override
    public String toString() {
        return "#" + id + "  $" + amount + "  "
                + category + "  " + description + "  " + date;
    }
}
