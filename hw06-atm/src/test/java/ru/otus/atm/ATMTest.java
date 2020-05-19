package ru.otus.atm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.atm.cell.StandardCellRepository;
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
        assertEquals(1, take100.size());
        assertEquals(100, sumMap(take100));
        var take5550 = atm.takeMoney(5550);
        assertEquals(3, take5550.size());
        assertEquals(5550, sumMap(take5550));
        var take100000 = atm.takeMoney(100_000);
        assertEquals(3, take100000.size());
        assertEquals(100_000, sumMap(take100000));
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
            put(10, 0);
            put(50, 0);
        }});
        assertThrows(IllegalBanknoteException.class, () -> repository.setAmount(5, 1));
        assertThrows(IllegalBanknoteException.class, () -> repository.getAmount(5));
        assertDoesNotThrow(() -> repository.getAmount(10));
        assertDoesNotThrow(() -> repository.setAmount(10, 1));
    }

    @Test
    @DisplayName("должен отображать корректный остаток")
    void shouldReturnCorrectSum() {
        assertEquals(160_500, atm.getCurrentSum());
    }

    int sumMap(Map<Integer, Integer> map) {
        return map.entrySet().stream().mapToInt(entry -> entry.getKey() * entry.getValue()).sum();
    }
}
