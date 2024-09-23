import java.awt.*;

public class TransactionCategory {
    String category;
    Color color;
    public TransactionCategory(String categoryName, Color categoryColor) {
        category = categoryName;
        color = categoryColor;
    }
    public String getCategory() {
        return category;
    }
    public Color getColor() {
        return color;
    }
    public void setCategory(String categoryName) {
        category = categoryName;
    }
    public void setColor(Color categoryColor) {
        color = categoryColor;
    }
}
