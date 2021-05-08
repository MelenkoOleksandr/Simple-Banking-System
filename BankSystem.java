package banking;

import org.sqlite.SQLiteDataSource;

import java.sql.*;
import java.util.Scanner;

public class BankSystem {
    private static BankCard[] accounts = new BankCard[1000];
    static Scanner scanner = new Scanner(System.in);
    public static int amount;
    public static int currentCard;
    public static int currentTransfer;
    protected static String dbURL;
    protected static SQLiteDataSource dataSource=  new SQLiteDataSource();


    public BankSystem(String dbName) {
        amount = 0;
        this.dbURL = "jdbc:sqlite:" + dbName;
        dataSource.setUrl(dbURL);
        synchronizeDBWithBank();
        createDataBase();

    }

    public static void synchronizeDBWithBank() {
        try (Connection con =  DriverManager.getConnection(dbURL)) {
            // Statement creation

            try (Statement statement = con.createStatement()){
                try (ResultSet cardTable = statement.executeQuery("SELECT * FROM card")) {

                    while (cardTable.next()) {
                        // Retrieve column values
                        int id = cardTable.getInt("id");
                        String number = cardTable.getString("number");
                        String pin = cardTable.getString("pin");
                        int ballance = cardTable.getInt("balance");
                        while (pin.length() != 4) {
                            pin = "0" + pin;
                        }
                        //System.out.println("Adding card to array with (" + number + "," + pin+ ", " + ballance + ")");

                        createNewCardWithParameters(number, pin,ballance);
                        /*System.out.println("Amount = " + amount);
                        showCard(amount);*/
                        //getArr();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void showCard(int i) {
        System.out.println(accounts[i - 1].toString());
    }

    public static void createNewCardWithParameters(String number, String pin, int ballance) {
        accounts[amount] = new BankCard(number, pin, ballance);
        amount++;
        //System.out.println("Amount" + amount);
    }

    public static void createNewCard() {

        accounts[amount] = new BankCard();
        addToDB(accounts[amount]);
        System.out.print("Your card has been created\n" + accounts[amount].toString());
        amount++;
        //System.out.println("Amount" + amount);
    }

    public static void showMenuForLogged() {
        System.out.println("1. Balance");
        System.out.println("2. Add income");
        System.out.println("3. Do transfer");
        System.out.println("4. Close account");
        System.out.println("5. Log out");
        System.out.println("0. Exit");
    }

    //Log in bankSystem
    public static void logIn() {

        System.out.println("Enter your card number:");
        String cardNumber = scanner.nextLine();
        System.out.println("Enter your PIN:");
        String PIN = scanner.nextLine();
        if (checkCards(cardNumber, PIN)) {
            System.out.println("You have successfully logged in!");
            showMenuForLogged();
            int choice = Integer.parseInt(scanner.nextLine());
            while (true) {
                switch (choice) {
                    case 1:
                        System.out.println("Ballance: " + accounts[currentCard].getBallance());
                        break;
                    case 2:
                        addIncome();
                        break;
                    case 3:
                        System.out.println("Transfer");
                        System.out.println("Enter card number:");
                        String userInput = scanner.nextLine();
                        while (userInput.isEmpty()) {
                            userInput = scanner.nextLine();
                        }
                        doTransfer(userInput);
                        break;
                    case 4:
                        closeAccount();
                        return;
                    case 5:
                        System.out.println("You have successfully logged out!");
                        return;
                    case 0:
                        getDB();
                        //getArr();
                        System.out.println("Bye");
                        System.exit(0);
                    default:
                        break;
                }
                showMenuForLogged();
                choice = Integer.parseInt(scanner.nextLine());
            }

        } else {
            System.out.println("Wrong card number or PIN!");
        }
    }

    protected static void addIncome() {
        System.out.println("Enter income:");
        int additionIncome = Integer.parseInt(scanner.nextLine());
        accounts[currentCard].addIncome(additionIncome);
        addIncomeToDB(additionIncome);
        System.out.println("Income was added!");
    }


    protected static void doTransfer(String transferCard) {
        if (transferCard.length() != 16) {
            System.out.println("Not enought symb");
            return;
        }
        char[] transArr = transferCard.toCharArray();
        String first15 = charArrToString(transArr);
        char lastChar = transArr[transArr.length - 1];
        //Luch alg
        if (accounts[currentCard].luchAlgoritm(first15) != lastChar) {
            System.out.println("Probably you made a mistake in the card number. Please try again!");
            return;
        }
        if (transferCard.equals(accounts[currentCard].getCardNumber())) {
            System.out.println("You can't transfer money to the same account!");
            return;
        }
        if (!checkCardsTransfer(transferCard)) {
            System.out.println("Such a card does not exist.");
            return;
        } else {
            System.out.println("Enter how much money you want to transfer:");
            int trasferMoney = Integer.parseInt(scanner.nextLine());
            if (accounts[currentCard].getBallance() < trasferMoney) {
                System.out.println("Not enough money!");
                return;
            }
            accounts[currentCard].addIncome(-trasferMoney);
            accounts[currentTransfer].addIncome(trasferMoney);
            transInDB(trasferMoney);
        }
    }

    public static String charArrToString(char[] arr) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < arr.length - 1; i++) {
            str.append(arr[i]);
        }
        return str.toString();
    }



    
    //checking card
    protected static boolean checkCards(String cardNumber, String PIN) {
        int cur = 0;
        for (int i = 0; i < amount; i++) {
            if (accounts[i].checkCardNumber(cardNumber) && accounts[i].checkCardPassword(PIN)) {
                currentCard = i;
                return true;
            }
        }
        return false;

    }

    //checking card
    protected static boolean checkCardsTransfer(String cardNumber) {
        int cur = 0;
        for (int i = 0; i < amount; i++) {
            if (accounts[i].checkCardNumber(cardNumber)) {
                currentTransfer = i;
                return true;
            }
        }
        return false;
    }

    //Creating table card in DB
    public static void createDataBase() {
        try (Connection con =  DriverManager.getConnection(dbURL)) {
            // Statement creation

            try (Statement statement = con.createStatement()){

                // Statement execution
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS card (" +
                        "id INTEGER PRIMARY KEY," +
                        "number TEXT NOT NULL," +
                        "pin TEXT NOT NULL," +
                        "balance INTEGER DEFAULT 0)");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Adding the card to DB
    public static void addToDB(BankCard card) {
        try (Connection con =  DriverManager.getConnection(dbURL)) {
            // Statement creation
            try (Statement statement = con.createStatement()) {
                // Statement execution
                int i = statement.executeUpdate("INSERT INTO card (number, pin, balance) VALUES " + card.stringForDB());

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void getArr() {
        for (int i = 0; i < accounts.length; i++) {
            if (accounts[i] != null) {
                System.out.println(accounts[i].toString());
            }
        }

    }

    //Show DB content
    public static void getDB() {
        try (Connection con =  DriverManager.getConnection(dbURL)) {
            // Statement creation

            try (Statement statement = con.createStatement()){
                try (ResultSet cardTable = statement.executeQuery("SELECT * FROM card")) {
                    while (cardTable.next()) {
                        // Retrieve column values
                        int id = cardTable.getInt("id");
                        String number = cardTable.getString("number");
                        String pin = cardTable.getString("pin");
                        int balance = cardTable.getInt("balance");
                        while (pin.length() != 4) {
                            pin = "0" + pin;
                        }
                        System.out.printf("id %d%n", id);
                        System.out.printf("number: %s%n", number);
                        System.out.printf("pin: %s%n", pin);
                        System.out.printf("balance: %s%n", balance);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //edit balance card
    protected static void addIncomeToDB(int additionalIncome) {
        String updateOrigin = "UPDATE card SET balance = balance + ? WHERE id = ?";
        try (Connection con =  DriverManager.getConnection(dbURL)) {
            // Statement creation
            try (PreparedStatement preparedStatement = con.prepareStatement(updateOrigin)) {
                preparedStatement.setInt(1, additionalIncome);
                preparedStatement.setInt(2, currentCard + 1);

                preparedStatement.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //delete account from DB
    protected static void closeAccount() {
        String updateOrigin = "DELETE FROM card WHERE number = ?";
        try (Connection con =  DriverManager.getConnection(dbURL)) {
            // Statement creation
            try (PreparedStatement preparedStatement = con.prepareStatement(updateOrigin)) {
                preparedStatement.setString(1, accounts[currentCard].getCardNumber());
                preparedStatement.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("The account has been closed!");
    }
    //delete account from DB
    protected static void deleteDB() {
        String updateOrigin = "DELETE FROM card";
        try (Connection con =  DriverManager.getConnection(dbURL)) {
            // Statement creation
            try (PreparedStatement preparedStatement = con.prepareStatement(updateOrigin)) {
                preparedStatement.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("The account has been closed!");
    }
    protected static void transInDB(int sum) {
        String updateOrigin = "UPDATE card SET balance = balance - ? WHERE id = ?";
        String updateTransfer = "UPDATE card SET balance = balance + ? WHERE id = ?";
        try (Connection con =  DriverManager.getConnection(dbURL)) {
            // Disable auto-commit mode
            con.setAutoCommit(false);

            try {
                PreparedStatement originStatement = con.prepareStatement(updateOrigin);
                PreparedStatement transferStatement = con.prepareStatement(updateTransfer);

                originStatement.setInt(1, sum);
                originStatement.setInt(2, currentCard + 1);
                originStatement.executeUpdate();

                transferStatement.setInt(1, sum);
                transferStatement.setInt(2, currentTransfer + 1);
                transferStatement.executeUpdate();

                con.commit();
                con.setAutoCommit(true);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
