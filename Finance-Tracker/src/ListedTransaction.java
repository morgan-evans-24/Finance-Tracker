import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.text.AbstractDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Date;

@SuppressWarnings("ALL")
public class ListedTransaction {
    public double cost;
    boolean isExpense;
    String name;
    String description;
    String date;
    String category;
    ArrayList<TransactionCategory> categories;
    int reoccurring;
    String reoccurringString;
    int reoccurringXlyNumber;
    JPanel gridPanel;
    JPanel fillerPanel;
    JLabel errorMessage;
    JFrame frame;

    TrackerMain main;

    public ListedTransaction(JPanel panelToAddTo, boolean expense, TrackerMain trackerMain) {
        errorMessage = new JLabel();
        frame = trackerMain.getFrame();
        isExpense = expense;
        main = trackerMain;
        categories = trackerMain.getTransactionCategories();
        reoccurring = -1;
        reoccurringString = "";
        reoccurringXlyNumber = 0;

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

        reoccurringXly.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    reoccurringXlyInput.setVisible(true);
                }
                else {
                    reoccurringXlyInput.setVisible(false);
                }
            }
        });

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

        checkBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (checkBox.isSelected()) {
                    reoccurring = 1;
                    int result = JOptionPane.showConfirmDialog(null, reoccurringPanel,
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

                    if (reoccurringString.equals("Custom")) {
                        reoccurringXlyNumber = Integer.parseInt(reoccurringXlyInput.getText());
                    }
                    System.out.println(reoccurringXlyNumber);
                }
                else {
                    reoccurring = 0;
                }
            }
        });


        categoryInput.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (categoryInput.getSelectedItem() == "<New Category>") {
                    String newCategory = JOptionPane.showInputDialog("Enter new category name");
                    int result = JOptionPane.showConfirmDialog(null, colorChooser,
                            "Choose a colour for the category", JOptionPane.OK_CANCEL_OPTION);
                    if (newCategory != null) {
                        categoryInput.addItem(newCategory);
                        categoryInput.setSelectedItem(newCategory);
                        main.addTransactionCategory(newCategory, colorChooser.getColor());
                    }
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
            makeCard(cost, name, description, date, reoccurring, reoccurringString, reoccurringXlyNumber, category,
                    isExpense, gridPanel, frame, main);

        });

        gridPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        constraints.insets = (new Insets(5, 0, 5, 0));
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        constraints.gridwidth = 1;
        gridPanel.setBackground(Color.CYAN);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.ipadx = 100; //Pads out the text fields

        gridPanel.add(costInput, constraints);

        constraints.gridx = 2;
        gridPanel.add(nameInput, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        gridPanel.add(descriptionInput, constraints);

        constraints.ipadx = 0; //Stops the other inputs expanding massively
        constraints.gridx = 2;
        gridPanel.add(dateChooser, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.weightx = 0.0;
        constraints.weighty = 0.0;

        gridPanel.add(checkBox, constraints);

        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.gridx = 2;
        gridPanel.add(errorMessage, constraints);

        constraints.gridx = 0;
        constraints.gridy = 3;
        if (expense) {
            gridPanel.add(categoryInput, constraints);
        }



        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 1;
        constraints.gridy = 4;
        gridPanel.add(addButton, constraints);


        gridPanel.setBackground(Color.red);
        gridPanel.setBorder(new LineBorder(Color.black));
        GridBagConstraints mainConstraints = new GridBagConstraints();
        try {
            panelToAddTo.remove(main.getNoOfTransactions());
        } catch (Exception e) {

        }
        mainConstraints.anchor = GridBagConstraints.NORTH;
        mainConstraints.insets = (new Insets(0, 0, 0, 0));
        mainConstraints.ipady = 0;


        mainConstraints.weightx = 1.0;


        mainConstraints.gridx = 0;
        mainConstraints.gridy = main.getNoOfTransactions();
        mainConstraints.fill = GridBagConstraints.HORIZONTAL;
        panelToAddTo.add(gridPanel, mainConstraints);

        mainConstraints.gridy = main.getNoOfTransactions() + 1;
        mainConstraints.weighty = 1.0;




        fillerPanel = new JPanel();
        fillerPanel.setBackground(Color.BLACK);
        panelToAddTo.add(fillerPanel, mainConstraints);
        System.out.println(panelToAddTo.getComponentZOrder(fillerPanel)); //Rearrange this so I can refill transactions pages on startup







    }

    public void makeCards(ArrayList<Transaction> expenses, ArrayList<Transaction> incomes, JPanel gridPanel, JFrame frame, TrackerMain main) {
        for (Transaction e : expenses) {
            makeCard(e.getCost(), e.getName(), e.getDescription(), e.getDate(), e.getReoccurring(),
                    e.getReoccurringFrequency().name(), e.getCustomReoccurringFrequency(), e.getCategory(), e.isExpense(), gridPanel, frame, main);
        }

    }


    private void makeCard(double cost, String  name, String description, String date, int reoccurring, String
            reoccurringString, int reoccurringXlyNumber, String category,boolean isExpense, JPanel gridPanel, JFrame frame, TrackerMain main) {

        JLabel reoccurringLabel;

        JLabel costLabel;
        JLabel nameLabel = new JLabel(name);
        JLabel descriptionLabel = new JLabel(description);
        JLabel dateLabel = new JLabel(date);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = (new Insets(0, 0, 0, 0));
        constraints.ipady = 0;
        constraints.gridy = main.getNoOfTransactions() - 1;
        constraints.weightx = 1.0;


        gridPanel.removeAll();
        gridPanel.setLayout(new GridLayout(4, 2));

        if (reoccurring == 1) {
            reoccurringLabel = new JLabel("reoccurring");
        }
        else {
            reoccurringLabel = new JLabel("One-time");
        }

        costLabel = new JLabel("Cost = £" + cost);


        gridPanel.add(costLabel, constraints);
        gridPanel.add(nameLabel, constraints);
        gridPanel.add(descriptionLabel, constraints);
        gridPanel.add(dateLabel, constraints);
        gridPanel.add(reoccurringLabel, constraints);


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

        Transaction transaction = new Transaction(cost, name, description, date, reoccurring, reoccurringString, reoccurringXlyNumber, category, isExpense);
        System.out.println(reoccurringString + " " + reoccurringXlyNumber);
        main.addTransaction(transaction);


    }
}
