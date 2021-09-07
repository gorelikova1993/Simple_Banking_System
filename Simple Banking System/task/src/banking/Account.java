package banking;

import banking.bankcard.Card;

public class Account {
    private int balance;
    private Card card;

    public Account(int balance, Card card) {
        this.balance = balance;
        this.card = card;
    }

    public Account(Card card) {
        balance = 0;
        this.card = card;
    }

    public int getBalance() {
        return balance;
    }

    public void addBalance(int balance) {
        this.balance += balance;
    }

    public Card getCard() {
        return card;
    }

}