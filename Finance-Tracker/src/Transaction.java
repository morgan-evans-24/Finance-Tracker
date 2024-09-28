public class Transaction {
    private double cost;
    private String name;
    private String description;
    private String date;
    private int reoccurring;
    enum ReoccuringFrequency {
        YEARLY,
        MONTHLY,
        WEEKLY,
        CUSTOM,
        NOT
    }
    private ReoccuringFrequency reoccurringFrequency;
    private int customReoccurringFrequency;
    private String category;
    private boolean isExpense;

    public Transaction(double cost, String name, String description, String date, int reoccurring, String reoccurringFrequency, int customReoccurringFrequency,String category, boolean isExpense) {
        this.cost = cost;
        this.name = name;
        this.description = description;
        this.date = date;
        this.reoccurring = reoccurring;
        this.category = category;
        this.isExpense = isExpense;
        switch (reoccurringFrequency) {
            case "YEARLY":
                this.reoccurringFrequency = ReoccuringFrequency.YEARLY;
                break;
            case "MONTHLY":
                this.reoccurringFrequency = ReoccuringFrequency.MONTHLY;
                break;
            case "WEEKLY":
                this.reoccurringFrequency = ReoccuringFrequency.WEEKLY;
                break;
            case "CUSTOM":
                this.reoccurringFrequency = ReoccuringFrequency.CUSTOM;
                this.customReoccurringFrequency = customReoccurringFrequency;
                break;
            default:
                this.reoccurringFrequency = ReoccuringFrequency.NOT;
                break;
        }
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

    public ReoccuringFrequency getReoccurringFrequency() {
        if (reoccurring == 1) {
            return reoccurringFrequency;
        }
        else {
            return ReoccuringFrequency.NOT;
        }
    }

    public int getCustomReoccurringFrequency() {
        if (reoccurringFrequency == ReoccuringFrequency.CUSTOM) {
            return customReoccurringFrequency;
        }
        else {
            return 0;
        }
    }

}
