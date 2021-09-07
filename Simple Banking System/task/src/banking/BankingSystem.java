package banking;

import banking.bankcard.Card;
import banking.bankcard.CardGenerator;
import banking.database.InterfaceDB;
import banking.database.SQLiteDatabase;

import java.util.Scanner;

public class BankingSystem {
    private Scanner scanner = new Scanner(System.in);
    private InterfaceDB dataBase;
    private boolean isExit = false;

    public void run(String databaseName) {
        dataBase = new SQLiteDatabase(databaseName);

        while (!isExit) {
            welcomeUI();
            int action;
            try {
                action = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                action = -1;
            }
            switch (action) {
                case 1:
                    registerCard();
                    break;
                case 2:
                    loggingInBankSystem();
                    break;
                case 0:
                    System.out.println("Bye!");
                    isExit = true;
                    break;
                default:
            }
            if (isExit) {
                dataBase.close();
            }
        }
    }

    private void loggingInBankSystem() {
        System.out.println("Enter your card number:");
        String cardNumberTest = scanner.nextLine();
        System.out.println("Enter your PIN:");
        String pinTest = scanner.nextLine();
        Account current = dataBase.loginAccount(new Card(cardNumberTest, pinTest));

        if (current != null) {
            boolean isLogout = false;
            System.out.println("You have successfully logged in!");
            while (!isLogout) {
                loginUI();
                int loginAction;
                try {
                    loginAction = Integer.parseInt(scanner.nextLine());
                } catch (NumberFormatException e) {
                    loginAction = -1;
                }

                switch (loginAction) {
                    case 1:
                        System.out.print("Balance: " + current.getBalance() + "$");
                        break;
                    case 2:
                        addIncome(current);
                        break;
                    case 3:
                        doTransfer(current);
                        break;
                    case 4:
                        System.out.println("The account has been closed!");
                        dataBase.deleteAccount(current);
                        isLogout = true;
                        break;
                    case 5:
                        System.out.println("\nYou have successfully logged out!");
                        break;
                    case 0:
                        System.out.println("\nBye!");
                        isExit = true;
                        return;
                    default:
                }
            }
        } else {
            System.out.println("Wrong card number or PIN!");
        }

    }

    private void doTransfer(Account current) {
        System.out.println("Transfer\n" + "Enter card number:");
        String recipientCardNumber = scanner.nextLine();
        if (recipientCardNumber.equals(current.getCard().getNumber())) {
            System.out.println("You can't transfer money to the same account!");
        } else if (!CardGenerator.isCorrectNumber(recipientCardNumber)) {
            System.out.println("Probably you made mistake in the card number. Please try again!");
        } else if (!dataBase.findAccount(recipientCardNumber)) {
            System.out.println("Such a card does not exist.");
        } else {
            System.out.println("Enter how much money you want to transfer:");
            int moneyTransfer = 0;
            try {
                moneyTransfer = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("You need to write a number!");
            }
            if (moneyTransfer > current.getBalance()) {
                System.out.println("Not enough money!" + current.getBalance());
            } else {
                dataBase.updateAccount(current.getCard().getNumber(), -moneyTransfer);
                current.addBalance(-moneyTransfer);
//                userHistoryFile.printInfo(String.format("Transfer money: %d \n to card: %s", moneyTransfer, recipientCardNumber));
                dataBase.updateAccount(recipientCardNumber, moneyTransfer);
                System.out.println("Success!");
            }
        }
    }

    private void addIncome(Account current) {
        System.out.println("Enter income:");
        int increment;
        try {
            increment = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            increment = 0;
        }
        dataBase.updateAccount(current.getCard().getNumber(), increment);
        current.addBalance(increment);
        System.out.println("Income was added!");
    }

    private void registerCard() {
        Account newUser = new Account(CardGenerator.create());
        dataBase.createAccount(newUser);
        System.out.println((String.format(
                "Your card has been created\n" +
                        "Your card number:\n" +
                        "%s\n" +
                        "Your card PIN:\n" +
                        "%s",
                newUser.getCard().getNumber(),
                newUser.getCard().getPin())
        ));

    }

    private void loginUI() {
        System.out.println("" +
                "1. Balance\n" +
                "2. Add income\n" +
                "3. Do transfer\n" +
                "4. Close account\n" +
                "5. Log out\n" +
                "0. Exit");
    }

    private void welcomeUI() {
        System.out.println("" +
                "1. Create an account\n" +
                "2. Log into account\n" +
                "0. Exit"
        );
    }
}
