import com.toedter.calendar.JDateChooser;
import jdk.jfr.Category;

import javax.sql.rowset.spi.TransactionalWriter;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Date;

public class ListedTransaction {
    public double cost;
    boolean isExpense;
    String name;
    String description;
    String date;
    String category;
    ArrayList<TransactionCategory> categories;
    int reoccurring;
    JPanel gridPanel;
    JLabel errorMessage;
    JFrame frame;

    TrackerMain main;

    public ListedTransaction(JPanel panelToAddTo, boolean expense, TrackerMain trackerMain) {
        errorMessage = new JLabel();
        frame = trackerMain.getFrame();
        isExpense = expense;
        main = trackerMain;
        categories = trackerMain.getTransactionCategories();

        String[] categoryNames = {"New Category"};



        JTextField costInput = new JTextField();
        JTextField nameInput = new JTextField();
        JTextArea descriptionInput = new JTextArea();
        JComboBox categoryInput = new JComboBox(categoryNames);

        TextPrompt costPrompt = new TextPrompt("Enter cost", costInput);
        TextPrompt namePrompt = new TextPrompt("Enter name", nameInput);
        TextPrompt descriptionPrompt = new TextPrompt("Enter description", descriptionInput);

        JDateChooser dateChooser = new JDateChooser();

        JCheckBox checkBox = new JCheckBox();
        if (expense) {
            checkBox.setText("Is this expense reoccurring?");
        }
        else {
            checkBox.setText("Is this income reoccurring?");
        }




        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> {
            Date dateFromChooser = dateChooser.getDate();

            try {
                cost = Double.parseDouble(costInput.getText());
            } catch (NumberFormatException e1) {
                errorMessage.setText("Please enter a valid number");
                return;
            }


            name = nameInput.getText();
            category = categoryInput.getSelectedItem().toString();
            description = descriptionInput.getText();
            date = String.format("%1$td/%1$tm/%1$tY", dateFromChooser);

            if (checkBox.isSelected()) {
                reoccurring = 1;
            }
            makeCard();

        });

        gridPanel = new JPanel();
        gridPanel.setPreferredSize(new Dimension(100, 100));
        gridPanel.setBackground(Color.CYAN);
        gridPanel.setLayout(new GridLayout(4, 2));
        gridPanel.add(costInput);
        gridPanel.add(nameInput);

        gridPanel.add(descriptionInput);
        gridPanel.add(dateChooser);

        gridPanel.add(checkBox);
        gridPanel.add(errorMessage);
        gridPanel.add(categoryInput);
        gridPanel.add(addButton);
        panelToAddTo.add(gridPanel, BorderLayout.CENTER);


    }




    private void makeCard() {
        JLabel reoccurringLabel;

        JLabel costLabel;
        JLabel nameLabel = new JLabel(name);
        JLabel descriptionLabel = new JLabel(description);
        JLabel dateLabel = new JLabel(date);

        gridPanel.removeAll();
        gridPanel.setLayout(new GridLayout(4, 2));

        if (reoccurring == 1) {
            reoccurringLabel = new JLabel("reoccurring");
        }
        else {
            reoccurringLabel = new JLabel("One-time");
        }

        costLabel = new JLabel(Double.toString(cost));


        gridPanel.add(costLabel);
        gridPanel.add(nameLabel);
        gridPanel.add(descriptionLabel);
        gridPanel.add(dateLabel);
        gridPanel.add(reoccurringLabel);


        frame.validate();
        frame.repaint();


        gridPanel.revalidate();
        gridPanel.repaint();

        if (isExpense) {
            main.updateTotals(cost, 0);
        }
        else {
            main.updateTotals(0, cost);
        }

        Transaction transaction = new Transaction(cost, name, description, date, reoccurring, category, isExpense);
        main.addTransaction(transaction);


    }
}
