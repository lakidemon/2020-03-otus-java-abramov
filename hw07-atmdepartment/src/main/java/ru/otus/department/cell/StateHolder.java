package ru.otus.department.cell;

import lombok.Getter;
import ru.otus.atm.cell.Cell;

import java.util.ArrayDeque;
import java.util.Deque;

public class StateHolder {
    private Deque<CellState> stack = new ArrayDeque<>();

    public StateHolder(Cell cell) {
        saveState(cell);
    }

    public void saveState(Cell cell) {
        stack.push(new CellState(cell));
    }

    public CellState getInitialState() {
        return stack.getLast();
    }

    public void reset() {
        stack.clear();
    }

    @Getter
    public class CellState {
        private int currentAmount;

        public CellState(Cell cell) {
            this.currentAmount = cell.getAmount();
        }
    }
}
