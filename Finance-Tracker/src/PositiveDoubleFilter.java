import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class PositiveDoubleFilter extends DocumentFilter {

    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
        StringBuilder sb = new StringBuilder(fb.getDocument().getText(0, fb.getDocument().getLength()));
        sb.insert(offset, string);

        if (isValidPositiveDouble(sb.toString())) {
            super.insertString(fb, offset, string, attr);
        } // else do nothing (invalid input)
    }
    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
        StringBuilder sb = new StringBuilder(fb.getDocument().getText(0, fb.getDocument().getLength()));
        sb.replace(offset, offset + length, text);

        if (isValidPositiveDouble(sb.toString())) {
            super.replace(fb, offset, length, text, attrs);
        } // else do nothing (invalid input)
    }

    @Override
    public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
        StringBuilder sb = new StringBuilder(fb.getDocument().getText(0, fb.getDocument().getLength()));
        sb.delete(offset, offset + length);

        if (isValidPositiveDouble(sb.toString())) {
            super.remove(fb, offset, length);
        }
    }




    // Method to check if input is a valid positive double value
    private boolean isValidPositiveDouble(String text) {
        try {
            if (text.isEmpty()) {
                return true; // Allow empty input
            }
            double value = Double.parseDouble(text);
            return value >= 0; // Only allow positive numbers
        } catch (NumberFormatException e) {
            return false; // Not a valid number
        }
    }
}

