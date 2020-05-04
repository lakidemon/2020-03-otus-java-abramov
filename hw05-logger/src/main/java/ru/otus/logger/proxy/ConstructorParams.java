package ru.otus.logger.proxy;

import lombok.Data;

@Data
public class ConstructorParams {
    private final Class[] parameterTypes;
    private final Object[] parameterValues;
}
