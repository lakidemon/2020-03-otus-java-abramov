package ru.otus.atm.exceptions;

public class NotEnoughBanknotesException extends RuntimeException {

    public NotEnoughBanknotesException(int sum) {
        super("Cannot dispense " + sum + " sum: not enough banknotes");
    }
}
