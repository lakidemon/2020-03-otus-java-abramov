package ru.otus.atm.cell;

import ru.otus.atm.banknote.Banknote;
import ru.otus.atm.exceptions.NegativeAmountException;

public interface Cell {

    Banknote getBanknote();

    int getAmount();

    void setAmount(int amount) throws NegativeAmountException;

    default boolean hasMoney() {
        return getAmount() > 0;
    }

    default int getSum() {
        return getBanknote().toMoney(getAmount());
    }

}
