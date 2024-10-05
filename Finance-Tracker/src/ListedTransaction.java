import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.text.AbstractDocument;
import java.awt.*;
import java.awt.event.ItemEvent;
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

    ArrayList<Transaction> transactions;


    int reoccurring;
    String reoccurringString;
    int reoccurringXlyNumber;

    JPanel cardPanel;
    ArrayList<JPanel> expenseCardPanels = new ArrayList<>();
    ArrayList<JPanel> incomeCardPanels = new ArrayList<>();

    JPanel fillerPanel;

    JPanel buttonPanel;
    JButton deleteButton;
    JButton editButton;

    JLabel errorMessage;
    JFrame frame;

    TrackerMain main;

    public ListedTransaction(JPanel panelToAddTo, boolean expense, TrackerMain trackerMain, ArrayList<Transaction> transactions) {
        errorMessage = new JLabel();
        frame = trackerMain.getFrame();
        isExpense = expense;
        main = trackerMain;
        categories = trackerMain.getTransactionCategories();
        reoccurring = -1;
        reoccurringString = "";
        reoccurringXlyNumber = 0;
        this.transactions = transactions;
        this.main = trackerMain;

        int noOfTransactions;

        ArrayList<String> categoryNames = new ArrayList<>();


        categoryNames.add("");
        categoryNames.add("<New Category>");
        for (TransactionCategory c : categories) {
            categoryNames.add(c.getCategory());
        }




        JTextField costInput = new JTextField();
        ((AbstractDocument) costInput.getDocument()).setDocumentFilter(new PositiveDoubleFilter());
        JTextField nameInput = new JTextField();
        JTextArea descriptionInput = new JTextArea();
        JComboBox categoryInput = new JComboBox(categoryNames.toArray());



        TextPrompt costPrompt = new TextPrompt("Enter cost", costInput);
        TextPrompt namePrompt = new TextPrompt("Enter name", nameInput);
        TextPrompt descriptionPrompt = new TextPrompt("Enter description", descriptionInput);

        JDateChooser dateChooser = new JDateChooser();

        JColorChooser colorChooser = new JColorChooser();
        AbstractColorChooserPanel[] panels = colorChooser.getChooserPanels();

        for (AbstractColorChooserPanel panel : panels) {
            if (!panel.getDisplayName().equals("HSV")) {
                colorChooser.removeChooserPanel(panel);
            }
        }

        JCheckBox checkBox = new JCheckBox();
        JRadioButton reoccurringWeekly = new JRadioButton("Weekly");
        JRadioButton reoccurringMonthly = new JRadioButton("Monthly");
        JRadioButton reoccurringYearly = new JRadioButton("Yearly");
        JRadioButton reoccurringXly = new JRadioButton("Custom");

        JTextField reoccurringXlyInput = new JTextField();
        ((AbstractDocument) reoccurringXlyInput.getDocument()).setDocumentFilter(new PositiveIntFilter());
        TextPrompt reoccurringXlyPrompt = new TextPrompt("Happens every 'x' days (Enter x)", reoccurringXlyInput);
        reoccurringXlyPrompt.setPreferredSize(new Dimension(100,50));
        ButtonGroup reoccurringGroup = new ButtonGroup();
        JPanel reoccurringPanel = new JPanel();

        reoccurringXlyInput.setVisible(false);

        reoccurringXly.addItemListener(e -> reoccurringXlyInput.setVisible(e.getStateChange() == ItemEvent.SELECTED));

        reoccurringGroup.add(reoccurringWeekly);
        reoccurringGroup.add(reoccurringMonthly);
        reoccurringGroup.add(reoccurringYearly);
        reoccurringGroup.add(reoccurringXly);

        reoccurringPanel.setLayout(new GridLayout(5, 1));

        reoccurringPanel.add(reoccurringWeekly);
        reoccurringPanel.add(reoccurringMonthly);
        reoccurringPanel.add(reoccurringYearly);
        reoccurringPanel.add(reoccurringXly);
        reoccurringPanel.add(reoccurringXlyInput);

        checkBox.setText("Reoccurring?");

        checkBox.addActionListener(e -> {
            if (checkBox.isSelected()) {
                reoccurring = 1;
                JOptionPane.showConfirmDialog(null, reoccurringPanel,
                        "How often does this transaction reoccur?", JOptionPane.OK_CANCEL_OPTION);
                if (reoccurringYearly.isSelected()) {
                    reoccurringString = "YEARLY";
                }
                if (reoccurringMonthly.isSelected()) {
                    reoccurringString = "MONTHLY";
                }
                if (reoccurringWeekly.isSelected()) {
                    reoccurringString = "WEEKLY";
                }
                if (reoccurringXly.isSelected()) {
                    reoccurringString = "CUSTOM";
                }
                System.out.println(reoccurringString);

                if (reoccurringString.equals("CUSTOM")) {
                    reoccurringXlyNumber = Integer.parseInt(reoccurringXlyInput.getText());
                }
                System.out.println(reoccurringXlyNumber);
            }
            else {
                reoccurring = 0;
            }
        });


        categoryInput.addActionListener(e -> {
            if (categoryInput.getSelectedItem() == "<New Category>") {
                String newCategory = JOptionPane.showInputDialog("Enter new category name");
                JOptionPane.showConfirmDialog(null, colorChooser,
                        "Choose a colour for the category", JOptionPane.OK_CANCEL_OPTION);
                if (newCategory != null) {
                    categoryInput.addItem(newCategory);
                    categoryInput.setSelectedItem(newCategory);
                    main.addTransactionCategory(newCategory, colorChooser.getColor());
                }
            }
        });


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
            makeCard(panelToAddTo);

        });

        if (isExpense) {
            noOfTransactions = main.getNoOfExpenses();
        }
        else {
            noOfTransactions = main.getNoOfIncomes();
        }

        editCardPanel(expense, costInput, nameInput, descriptionInput, dateChooser, checkBox, categoryInput, addButton);

        GridBagConstraints mainConstraints = new GridBagConstraints();
        try {
            panelToAddTo.remove(noOfTransactions);
        } catch (Exception ignored) {

        }
        mainConstraints.anchor = GridBagConstraints.NORTH;
        mainConstraints.insets = (new Insets(0, 0, 0, 0));
        mainConstraints.ipady = 0;


        mainConstraints.weightx = 1.0;



        mainConstraints.gridx = 0;
        mainConstraints.gridy = noOfTransactions;
        mainConstraints.fill = GridBagConstraints.HORIZONTAL;
        panelToAddTo.add(cardPanel, mainConstraints);

        mainConstraints.gridy = noOfTransactions + 1;
        mainConstraints.weighty = 1.0;




        fillerPanel = new JPanel();
        fillerPanel.setBackground(Color.BLACK);
        panelToAddTo.add(fillerPanel, mainConstraints);
    }

    private void editCardPanel(boolean expense, JTextField costInput, JTextField nameInput, JTextArea descriptionInput, JDateChooser dateChooser, JCheckBox checkBox, JComboBox categoryInput, JButton addButton) {
        cardPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        constraints.insets = (new Insets(5, 0, 5, 0));
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        constraints.gridwidth = 1;
        cardPanel.setBackground(Color.CYAN);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.ipadx = 100; //Pads out the text fields

        cardPanel.add(costInput, constraints);

        constraints.gridx = 2;
        cardPanel.add(nameInput, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        cardPanel.add(descriptionInput, constraints);

        constraints.ipadx = 0; //Stops the other inputs expanding massively
        constraints.gridx = 2;
        cardPanel.add(dateChooser, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.weightx = 0.0;
        constraints.weighty = 0.0;

        cardPanel.add(checkBox, constraints);

        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.gridx = 2;
        cardPanel.add(errorMessage, constraints);

        constraints.gridx = 0;
        constraints.gridy = 3;
        if (expense) {
            cardPanel.add(categoryInput, constraints);
        }


        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 1;
        constraints.gridy = 4;
        cardPanel.add(addButton, constraints);


        cardPanel.setBorder(new LineBorder(Color.black));
    }

    public ListedTransaction(JPanel panelToAddTo, TrackerMain main, JFrame frame, ArrayList<Transaction> transactions) {

        System.out.println("how many of these?");
        System.out.println(transactions.size());

        int noOfTransactions;
        this.transactions = transactions;
        this.main = main;
        this.frame = frame;

        if (transactions.isEmpty()) {
            return;
        }
        for (int i = 0; i < transactions.size(); i++) {
            Transaction t = transactions.get(i);
            if (t.isExpense()) {
                noOfTransactions = main.getNoOfExpenses();
            }
            else {
                noOfTransactions = main.getNoOfIncomes();
            }
            System.out.println(noOfTransactions + "Debug" + t.isExpense());

            cardPanel = new JPanel(new GridBagLayout());

            GridBagConstraints mainConstraints = new GridBagConstraints();
            try {
                panelToAddTo.remove(i);
            } catch (Exception ignored) {

            }
            mainConstraints.anchor = GridBagConstraints.NORTH;
            mainConstraints.insets = (new Insets(0, 0, 0, 0));
            mainConstraints.ipady = 0;


            mainConstraints.weightx = 1.0;


            mainConstraints.gridx = 0;
            mainConstraints.gridy = i;
            mainConstraints.fill = GridBagConstraints.HORIZONTAL;
            panelToAddTo.add(cardPanel, mainConstraints);

            mainConstraints.gridy = i + 1;
            mainConstraints.weighty = 1.0;




            fillerPanel = new JPanel();

            fillerPanel.setBackground(Color.BLACK);

            panelToAddTo.add(fillerPanel, mainConstraints);

            cost = t.getCost();
            name = t.getName();
            description = t.getDescription();
            date = t.getDate();
            reoccurring = t.getReoccurring();
            reoccurringString = t.getReoccurringFrequency().name();
            reoccurringXlyNumber = t.getCustomReoccurringFrequency();
            category = t.getCategory();
            isExpense = t.isExpense();
            makeCardWithoutAdding(main, frame, panelToAddTo);
        }





    }

    private void makeCard(JPanel panelToAddTo) {

        make(main, frame, panelToAddTo);

        if (isExpense) {
            main.updateTotals(cost, 0);
        }
        else {
            main.updateTotals(0, cost);
        }

        Transaction transaction = new Transaction(cost, name, description, date, reoccurring, reoccurringString, reoccurringXlyNumber, category, isExpense);

        main.addTransaction(transaction);


    }



    private void makeCardWithoutAdding(TrackerMain main, JFrame frame, JPanel panelToAddTo) {

        make(main, frame, panelToAddTo);

        if (isExpense) {
            main.updateTotals(cost, 0);
//            main.raiseNoExpenses();
        }
        else {
            main.updateTotals(0, cost);
//            main.raiseNoIncomes();
        }

    }

    private void make(TrackerMain main, JFrame frame, JPanel panelToAddTo) {
        JLabel reoccurringLabel;

        JLabel costLabel;
        JLabel nameLabel;
        JLabel descriptionLabel;
        JLabel dateLabel;
        int transactionNumber;
        if (isExpense) {
            transactionNumber = expenseCardPanels.size();
        }
        else {
            transactionNumber = incomeCardPanels.size();
        }

        deleteButton = new JButton("Delete");

        int finalTransactionNumber = transactionNumber;
        deleteButton.addActionListener(e -> {
            System.out.println(finalTransactionNumber + ", " + isExpense);
            main.removeTransactionFromJson(finalTransactionNumber, isExpense);
            resetTransactions(panelToAddTo);

        });
        editButton = new JButton("Edit");
        editButton.addActionListener(e -> {
            if (isExpense) {
                System.out.println(main.getExpenses().get(finalTransactionNumber).getName() + " ooga booga");

//                editCardPanel(true, );
                //THIS WORKS, Implement rest of it lol
            }
        });

        buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(deleteButton);
        buttonPanel.add(editButton);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = (new Insets(5, 0, 5, 0));
        constraints.fill = GridBagConstraints.VERTICAL;
        constraints.anchor = GridBagConstraints.CENTER;


        constraints.weightx = 1.0;
        constraints.weighty = 1.0;


        cardPanel.removeAll();
        cardPanel.setLayout(new GridBagLayout());

        costLabel = new JLabel("<html><b>Cost</b>: £" + cost + "</html>");
        nameLabel = new JLabel("<html><b>Name</b>: " + name + "</html>");
        String descriptionString = "<b>Description</b>: " + description;
        descriptionString = wrapText(descriptionString, 40);
        descriptionLabel = new JLabel(descriptionString);
        dateLabel = new JLabel("<html><b>Date</b>: " + date + "</html>");
        if (reoccurring == 1) {
            if (!reoccurringString.equals("CUSTOM")) {
                reoccurringLabel = new JLabel("<html><b>Transaction Type</b>: " + reoccurringString + "</html>");
            }
            else {
                reoccurringLabel = new JLabel("Repeats every " + reoccurringXlyNumber + " days");
            }

        }
        else {
            reoccurringLabel = new JLabel("<html><b>Not Reoccurring</b></html>");
        }


        constraints.gridx = 0;
        constraints.gridy = 0;
        cardPanel.add(costLabel, constraints);
        constraints.gridx = 1;
        constraints.gridy = 0;
        cardPanel.add(nameLabel, constraints);
        constraints.gridx = 0;
        constraints.gridy = 1;
        cardPanel.add(descriptionLabel, constraints);
        constraints.gridx = 1;
        constraints.gridy = 1;
        cardPanel.add(dateLabel, constraints);
        constraints.gridx = 0;
        constraints.gridy = 2;
        cardPanel.add(reoccurringLabel, constraints);
        constraints.gridx = 1;
        constraints.gridy = 2;
        cardPanel.add(buttonPanel, constraints);
        cardPanel.setBorder(new LineBorder(Color.black));

        if (isExpense) {
            expenseCardPanels.add(cardPanel);
        }
        else {
            incomeCardPanels.add(cardPanel);
        }


        frame.validate();
        frame.repaint();


        cardPanel.revalidate();
        cardPanel.repaint();
    }

    public String wrapText(String text, int wordLength) {
        StringBuilder result = new StringBuilder("<html>");
        int count = 0;

        for (String word : text.split(" ")) {
            if (count + word.length() > wordLength) {
                result.append("<br>");
                count = 0;
            }
            result.append(word).append(" ");
            count += word.length() + 1;
        }

        result.append("</html>");
        return result.toString();

    }

    public void resetTransactions(JPanel panelToAddTo) {
        System.out.println("Resetting transactions");
        panelToAddTo.removeAll();
        new ListedTransaction(panelToAddTo, main, frame, transactions);
    }

}
