package ru.otus.atm.cell.standard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.otus.atm.banknote.Banknote;
import ru.otus.atm.cell.Cell;
import ru.otus.atm.exceptions.NegativeAmountException;

import java.util.Map;

@Getter
@AllArgsConstructor
public class StandardCell implements Cell {
    private final Banknote banknote;
    private int amount;

    public StandardCell(Map.Entry<Banknote, Integer> initialContent) {
        this(initialContent.getKey(), initialContent.getValue());
    }

    @Override
    public void setAmount(int amount) throws NegativeAmountException {
        if (amount < 0) {
            throw new NegativeAmountException(amount);
        }
        this.amount = amount;
    }

}
