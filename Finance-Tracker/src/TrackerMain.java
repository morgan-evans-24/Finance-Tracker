import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart; // Should not show an error if JAR is included correctly
import org.jfree.data.general.DefaultPieDataset;


public class TrackerMain {

    double incomeSum = 0;
    double expenseSum = 0;



    private final JFrame frame;
    JPanel incomeGridPanel = new JPanel();
    JPanel expenseGridPanel = new JPanel();
    JPanel navbarPanel = new JPanel();

    JPanel contentPanel = new JPanel();
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

    ArrayList<Transaction> expenses = new ArrayList<Transaction>();
    ArrayList<Transaction> incomes = new ArrayList<Transaction>();
    ArrayList<TransactionCategory> categories = new ArrayList<TransactionCategory>();



    TrackerMain() {
        expenseTotalLabel.setText("No expenses");
        incomeTotalLabel.setText("No income");

        contentPanel.setLayout(new BorderLayout());
        expenseGridPanel.setLayout(new GridLayout());
        incomeGridPanel.setLayout(new GridLayout());

        //TESTING
        addTransaction(new Transaction(140, "new laptop", "asdasd", "07/08/1990", 1, "School", true));
        addTransaction(new Transaction(132, "shopping", "asdasd", "07/08/1990", 1, "Groceries", true));
        addTransaction(new Transaction(80, "repairs", "asdasd", "07/08/1990", 1, "Car", true));
        addTransaction(new Transaction(250, "More shopping", "asdasd", "07/08/1990", 1, "Groceries", true));
        addTransaction(new Transaction(80, "fuel", "asdasd", "07/08/1990", 1, "Car", true));
        addTransaction(new Transaction(300, "loans", "asdasd", "07/08/1990", 1, "School", true));
        sortExpensesByCategory();

        makePieChart();


        navbarInit();


        frame = new JFrame();
        frame.setSize(850, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setTitle("Finance Tracker");
        frame.getContentPane().setLayout(new BorderLayout());



        expenseButton = new JButton("add expense");
        expenseButton.addActionListener(e -> {
            ListedTransaction listedTransaction = new ListedTransaction(expenseGridPanel, true, this);
            frame.validate();
            frame.repaint();

        });
        incomeButton = new JButton("add income");
        incomeButton.addActionListener(e -> {
            ListedTransaction listedTransaction = new ListedTransaction(incomeGridPanel, false, this);
            frame.validate();
            frame.repaint();


        });


        frame.getContentPane().add(contentScrollPane, BorderLayout.CENTER);
        frame.getContentPane().add(navbarPanel, BorderLayout.WEST);











        frame.setVisible(true);
        frame.validate();
        frame.repaint();
        showHomeScreen();

    }

    private void makePieChart() {

        DefaultPieDataset dataset = new DefaultPieDataset();

        String currentCategory = "";
        double currentTotal = 0.0;

        for (int i = 0; i < expenses.size(); i++) {
            if (i == 0) {      //Returns true if expense, false if income
                currentCategory = expenses.get(i).getCategory();
                currentTotal = expenses.get(i).getCost();
                continue;
            }
            if (!expenses.get(i).getCategory().equals(currentCategory)) {
                dataset.setValue(currentCategory, currentTotal);
                currentCategory = expenses.get(i).getCategory();
                currentTotal = expenses.get(i).getCost();
            }
            else {
                currentTotal += expenses.get(i).getCost();
            }

        }
        dataset.setValue(currentCategory, currentTotal);

          //USE ARRAYLIST OF OBJECTS "TRANSACTIONS" TO TRACK THIS

        JFreeChart chart = ChartFactory.createPieChart(
                "Test",
                dataset,
                true,
                true,
                false
        );

        chartPanel = new ChartPanelWithCircle(chart);
        chartPanel.setLayout(new BorderLayout());

        contentPanel.add(chartPanel);
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



    // BUTTONS
    public void showHomeScreen() {
        contentPanel.removeAll();
        contentPanel.add(chartPanel, BorderLayout.CENTER);
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
        }
        else {
            incomes.add(transaction);
        }

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
    }

    public ArrayList<TransactionCategory> getTransactionCategories() {
        return categories;
    }


    public void sortExpensesByCategory() {
        boolean sorted = false;
        boolean changed = false;
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
        new TrackerMain();
    }
}