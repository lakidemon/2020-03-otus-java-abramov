package ru.otus;

import lombok.SneakyThrows;

public class SynchronizedNumbers {
    public static final int cycles = 10;
    private volatile Thread lastPrinter;

    public static void main(String[] args) {
        new SynchronizedNumbers().start();
    }

    @SneakyThrows
    public void start() {
        for (int i = 1; i < 3; i++) {
            var thread = new Thread(this::doPrint, "T" + i);
            thread.start();
            thread.join(100);
        }
    }

    private synchronized void doPrint() {
        for (int i = 0; i < cycles; i++) {
            var number = 0;
            for (int c = 0; c < 20; c++) {
                if (c < 10)
                    number++;
                else
                    number--;

                Thread currentThread = Thread.currentThread();
                while (lastPrinter == currentThread) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        currentThread.interrupt();
                    }
                }

                System.out.println(String.format("%s: %d", currentThread.getName(), number));
                lastPrinter = currentThread;

                notifyAll();
            }
        }
    }

}
