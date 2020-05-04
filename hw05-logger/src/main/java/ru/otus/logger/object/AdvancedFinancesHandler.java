package ru.otus.logger.object;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import ru.otus.logger.Log;

import java.math.BigDecimal;

@RequiredArgsConstructor
@ToString
public class AdvancedFinancesHandler implements LoggableHandler {
    private final String someField1;
    private final BigDecimal someField2;

    @Log
    @Override
    public void loggableMethod(String arg1, int arg2) {
        System.out.println("User: " + arg1 + ", income: " + arg2);
    }

    @Override
    public void notLoggableMethod(String arg1, int arg2) {
        System.out.println("User: " + arg1 + ", loss: " + arg2);
    }

}
