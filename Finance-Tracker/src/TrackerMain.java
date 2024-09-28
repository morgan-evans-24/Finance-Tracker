import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.Strictness;
import com.google.gson.reflect.TypeToken;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart; // Should not show an error if JAR is included correctly

import org.jfree.chart.plot.CenterTextMode;
import org.jfree.chart.plot.RingPlot;
import org.jfree.data.general.DefaultPieDataset;

import com.formdev.flatlaf.FlatLightLaf;




public class TrackerMain {

    double incomeSum = 0;
    double expenseSum = 0;
    int noOfTransactions = 0;



    private final JFrame frame;
    JPanel incomeGridPanel = new JPanel(new GridBagLayout());
    JPanel expenseGridPanel = new JPanel(new GridBagLayout());
    JPanel navbarPanel = new JPanel();

    JPanel contentPanel = new JPanel();
    JPanel contentGridBag = new JPanel();
    JScrollPane contentScrollPane = new JScrollPane(contentPanel);


    ChartPanel chartPanel;

    JButton incomeButton;
    JButton expenseButton;

    // Navbar buttons
    JButton homeButton;
    JButton expenseTabButton;
    JButton incomeTabButton;
    JButton settingsButton;

    JLabel incomeTotalLabel = new JLabel();
    JLabel expenseTotalLabel = new JLabel();
    
    String[] timeFrames = {"Weekly", "Monthly", "Yearly"};
    String currentTimeFrame = "Weekly";


    
    JComboBox timeFrameBox;

    ArrayList<Transaction> expenses = new ArrayList<>();
    ArrayList<Transaction> incomes = new ArrayList<>();
    ArrayList<TransactionCategory> categories = new ArrayList<>();

    String expensesPath = "src/JSON/expenses.json";
    String incomesPath = "src/JSON/incomes.json";
    String categoriesPath = "src/JSON/categories.json";

    Gson gson;

    ListedTransaction listedTransaction;

    TrackerMain() {

        gson = new GsonBuilder().registerTypeAdapter(Color.class, new ColorAdapter()).setStrictness(Strictness.LENIENT).setPrettyPrinting().create();

        expenses = readJsonFile(expensesPath, new TypeToken<ArrayList<Transaction>>() //convert ArrayList<Transaction> into a Typetoken, because type erasure messes with Gson
        {}.getType(), "Expenses");

        incomes = readJsonFile(incomesPath, new TypeToken<ArrayList<Transaction>>()
        {}.getType(), "Incomes");

        categories = readJsonFile(categoriesPath, new TypeToken<ArrayList<TransactionCategory>>()
        {}.getType(), "Categories");





        timeFrameBox = new JComboBox(timeFrames);
        
        
        expenseTotalLabel.setText("No expenses");
        incomeTotalLabel.setText("No income");

        contentGridBag.setLayout(new GridBagLayout());
        contentPanel.setLayout(new BorderLayout());

        expenseGridPanel.setBackground(Color.yellow);



        sortExpensesByCategory();




        navbarInit();

        timeFrameBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (timeFrameBox.getSelectedItem() == "Weekly") {
                    currentTimeFrame = "Weekly";
                }
                else if (timeFrameBox.getSelectedItem() == "Monthly") {
                    currentTimeFrame = "Monthly";
                }
                else if (timeFrameBox.getSelectedItem() == "Yearly") {
                    currentTimeFrame = "Yearly";
                }
                showHomeScreen();
            }
        });


        frame = new JFrame();
        frame.setSize(850, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setTitle("Finance Tracker");
        frame.getContentPane().setLayout(new BorderLayout());



        expenseButton = new JButton("add expense");
        expenseButton.addActionListener(e -> {
            new ListedTransaction(expenseGridPanel, true, this);
            frame.validate();
            frame.repaint();

        });
        incomeButton = new JButton("add income");
        incomeButton.addActionListener(e -> {
            new ListedTransaction(incomeGridPanel, false, this);
            frame.validate();
            frame.repaint();


        });


        frame.getContentPane().add(contentScrollPane, BorderLayout.CENTER);
        frame.getContentPane().add(navbarPanel, BorderLayout.WEST);







//        listedTransaction.makeCards(expenseGridPanel, frame, this);



        frame.setVisible(true);
        frame.validate();
        frame.repaint();
        showHomeScreen();

        
    }


    private <T> ArrayList<T> readJsonFile(String path, Type type, String fileType) { //fileType string is just used for console messages
        ArrayList<T> list = new ArrayList<>();
        File file = new File(path);
        if (!file.exists() || file.length() == 0) {
            System.out.println(fileType + " file is empty or does not exist, starting with empty file");
            return list;  // Return empty list
        }

        try (FileReader reader = new FileReader(file)) { //This FileReader moves through the Json files line by line
            list = gson.fromJson(reader, type); //This line deserializes the data and turns it into an array of Transactions
            if (list == null) { //Type == arraylist<Transaction>, we just use type to get around type erasure
                list = new ArrayList<>();  // Return empty list if file has null content
            }
        } catch (IOException e) {
            System.out.println(fileType + " file not found or could not be read, starting with empty file");
        }
        return list;
    }




    private void makeRingChart(JPanel panelToAddTo, GridBagConstraints constraints) {


        DefaultPieDataset dataset = new DefaultPieDataset();

        String currentCategory = "";
        double currentTotal = 0.0;
        double totalExpenses = 0.0;
        double totalIncome;

        double thisCost;

        boolean empty = true;

        for (int i = 0; i < expenses.size(); i++) {

            thisCost = expenses.get(i).getCost();
            System.out.println(expenses.get(i).getReoccurringFrequency().toString());
            if (currentTimeFrame.equals("Weekly")) {
                if (expenses.get(i).getReoccurringFrequency().toString().equals("YEARLY")) {
                    thisCost = thisCost / 52;
                }
                if (expenses.get(i).getReoccurringFrequency().toString().equals("MONTHLY")) {
                    thisCost = thisCost / 4;
                }
                if (expenses.get(i).getReoccurringFrequency().toString().equals("CUSTOM")) {
                    thisCost = thisCost * ((double) 7 /expenses.get(i).getCustomReoccurringFrequency());
                }
            }
            if (currentTimeFrame.equals("Monthly")) {
                if (expenses.get(i).getReoccurringFrequency().toString().equals("YEARLY")) {
                    thisCost = thisCost / 12;
                }
                if (expenses.get(i).getReoccurringFrequency().toString().equals("WEEKLY")) {
                    thisCost = thisCost * 4;
                }
                if (expenses.get(i).getReoccurringFrequency().toString().equals("CUSTOM")) {
                    thisCost = thisCost * ((double) 30 /expenses.get(i).getCustomReoccurringFrequency());
                }
            }
            if (currentTimeFrame.equals("Yearly")) {
                if (expenses.get(i).getReoccurringFrequency().toString().equals("MONTHLY")) {
                    thisCost = thisCost * 12;
                }
                if (expenses.get(i).getReoccurringFrequency().toString().equals("WEEKLY")) {
                    thisCost = thisCost * 52;
                }
                if (expenses.get(i).getReoccurringFrequency().toString().equals("CUSTOM")) {
                    thisCost = thisCost * ((double) 365 /expenses.get(i).getCustomReoccurringFrequency());
                }
            }


            totalExpenses += thisCost;
            if (i == 0) {      //Returns true if expense, false if income
                currentCategory = expenses.get(i).getCategory();
                currentTotal = thisCost;
                continue;
            }
            if (!expenses.get(i).getCategory().equals(currentCategory)) {
                dataset.setValue(currentCategory, currentTotal);
                empty = false;
                currentCategory = expenses.get(i).getCategory();
                currentTotal = thisCost;
            }
            else {
                currentTotal += thisCost;
            }

        }
        if (currentTotal != 0) {
            dataset.setValue(currentCategory, currentTotal);
            empty = false;
        }

        if (!empty) {
            currentTotal = 0.0;


            for (Transaction income : incomes) {
                thisCost = income.getCost();
                if (currentTimeFrame.equals("Weekly")) {
                    if (income.getReoccurringFrequency().toString().equals("YEARLY")) {
                        thisCost = thisCost / 52;
                    }
                    if (income.getReoccurringFrequency().toString().equals("MONTHLY")) {
                        thisCost = thisCost / 4;
                    }
                    if (income.getReoccurringFrequency().toString().equals("CUSTOM")) {
                        thisCost = thisCost * ((double) 7 /income.getCustomReoccurringFrequency());
                    }
                }
                if (currentTimeFrame.equals("Monthly")) {
                    if (income.getReoccurringFrequency().toString().equals("YEARLY")) {
                        thisCost = thisCost / 12;
                    }
                    if (income.getReoccurringFrequency().toString().equals("WEEKLY")) {
                        thisCost = thisCost * 4;
                    }
                    if (income.getReoccurringFrequency().toString().equals("CUSTOM")) {
                        thisCost = thisCost * ((double) 30 /income.getCustomReoccurringFrequency());
                    }
                }
                if (currentTimeFrame.equals("Yearly")) {
                    if (income.getReoccurringFrequency().toString().equals("MONTHLY")) {
                        thisCost = thisCost * 12;
                    }
                    if (income.getReoccurringFrequency().toString().equals("WEEKLY")) {
                        thisCost = thisCost * 52;
                    }
                    if (income.getReoccurringFrequency().toString().equals("CUSTOM")) {
                        thisCost = thisCost * ((double) 365 /income.getCustomReoccurringFrequency());
                    }
                }
                currentTotal += thisCost;
            }
            totalIncome = currentTotal;
            dataset.setValue("Remaining Income", totalIncome-totalExpenses);



            //USE ARRAYLIST OF OBJECTS "TRANSACTIONS" TO TRACK THIS

            JFreeChart chart = ChartFactory.createRingChart(
                    "Test Ring Chart",
                    dataset,
                    false,
                    true,
                    false
            );


            RingPlot plot = (RingPlot) chart.getPlot();
            currentCategory = "";
            for (int i = 0; i < expenses.size(); i++) {
                if (i == 0) {
                    currentCategory = expenses.get(i).getCategory();
                    continue;
                }
                if (!expenses.get(i).getCategory().equals(currentCategory)) {
                    plot.setSectionOutlinePaint(currentCategory, Color.black);
                    plot.setSectionOutlineStroke(currentCategory, new BasicStroke(2.0f));
                    for (TransactionCategory c : categories) {
                        if (c.getCategory().equals(currentCategory)) {
                            plot.setSectionPaint(currentCategory, c.getColor());
                        }
                    }
                    currentCategory = expenses.get(i).getCategory();
                }
            }

            plot.setShadowPaint(null);
            plot.setSectionOutlinePaint(currentCategory, Color.black);
            plot.setSectionOutlineStroke(currentCategory, new BasicStroke(2.0f));
            for (TransactionCategory c : categories) {
                if (c.getCategory().equals(currentCategory)) {
                    plot.setSectionPaint(currentCategory, c.getColor());
                }
            }

            plot.setOutlineVisible(false);
            plot.setLabelFont(new Font("Arial", Font.BOLD, 22));


            plot.setLabelBackgroundPaint(null);
            plot.setLabelShadowPaint(null);
            plot.setLabelOutlinePaint(null);

            plot.setSectionPaint("Remaining Income", new Color(255,255,255,0));
            plot.setSimpleLabels(true);

            plot.setSectionDepth(0.6);

            plot.setCenterTextMode(CenterTextMode.FIXED);
            plot.setCenterTextFont(new Font("Arial", Font.BOLD, 22));
            plot.setCenterTextColor(Color.black);
            plot.setCenterText("Total income: " + totalIncome);



            plot.setSeparatorsVisible(false);



            chartPanel = new ChartPanel(chart);
            panelToAddTo.add(chartPanel, constraints);
        }
        else {

            JLabel emptyLabel = new JLabel("No expenses, head to the 'Expenses' tab to add some!");
            emptyLabel.setHorizontalAlignment(JLabel.CENTER);
            emptyLabel.setFont(new Font("Arial", Font.BOLD, 22));
            panelToAddTo.add(emptyLabel, constraints);
        }






    }

    private void navbarInit() {
        navbarPanel.setLayout(new GridLayout(4, 1));
        homeButton = new JButton("Home");
        homeButton.addActionListener(e -> showHomeScreen());
        expenseTabButton = new JButton("Expense Tab");
        expenseTabButton.addActionListener(e -> showExpenseScreen());
        incomeTabButton = new JButton("Income Tab");
        incomeTabButton.addActionListener(e -> showIncomeScreen());
        settingsButton = new JButton("Settings");
        navbarPanel.add(homeButton);
        navbarPanel.add(expenseTabButton);
        navbarPanel.add(incomeTabButton);
        navbarPanel.add(settingsButton);
    }

    public JFrame getFrame() {
        return frame;
    }

    public int getNoOfTransactions() {
        return noOfTransactions;
    }


    // BUTTONS
    public void showHomeScreen() {
        contentPanel.removeAll();
        contentGridBag.removeAll();

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.BOTH;

        GridBagConstraints constraints2 = new GridBagConstraints();
        constraints2.gridx = 0;
        constraints2.gridy = 0;
        constraints2.anchor = GridBagConstraints.NORTHEAST;
        constraints2.insets = new Insets(40, 0, 0, 40);
        contentGridBag.add(timeFrameBox, constraints2);
        makeRingChart(contentGridBag, constraints);
        contentPanel.add(contentGridBag, BorderLayout.CENTER);

        contentPanel.revalidate();
        frame.setVisible(true);
        frame.validate();
        frame.repaint();


    }
    public void showExpenseScreen() {
        contentPanel.removeAll();
        contentPanel.add(expenseGridPanel, BorderLayout.CENTER);
        contentPanel.add(expenseButton, BorderLayout.EAST);
        contentPanel.add(expenseTotalLabel, BorderLayout.SOUTH);
        contentPanel.revalidate();
        frame.setVisible(true);
        frame.validate();
        frame.repaint();
    }
    public void showIncomeScreen() {
        contentPanel.removeAll();
        contentPanel.add(incomeGridPanel, BorderLayout.CENTER);
        contentPanel.add(incomeButton, BorderLayout.EAST);
        contentPanel.add(incomeTotalLabel, BorderLayout.SOUTH);
        contentPanel.revalidate();
        frame.setVisible(true);
        frame.validate();
        frame.repaint();
    }

    public void addTransaction(Transaction transaction) {
        if (transaction.isExpense())
        {
            expenses.add(transaction);

            try (FileWriter writer = new FileWriter(expensesPath)) {
                gson.toJson(expenses, writer);
                System.out.println("Updated expenses file to " + expensesPath);
            } catch (IOException e) {
                System.out.println("Error writing to expenses file");
                e.printStackTrace();
            }

        }
        else {
            incomes.add(transaction);

            try (FileWriter writer = new FileWriter(incomesPath)) {
                gson.toJson(incomes, writer);
                System.out.println("Updated incomes file to " + incomesPath);
            } catch (IOException e) {
                System.out.println("Error writing to incomes file");
                e.printStackTrace();
            }
        }
        noOfTransactions++;


    }
    //END OF BUTTONS


    public void updateTotals(double expenses, double incomes) {
        incomeSum += incomes;
        incomeTotalLabel.setText("Total income: " + incomeSum);
        expenseSum += expenses;
        expenseTotalLabel.setText("Total expense: " + expenseSum);
    }

    public void addTransactionCategory(String name, Color color) {
        categories.add(new TransactionCategory(name, color)); //CALLED FROM LISTED TRANSACTION WHEN NEW TRANSACTION IS SELECTED
        try (FileWriter writer = new FileWriter(categoriesPath)) {
            gson.toJson(categories, writer);
            System.out.println("Updated categories file to " + categoriesPath);
        } catch (IOException e) {
            System.out.println("Error writing to categories file");
            e.printStackTrace();
        }
    }

    public ArrayList<TransactionCategory> getTransactionCategories() {
        return categories;
    }


    public void sortExpensesByCategory() {
        boolean sorted = false;
        boolean changed;
        Transaction temp;
        while (!sorted) {
            changed = false;
            for (int i = 0; i < expenses.size() - 2; i++) {
                if (expenses.get(i).getCategory().compareTo(expenses.get(i + 1).getCategory()) > 0) {
                    temp = expenses.get(i + 1);
                    expenses.set(i + 1, expenses.get(i));
                    expenses.set(i, temp);
                    changed = true;
                }
            }
            if (!changed) {
                sorted = true;
            }
        }
        for (int i = 0; i < expenses.size() - 1; i++) {
            System.out.println(expenses.get(i).getCategory());   //USED TO TEST
        }
    }



    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        new TrackerMain();
    }
}