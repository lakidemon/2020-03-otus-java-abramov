package ru.otus.atm.cell;

import ru.otus.atm.banknote.Banknote;
import ru.otus.atm.exceptions.IllegalBanknoteException;
import ru.otus.atm.exceptions.NegativeAmountException;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class StandardCellRepository implements CellRepository {
    private final Map<Banknote, Integer> cells = new TreeMap<>(Comparator.comparingInt(Banknote::getValue).reversed());

    public StandardCellRepository(Map<Banknote, Integer> initialContent) {
        this.cells.putAll(initialContent);
    }

    @Override
    public int getSum() {
        return cells.entrySet().stream().mapToInt(entry -> entry.getKey().toMoney(entry.getValue())).sum();
    }

    @Override
    public int getAmount(Banknote banknote) throws IllegalBanknoteException {
        var amount = cells.get(banknote);
        if (amount == null) {
            throw new IllegalBanknoteException(banknote);
        }
        return amount;
    }

    @Override
    public void setAmount(Banknote banknote, int amount) throws IllegalBanknoteException, NegativeAmountException {
        if (amount < 0) {
            throw new NegativeAmountException(amount);
        }
        if (!cells.containsKey(banknote)) {
            throw new IllegalBanknoteException(banknote);
        }
        cells.put(banknote, amount);
    }

    @Override
    public void take(Banknote banknote, int amount) throws IllegalBanknoteException, NegativeAmountException {
        setAmount(banknote, getAmount(banknote) - amount);
    }

    @Override
    public Map<Banknote, Integer> getCellsContent() {
        return Collections.unmodifiableMap(cells);
    }
}
