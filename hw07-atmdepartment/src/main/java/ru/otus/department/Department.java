package ru.otus.department;

import java.util.List;

public class Department {
    private List<ControlledATM> atms;

    public int getTotalSum() {
        return atms.stream().mapToInt(ControlledATM::getCurrentSum).sum();
    }


}
