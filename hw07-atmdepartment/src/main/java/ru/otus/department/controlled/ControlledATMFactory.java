package ru.otus.department.controlled;

import ru.otus.atm.banknote.Ruble;
import ru.otus.atm.cell.standard.StandardCell;
import ru.otus.atm.cell.standard.StandardCellRepository;
import ru.otus.department.cell.ProxiedCell;

import java.util.EnumMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class ControlledATMFactory {

    public static ControlledATM createATMWithRandomContents() {
        var map = new EnumMap<Ruble, Integer>(Ruble.class);
        for (Ruble ruble : Ruble.values()) {
            map.put(ruble, ThreadLocalRandom.current().nextInt(10, 50));
        }
        var repositoryWithProxies = new StandardCellRepository(
                map.entrySet().stream().map(StandardCell::new).map(ProxiedCell::new).collect(Collectors.toList()));
        return new ControlledATM(repositoryWithProxies);
    }

}
