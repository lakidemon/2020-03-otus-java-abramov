package ru.otus.atm.cell.standard;

import ru.otus.atm.banknote.Banknote;
import ru.otus.atm.cell.Cell;
import ru.otus.atm.cell.CellRepository;
import ru.otus.atm.exceptions.IllegalBanknoteException;
import ru.otus.atm.exceptions.NegativeAmountException;

import java.util.*;

public class StandardCellRepository implements CellRepository {
    private static final Comparator<Cell> CELL_COMPARATOR = Comparator.comparingInt(
            (Cell c) -> c.getBanknote().getValue()).reversed();

    private final Set<Cell> cells = new TreeSet<>(CELL_COMPARATOR);

    public StandardCellRepository(Map<Banknote, Integer> initialContent) {
        initialContent.entrySet().stream().map(StandardCell::new).forEach(cells::add);
    }

    @Override
    public int getSum() {
        return cells.stream().mapToInt(Cell::getSum).sum();
    }

    @Override
    public int getAmount(Banknote banknote) throws IllegalBanknoteException {
        return find(banknote).getAmount();
    }

    @Override
    public void setAmount(Banknote banknote, int amount) throws IllegalBanknoteException, NegativeAmountException {
        find(banknote).setAmount(amount);
    }

    @Override
    public void take(Banknote banknote, int amount) throws IllegalBanknoteException, NegativeAmountException {
        setAmount(banknote, getAmount(banknote) - amount);
    }

    @Override
    public Collection<Cell> getCells() {
        return Collections.unmodifiableCollection(cells);
    }

    private Cell find(Banknote banknote) throws IllegalBanknoteException {
        return cells.stream()
                .filter(c -> Objects.equals(banknote, c.getBanknote()))
                .findFirst()
                .orElseThrow(() -> new IllegalBanknoteException(banknote));
    }
}
