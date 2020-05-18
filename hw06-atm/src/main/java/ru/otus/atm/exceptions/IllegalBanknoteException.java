package ru.otus.atm.exceptions;

public class IllegalBanknoteException extends RuntimeException {

    public IllegalBanknoteException(int value) {
        super("ATM doesn't have cell for " + value + " banknotes");
    }

}
