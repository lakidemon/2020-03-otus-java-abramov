package ru.otus.atm.exceptions;

public class InvalidTakeAmountException extends RuntimeException {

    public InvalidTakeAmountException(int requested, int failedRemaining) {
        super("Cannot dispense " + requested + " sum: no suitable banknote for " + failedRemaining);
    }

}
