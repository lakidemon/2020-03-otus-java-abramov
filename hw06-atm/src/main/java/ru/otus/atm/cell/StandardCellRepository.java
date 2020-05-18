package ru.otus.atm.cell;

import ru.otus.atm.exceptions.IllegalBanknoteException;
import ru.otus.atm.exceptions.NegativeAmountException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class StandardCellRepository implements CellRepository {
    private final Map<Integer, Integer> cells = new HashMap<>();

    public StandardCellRepository(Map<Integer, Integer> initialContent) {
        this.cells.putAll(initialContent);
    }

    @Override
    public int getSum() {
        return cells.entrySet().stream().mapToInt(entry -> entry.getKey() * entry.getValue()).sum();
    }

    @Override
    public int getAmount(int value) throws IllegalBanknoteException {
        var amount = cells.get(value);
        if (amount == null) {
            throw new IllegalBanknoteException(value);
        }
        return amount;
    }

    @Override
    public void setAmount(int value, int amount) throws IllegalBanknoteException, NegativeAmountException {
        if (amount < 0) {
            throw new NegativeAmountException(amount);
        }
        if (!cells.containsKey(value)) {
            throw new IllegalBanknoteException(value);
        }
        cells.put(value, amount);
    }

    @Override
    public Map<Integer, Integer> getCellsContent() {
        return Collections.unmodifiableMap(cells);
    }
}
