package ru.otus.atm.exceptions;

public class NegativeAmountException extends RuntimeException {

    public NegativeAmountException(int amount) {
        super("Negative amount: " + amount);
    }
}
