package com.expense.main;

import java.time.LocalDate;
import java.util.Scanner;

import com.expense.bean.TransactionBean;
import com.expense.util.TransactionManager;

public class ExpenseTrackerApp {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        TransactionManager manager = new TransactionManager();

        System.out.print("Do you want to load data from a file? (y/n): ");
        if (sc.nextLine().equalsIgnoreCase("y")) {
            System.out.print("Enter filename: ");
            String filename = sc.nextLine();
            manager.loadFromExcelFile(filename);
        }

        while (true) {
            System.out.println("\n--- Expense Tracker Menu ---");
            System.out.println("1. Add Income");
            System.out.println("2. Add Expense");
            System.out.println("3. View Monthly Summary");
            System.out.println("4. Save Data to File");
            System.out.println("5. Exit");

            System.out.print("Choose an option: ");
            int choice = Integer.parseInt(sc.nextLine());

            switch (choice) {
                case 1:
                    System.out.print("Enter income category (e.g. salary, business): ");
                    String inCat = sc.nextLine();
                    System.out.print("Enter amount: ");
                    double inAmt = Double.parseDouble(sc.nextLine());
                    manager.addTransaction(new TransactionBean("income", inCat, inAmt, LocalDate.now()));
                    break;
                case 2:
                    System.out.print("Enter expense category (e.g. food, rent, travel): ");
                    String exCat = sc.nextLine();
                    System.out.print("Enter amount: ");
                    double exAmt = Double.parseDouble(sc.nextLine());
                    manager.addTransaction(new TransactionBean("expense", exCat, exAmt, LocalDate.now()));
                    break;
                case 3:
                    System.out.print("Enter year (e.g. 2025): ");
                    int year = Integer.parseInt(sc.nextLine());
                    System.out.print("Enter month number (1-12): ");
                    int month = Integer.parseInt(sc.nextLine());
                    manager.printMonthlySummary(year, month);
                    break;
                case 4:
                    System.out.print("Enter filename to save: ");
                    String saveFile = sc.nextLine();
                    manager.saveToFile(saveFile);
                    break;
                case 5:
                    System.out.println("Exiting. Goodbye!");
                    sc.close();
                    return;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }
}
