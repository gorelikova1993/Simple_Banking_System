package banking;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Bank {
    Scanner scanner = new Scanner(System.in);

    private Account userAccount = null;
    private final String SUCCESS = "You have successfully logged in!";
    private final String FAIL = "Wrong card number or PIN!";
    private final String LOG_OUT = "You have successfully logged out!";
    private final String url = "jdbc:sqlite:card.s3db";
    private boolean repeat = true;
    private List<Account> accounts = new ArrayList<>();

    void createBank() {
        while (repeat) {
            showBankUI();
        }
        System.out.println("Bye!");
    }

    void showBankUI() {
        System.out.println("1. Create an account");
        System.out.println("2. Log into account");
        System.out.println("0. Exit");

        int response = Integer.valueOf(scanner.nextLine());

        switch (response) {
            case 1: createAccount(accounts);
                break;
            case 2: logIntoAccount(accounts);
                break;
            case 0:
                repeat = false;
                return;
            default:
                System.out.println("Wrong number!");
        }

    }

    void showAccountUI() {
        System.out.println("1. Balance");
        System.out.println("2. Log out");
        System.out.println("0. Exit");

        int response = Integer.valueOf(scanner.nextLine());

        switch (response) {
            case 1: showBalance(userAccount);
                break;
            case 2: userAccount.setLogged(false);
                System.out.println(LOG_OUT);
                return;
            case 0:
                userAccount.setLogged(false);
                repeat = false;
                return;
        }
    }

    void createAccount(List<Account> accounts) {
        Account account = new Account();
        System.out.println("Your card has been created");
        System.out.println("Your card number:");
        System.out.println(account.getAccountNumber());
        System.out.println("Your card PIN:");
        System.out.println(account.getPin());
        insert(account.getAccountNumber(), account.getPin(), account.getBalance());
        accounts.add(account);
    }

    void logIntoAccount(List<Account> accounts) {
        System.out.println("Enter your card number:");
        String cardNumber = scanner.nextLine();
        System.out.println("Enter your PIN:");
        String pin = scanner.nextLine();

        for (Account account: accounts) {
            if (cardNumber.equals(account.getAccountNumber())) {
                if (account.getPin().equals(pin)) {
                    account.setLogged(true);
                    userAccount = account;
                    System.out.println(SUCCESS);
                    while (userAccount.isLogged()){
                        showAccountUI();
                    }
                } else {
                    System.out.println(FAIL);
                }
            } else {
                System.out.println(FAIL);
            }
        }
    }

    void showBalance(Account account) {
        System.out.println("Balance: " + account.getBalance());
    }

    private Connection connect() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(this.url);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public void insert(String number, String pin, double balance) {
        String sql = "INSERT INTO card(number,pin,balance) VALUES(?,?,?)";

        try (Connection connection = this.connect();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, number);
            pstmt.setString(2, pin);
            pstmt.setDouble(3, balance);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
