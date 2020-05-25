package ru.otus.department;

import ru.otus.department.controlled.ControlledATM;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Department implements Restorable {
    private List<ControlledATM> atms = new ArrayList<>();

    public int getTotalSum() {
        return atms.stream().mapToInt(ControlledATM::getCurrentSum).sum();
    }

    public void addATM(ControlledATM atm) {
        atms.add(atm);
    }

    public void removeATM(ControlledATM atm) {
        atms.remove(atm);
    }

    @Override
    public void restore() {
        atms.forEach(ControlledATM::restore);
    }

    public Collection<ControlledATM> getATMs() {
        return Collections.unmodifiableCollection(atms);
    }
}
