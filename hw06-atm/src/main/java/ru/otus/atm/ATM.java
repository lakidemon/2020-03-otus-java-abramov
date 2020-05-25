package ru.otus.atm;

import lombok.RequiredArgsConstructor;
import ru.otus.atm.banknote.Banknote;
import ru.otus.atm.banknote.Ruble;
import ru.otus.atm.cell.Cell;
import ru.otus.atm.cell.CellRepository;
import ru.otus.atm.cell.standard.StandardCellRepository;
import ru.otus.atm.exceptions.InvalidTakeAmountException;
import ru.otus.atm.exceptions.NegativeAmountException;
import ru.otus.atm.exceptions.NotEnoughBanknotesException;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ATM {
    protected final CellRepository cellRepository;

    public int getCurrentSum() {
        return cellRepository.getSum();
    }

    public Map<Banknote, Integer> takeMoney(int sum) throws InvalidTakeAmountException, NegativeAmountException {
        if (sum <= 0) {
            throw new NegativeAmountException(sum);
        }
        var takenMap = new HashMap<Banknote, Integer>();
        var sortedValues = cellRepository.getCells()
                .stream()
                .filter(Cell::hasMoney)
                .map(Cell::getBanknote)
                .collect(Collectors.toList());

        var remaining = sum;
        while (remaining > 0) {
            if (sortedValues.isEmpty()) {
                throw new NotEnoughBanknotesException(sum);
            }
            var banknote = closest(sortedValues, remaining).orElse(null);
            if (banknote == null) {
                throw new InvalidTakeAmountException(sum, remaining);
            }
            remaining -= banknote.getValue();

            var taken = takenMap.compute(banknote, INCREMENT_OR_1);
            if (cellRepository.getAmount(banknote) == taken) {
                sortedValues.remove(banknote);
            }
        }

        takenMap.forEach(cellRepository::take);
        return takenMap;
    }

    public static ATM atmForRubles() {
        var banknotes = new HashMap<Banknote, Integer>();
        banknotes.put(Ruble._10, 50);
        banknotes.put(Ruble._1000, 25);
        banknotes.put(Ruble._2000, 20);
        banknotes.put(Ruble._50, 100);
        banknotes.put(Ruble._100, 50);
        banknotes.put(Ruble._5000, 10);
        banknotes.put(Ruble._200, 50);
        banknotes.put(Ruble._500, 50);
        return standardATM(banknotes);
    }

    public static ATM standardATM(Map<Banknote, Integer> banknotes) {
        return new ATM(new StandardCellRepository(banknotes));
    }

    private static Optional<Banknote> closest(Collection<Banknote> sortedValues, int number) {
        for (var value : sortedValues) {
            if (number >= value.getValue()) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }

    private static BiFunction<Object, Integer, Integer> INCREMENT_OR_1 = (k, v) -> (v == null) ? 1 : v + 1;
}
