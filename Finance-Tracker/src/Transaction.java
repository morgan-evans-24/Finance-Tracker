public class Transaction {
    private double cost;
    private String name;
    private String description;
    private String date;
    private int reoccurring;
    private String category;
    private boolean isExpense;

    public Transaction(double cost, String name, String description, String date, int reoccurring, String category, boolean isExpense) {
        this.cost = cost;
        this.name = name;
        this.description = description;
        this.date = date;
        this.reoccurring = reoccurring;
        this.category = category;
        this.isExpense = isExpense;
    }

    public double getCost() {
        return cost;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public int getReoccurring() {
        return reoccurring;
    }

    public String getCategory() {
        return category;
    }

    public boolean isExpense() {
        return isExpense;
    }

}
