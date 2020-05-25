package ru.otus.department;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayDeque;
import java.util.Deque;

public class StateHolder {
    private Deque<State> stack = new ArrayDeque<>();

    public StateHolder(int initial) {
        saveState(initial);
    }

    public void saveState(int current) {
        stack.push(new State(current));
    }

    public int getInitialState() {
        return stack.getLast().getCurrent();
    }

    public void reset() {
        stack.clear();
    }

    @AllArgsConstructor
    @Getter
    public class State {
        private int current;
    }
}
