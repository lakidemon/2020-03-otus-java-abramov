package ru.otus.department;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.otus.atm.ATM;
import ru.otus.atm.banknote.Banknote;
import ru.otus.atm.cell.Cell;
import ru.otus.atm.cell.CellRepository;
import ru.otus.department.controlled.ControlledATM;
import ru.otus.department.controlled.ControlledATMFactory;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class DepartmentTest {
    private Department department;

    @BeforeEach
    void setUp() {
        department = new Department();
        IntStream.range(0, 10)
                .mapToObj(i -> ControlledATMFactory.createATMWithRandomContents())
                .forEach(department::addATM);
    }

    @Test
    void shouldReturnCorrectTotalSum() {
        var manuallyCalculated = department.getATMs()
                .stream()
                .map(ControlledATM::getCellRepository)
                .map(CellRepository::getCells)
                .flatMap(Collection::stream)
                .mapToInt(Cell::getSum)
                .sum();

        assertEquals(manuallyCalculated, department.getTotalSum());
    }

    @Test
    void shouldRestoreProperly() {
        var beforeRestore = mapDepartment(department);
        department.getATMs().forEach(atm -> atm.takeMoney(60_000));
        department.restore();
        assertEquals(beforeRestore, mapDepartment(department));
    }

    private Map<ATM, Map<Banknote, Integer>> mapDepartment(Department department) {
        return department.getATMs()
                .stream()
                .collect(Collectors.toMap(Function.identity(), atm -> atm.getCellRepository()
                        .getCells()
                        .stream()
                        .collect(Collectors.toMap(Cell::getBanknote, Cell::getAmount))));
    }
}
