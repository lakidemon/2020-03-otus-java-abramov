package ru.otus.department.controlled;

import lombok.RequiredArgsConstructor;
import ru.otus.atm.ATM;
import ru.otus.atm.banknote.Banknote;
import ru.otus.atm.cell.CellRepository;
import ru.otus.atm.exceptions.InvalidTakeAmountException;
import ru.otus.atm.exceptions.NegativeAmountException;
import ru.otus.department.Restorable;
import ru.otus.department.cell.ProxiedCell;

import java.util.Map;

@RequiredArgsConstructor
public class ControlledATM implements ATM, Restorable {
    private final ATM atm;

    @Override
    public void restore() {
        getCellRepository().getCells().stream().map(ProxiedCell.class::cast).forEach(ProxiedCell::restore);
    }

    @Override
    public CellRepository getCellRepository() {
        return atm.getCellRepository();
    }

    @Override
    public int getCurrentSum() {
        return atm.getCurrentSum();
    }

    @Override
    public Map<Banknote, Integer> takeMoney(int sum) throws InvalidTakeAmountException, NegativeAmountException {
        return atm.takeMoney(sum);
    }
}
