package ru.otus.atm;

import lombok.RequiredArgsConstructor;
import ru.otus.atm.cell.CellRepository;
import ru.otus.atm.cell.StandardCellRepository;
import ru.otus.atm.exceptions.InvalidTakeAmountException;
import ru.otus.atm.exceptions.NegativeAmountException;
import ru.otus.atm.exceptions.NotEnoughBanknotesException;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ATM {
    private final CellRepository cellRepository;

    public int getCurrentSum() {
        return cellRepository.getSum();
    }

    public Map<Integer, Integer> takeMoney(int sum) throws InvalidTakeAmountException, NegativeAmountException {
        if (sum <= 0) {
            throw new NegativeAmountException(sum);
        }
        var cellMap = new HashMap<>(cellRepository.getCellsContent());
        var takenMap = new HashMap<Integer, Integer>();
        var sortedValues = cellMap.entrySet()
                .stream()
                .filter(e -> e.getValue() > 0)
                .map(Map.Entry::getKey)
                .sorted(Comparator.comparingInt(Integer::intValue).reversed())
                .collect(Collectors.toList());

        int remaining = sum;
        while (remaining > 0) {
            if (sortedValues.isEmpty()) {
                throw new NotEnoughBanknotesException(sum);
            }
            int banknote = closest(sortedValues, remaining);
            if (banknote == -1)
                throw new InvalidTakeAmountException(sum, remaining);
            remaining -= banknote;
            int available = cellMap.compute(banknote, (k, v) -> v - 1);
            takenMap.compute(banknote, (k, v) -> (v == null) ? 1 : v + 1);
            if (available == 0) {
                sortedValues.remove(banknote);
            }
        }

        cellMap.forEach(cellRepository::setAmount);
        return takenMap;
    }

    public static ATM atmForRubles() {
        var banknotes = new HashMap<Integer, Integer>();
        banknotes.put(10, 0); // будет пустая ячейка
        banknotes.put(50, 100);
        banknotes.put(100, 50);
        banknotes.put(200, 50);
        banknotes.put(500, 50);
        banknotes.put(1000, 25);
        banknotes.put(2000, 20);
        banknotes.put(5000, 10);
        return standardATM(banknotes);
    }

    public static ATM standardATM(Map<Integer, Integer> banknotes) {
        return new ATM(new StandardCellRepository(banknotes));
    }

    private static int closest(List<Integer> sortedValues, int number) {
        for (int value : sortedValues) {
            if (number >= value)
                return value;
        }
        return -1;
    }

}
