package ru.otus.atm.cell;

import ru.otus.atm.banknote.Banknote;
import ru.otus.atm.exceptions.IllegalBanknoteException;
import ru.otus.atm.exceptions.NegativeAmountException;

import java.util.Map;

public interface CellRepository {

    int getSum();

    int getAmount(Banknote banknote) throws IllegalBanknoteException;

    void setAmount(Banknote banknote, int amount) throws IllegalBanknoteException, NegativeAmountException;

    void take(Banknote banknote, int amount) throws IllegalBanknoteException, NegativeAmountException;

    Map<Banknote, Integer> getCellsContent();

}
