package ru.otus.department.controlled;

import ru.otus.atm.ATM;
import ru.otus.atm.cell.CellRepository;
import ru.otus.department.cell.ProxiedCell;

public class ControlledATM extends ATM {

    public ControlledATM(CellRepository cellRepository) {
        super(cellRepository);
    }

    public void restore() {
        cellRepository.getCells().stream().map(ProxiedCell.class::cast).forEach(ProxiedCell::restore);
    }
}
