package ru.otus.atm;

import ru.otus.atm.banknote.Banknote;
import ru.otus.atm.cell.CellRepository;
import ru.otus.atm.exceptions.InvalidTakeAmountException;
import ru.otus.atm.exceptions.NegativeAmountException;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public interface ATM {

    CellRepository getCellRepository();

    int getCurrentSum();

    Map<Banknote, Integer> takeMoney(int sum) throws InvalidTakeAmountException, NegativeAmountException;

    static Optional<Banknote> closest(Collection<Banknote> sortedValues, int number) {
        for (var value : sortedValues) {
            if (number >= value.getValue()) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }

}
