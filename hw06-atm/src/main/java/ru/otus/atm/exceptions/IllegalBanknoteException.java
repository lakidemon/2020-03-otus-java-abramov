package ru.otus.atm.exceptions;

import ru.otus.atm.banknote.Banknote;

public class IllegalBanknoteException extends RuntimeException {

    public IllegalBanknoteException(Banknote value) {
        super("ATM doesn't have cell for " + value.getClass().getSimpleName() + " banknotes");
    }

}
