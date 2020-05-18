package ru.otus.atm.cell;

import ru.otus.atm.exceptions.IllegalBanknoteException;
import ru.otus.atm.exceptions.NegativeAmountException;

import java.util.Map;

public interface CellRepository {

    int getSum();

    int getAmount(int value) throws IllegalBanknoteException;

    void setAmount(int value, int amount) throws IllegalBanknoteException, NegativeAmountException;

    Map<Integer, Integer> getCellsContent();

}
