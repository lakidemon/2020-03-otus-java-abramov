package ru.otus.department.controlled;

import ru.otus.atm.banknote.Ruble;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

public class ControlledATMFactory {

    public static ControlledATM createATMWithRandomContents() {
        var map = new EnumMap<Ruble, Integer>(Ruble.class);
        for (Ruble ruble : Ruble.values()) {
            map.put(ruble, ThreadLocalRandom.current().nextInt(10, 50));
        }

    }

}
