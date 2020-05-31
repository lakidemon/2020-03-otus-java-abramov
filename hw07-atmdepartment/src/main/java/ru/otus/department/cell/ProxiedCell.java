package ru.otus.department.cell;

import ru.otus.atm.banknote.Banknote;
import ru.otus.atm.cell.Cell;
import ru.otus.atm.exceptions.NegativeAmountException;
import ru.otus.department.Restorable;

public class ProxiedCell implements Cell, Restorable {
    private Cell cell;
    private StateHolder stateHolder;

    public ProxiedCell(Cell cell) {
        this.cell = cell;
        this.stateHolder = new StateHolder(this);
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
        stateHolder.saveState(this);
    }

    @Override
    public void restore() {
        int initialState = stateHolder.getInitialState().getCurrentAmount();
        stateHolder.reset();
        setAmount(initialState);
    }
}
