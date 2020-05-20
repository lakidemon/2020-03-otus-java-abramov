package ru.otus.atm.banknote;

public interface Banknote {

    int getValue();

    default int toMoney(int amount) {
        return amount * getValue();
    }
}
