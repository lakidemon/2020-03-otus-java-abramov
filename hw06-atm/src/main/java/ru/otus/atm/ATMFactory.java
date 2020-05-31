package ru.otus.atm;

import ru.otus.atm.banknote.Banknote;
import ru.otus.atm.banknote.Ruble;
import ru.otus.atm.cell.standard.StandardCellRepository;

import java.util.HashMap;
import java.util.Map;

public class ATMFactory {

    static StandardATM atmForRubles() {
        var banknotes = new HashMap<Banknote, Integer>();
        banknotes.put(Ruble._10, 50);
        banknotes.put(Ruble._1000, 25);
        banknotes.put(Ruble._2000, 20);
        banknotes.put(Ruble._50, 100);
        banknotes.put(Ruble._100, 50);
        banknotes.put(Ruble._5000, 10);
        banknotes.put(Ruble._200, 50);
        banknotes.put(Ruble._500, 50);
        return standardATM(banknotes);
    }

    static StandardATM standardATM(Map<Banknote, Integer> banknotes) {
        return new StandardATM(new StandardCellRepository(banknotes));
    }

}
