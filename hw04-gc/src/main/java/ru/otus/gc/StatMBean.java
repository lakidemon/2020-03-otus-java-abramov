package ru.otus.gc;

public interface StatMBean {

    int getIterations();

    long getElapsedTime();

    int getMemoryPercent();
}
