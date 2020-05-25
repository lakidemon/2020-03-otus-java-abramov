package ru.otus.department;

import ru.otus.department.controlled.ControlledATM;

import java.util.ArrayList;
import java.util.List;

public class Department {
    private List<ControlledATM> atms = new ArrayList<>();

    public int getTotalSum() {
        return atms.stream().mapToInt(ControlledATM::getCurrentSum).sum();
    }

    public void addAtm(ControlledATM atm) {
        atms.add(atm);
    }

    public void removeAtm(ControlledATM atm) {
        atms.remove(atm);
    }
}
