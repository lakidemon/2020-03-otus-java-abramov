package ru.otus.gc;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Stat implements StatMBean {
    private int iterations = 0;
    private long elapsedTime = 0;
    private int memoryPercent;

    public void incrementIterations() {
        iterations++;
    }
}
