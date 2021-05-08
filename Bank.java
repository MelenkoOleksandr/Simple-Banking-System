package banking;

import org.sqlite.SQLiteDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Bank {
    protected static BankSystem b;
    final static Scanner scanner = new Scanner(System.in);
    public Bank (String dbName) {
        b = new BankSystem(dbName);
    }
    public static void work() {
        printMenu();
        int choice = Integer.parseInt(scanner.nextLine());
        while (true) {
            switch (choice) {
                case 1:
                    b.createNewCard();
                    printMenu();
                    break;
                case 2:
                    b.logIn();
                    printMenu();
                    break;
                case 0:
                    System.out.println("Bye");
                    b.getDB();
                    return;
                default:
                    break;
            }
            choice = Integer.parseInt(scanner.nextLine());
        }
    }

    public static void printMenu() {
        System.out.println("1. Create an account");
        System.out.println("2. Log into account");
        System.out.println("0. Exit");
    }

}
