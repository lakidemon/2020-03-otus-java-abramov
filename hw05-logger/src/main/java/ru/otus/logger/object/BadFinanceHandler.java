package ru.otus.logger.object;

import ru.otus.logger.Log;

public class BadFinanceHandler {

    @Log
    public void loggableMethod(String arg1, int arg2) {
        System.out.println("User: " + arg1 + ", income: " + arg2);
    }

    public void notLoggableMethod(String arg1, int arg2) {
        System.out.println("User: " + arg1 + ", loss: " + arg2);
    }

}
