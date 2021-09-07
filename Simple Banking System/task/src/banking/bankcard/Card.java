package banking.bankcard;

import java.util.Objects;

public class Card {
    private String number;
    private String pin;

    public Card(String number, String pin) {
        this.number = number;
        this.pin = pin;
    }

    public String getNumber() {
        return number;
    }

    public String getPin() {
        return pin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return Objects.equals(number, card.number) &&
                Objects.equals(pin, card.pin);
    }

    @Override
    public int hashCode() {
        return 31 * number.hashCode() + pin.hashCode();
    }
}
