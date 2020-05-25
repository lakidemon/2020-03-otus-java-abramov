package ru.otus.department;

import ru.otus.atm.banknote.Banknote;
import ru.otus.atm.cell.Cell;
import ru.otus.atm.exceptions.NegativeAmountException;

public class ProxiedCell implements Cell, Restorable {
    private Cell cell;
    private StateHolder stateHolder;

    public ProxiedCell(Cell cell) {
        this.cell = cell;
        this.stateHolder = new StateHolder(cell.getAmount());
    }

    @Override
    public Banknote getBanknote() {
        return cell.getBanknote();
    }

    @Override
    public int getAmount() {
        return cell.getAmount();
    }

    @Override
    public void setAmount(int amount) throws NegativeAmountException {
        cell.setAmount(amount);
        stateHolder.saveState(amount);
    }

    @Override
    public void restore() {
        int initialState = stateHolder.getInitialState();
        stateHolder.reset();
        setAmount(initialState);
    }
}
