package ru.otus.atm;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.otus.atm.banknote.Banknote;
import ru.otus.atm.cell.Cell;
import ru.otus.atm.cell.CellRepository;
import ru.otus.atm.exceptions.InvalidTakeAmountException;
import ru.otus.atm.exceptions.NegativeAmountException;
import ru.otus.atm.exceptions.NotEnoughBanknotesException;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public class StandardATM implements ATM {
    protected final CellRepository cellRepository;

    @Override
    public int getCurrentSum() {
        return cellRepository.getSum();
    }

    @Override
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
            var banknote = ATM.closest(sortedValues, remaining).orElse(null);
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

    private static BiFunction<Object, Integer, Integer> INCREMENT_OR_1 = (k, v) -> (v == null) ? 1 : v + 1;
}
