package ru.otus.atm;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.atm.banknote.Banknote;
import ru.otus.atm.banknote.Ruble;
import ru.otus.atm.cell.standard.StandardCellRepository;
import ru.otus.atm.exceptions.IllegalBanknoteException;
import ru.otus.atm.exceptions.InvalidTakeAmountException;
import ru.otus.atm.exceptions.NegativeAmountException;
import ru.otus.atm.exceptions.NotEnoughBanknotesException;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Банкомат")
public class ATMTest {
    private ATM atm;

    @BeforeEach
    void setUp() {
        atm = ATM.atmForRubles();
    }

    @Test
    @DisplayName("должен правильно выдавать деньги минимальным кол-вом купюр")
    void shouldDispenseMoneyCorrectly() {
        var take100 = atm.takeMoney(100);
        assertEquals(1, take100.size(), take100::toString);
        assertEquals(100, sumMap(take100), take100::toString);
        var take5550 = atm.takeMoney(5550);
        assertEquals(3, take5550.size(), take5550::toString);
        assertEquals(5550, sumMap(take5550), take5550::toString);
        var take100000 = atm.takeMoney(100_000);
        assertEquals(3, take100000.size(), take100000::toString);
        assertEquals(100_000, sumMap(take100000), take100000::toString);
    }

    @Test
    @DisplayName("должен падать если запрошена сумма, для которой нет подходящих купюр")
    void shouldFailIfNoSuitableBanknote() {
        assertThrows(InvalidTakeAmountException.class, () -> atm.takeMoney(5));
    }

    @Test
    @DisplayName("должен падать если запрошена сумма <=0")
    void shouldFailIfNegativeAmountRequested() {
        assertThrows(NegativeAmountException.class, () -> atm.takeMoney(-1));
        assertThrows(NegativeAmountException.class, () -> atm.takeMoney(0));
    }

    @Test
    @DisplayName("должен падать если запрошена сумма больше, чем есть на остатке")
    void shouldFailIfRequestedSumIsHigherThanATMHas() {
        assertThrows(NotEnoughBanknotesException.class, () -> atm.takeMoney(200_000));
    }

    @Test
    @DisplayName("должен падать при обращении к ячейке, которой нет")
    void shouldFailIfTryingToAccessCellWhichDoesNotExist() {
        StandardCellRepository repository = new StandardCellRepository(new HashMap<>() {{
            put(Ruble._10, 0);
            put(Ruble._50, 0);
        }});
        var unsupportedOrFake = new Dollar(5);
        assertThrows(IllegalBanknoteException.class, () -> repository.setAmount(unsupportedOrFake, 1));
        assertThrows(IllegalBanknoteException.class, () -> repository.getAmount(unsupportedOrFake));
        assertDoesNotThrow(() -> repository.getAmount(Ruble._10));
        assertDoesNotThrow(() -> repository.setAmount(Ruble._10, 1));
    }

    @Test
    @DisplayName("должен отображать корректный остаток")
    void shouldReturnCorrectSum() {
        assertEquals(160_500, atm.getCurrentSum());
    }

    int sumMap(Map<Banknote, Integer> map) {
        return map.entrySet().stream().mapToInt(entry -> entry.getKey().toMoney(entry.getValue())).sum();
    }

    @RequiredArgsConstructor
    @Getter
    private class Dollar implements Banknote {
        private final int value;
    }
}
