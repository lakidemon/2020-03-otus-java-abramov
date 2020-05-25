package ru.otus.department;

import ru.otus.atm.ATM;
import ru.otus.atm.cell.CellRepository;

public class ControlledATM extends ATM {

    public ControlledATM(CellRepository cellRepository) {
        super(cellRepository);
    }

    public void restore() {

    }
}
